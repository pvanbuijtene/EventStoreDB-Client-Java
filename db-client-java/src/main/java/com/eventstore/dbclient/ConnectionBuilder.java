package com.eventstore.dbclient;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ConnectionBuilder {
    private Timeouts _timeouts;
    private SslContext _sslContext = null;
    private boolean _keepAlive = false;

    public ConnectionBuilder() {
        _timeouts = Timeouts.DEFAULT;
    }

    public ConnectionBuilder connectionTimeouts(Timeouts timeouts) {
        _timeouts = timeouts;
        return this;
    }

    public ConnectionBuilder sslContext(SslContext context) {
        _sslContext = context;
        return this;
    }

    public ConnectionBuilder keepAlive(boolean value) {
        _keepAlive = value;
        return this;
    }

    public GrpcClient createSingleNodeConnection(Endpoint endpoint) {
        return new SingleNodeClient(endpoint.getHostname(), endpoint.getPort(), _timeouts, _sslContext, _keepAlive);
    }

    public GrpcClient createSingleNodeConnection(String hostname, int port) {
        return createSingleNodeConnection(new Endpoint(hostname, port));
    }

    public GrpcClient createClusterConnectionUsingSeeds(Endpoint[] endpoints) {
        return createClusterConnectionUsingSeeds(endpoints, NodePreference.RANDOM);
    }

    public GrpcClient createClusterConnectionUsingSeeds(Endpoint[] endpoints, NodePreference nodePreference) {
        ArrayList<InetSocketAddress> addresses = new ArrayList<>();

        for (int i = 0; i < endpoints.length; ++i) {
            Endpoint seed = endpoints[i];
            InetSocketAddress address = new InetSocketAddress(seed.getHostname(), seed.getPort());

            addresses.add(address);
        }

        return new EventStoreDBClusterClient(addresses, null, nodePreference, _timeouts, _sslContext, _keepAlive);
    }

    public GrpcClient createClusterConnectionUsingDns(Endpoint endpoint) {
        return createClusterConnectionUsingDns(endpoint, NodePreference.RANDOM);
    }

    public GrpcClient createClusterConnectionUsingDns(Endpoint endpoint, NodePreference nodePreference) {
        return new EventStoreDBClusterClient(null, endpoint, nodePreference, _timeouts, _sslContext, false);
    }

    public GrpcClient createConnectionFromConnectionSettings(EventStoreDBClientSettings clientSettings) {

        ConnectionBuilder builder = new ConnectionBuilder();

        builder = builder.keepAlive(clientSettings.isKeepAlive());

        if (clientSettings.isTls()) {
            try {
                SslContextBuilder sslContext = GrpcSslContexts.forClient();

                if (!clientSettings.isTlsVerifyCert()) {
                    sslContext.trustManager(InsecureTrustManagerFactory.INSTANCE);
                }

                builder.sslContext(sslContext.build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

        if (clientSettings.isDnsDiscover()) {
            return builder.createClusterConnectionUsingDns(clientSettings.getHosts()[0], clientSettings.getNodePreference());
        }

        if (clientSettings.getHosts().length > 1) {
            return builder.createClusterConnectionUsingSeeds(
                    clientSettings.getHosts(),
                    clientSettings.getNodePreference()
            );
        }

        return builder.createSingleNodeConnection(clientSettings.getHosts()[0]);
    }
}
