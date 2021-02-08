package com.eventstore.dbclient;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SingleNodeClient implements GrpcClient {
    private final String host;
    private final int port;
    private final ManagedChannel channel;
    private final SslContext context;
    private final Timeouts timeouts;
    private final boolean keepAlive;

    public SingleNodeClient(String host, int port, Timeouts timeouts, SslContext context, boolean keepAlive) {
        this.host = host;
        this.port = port;
        this.context = context;
        this.timeouts = timeouts;
        this.channel = createChannel();
        this.keepAlive = keepAlive;
    }

    private ManagedChannel createChannel() {
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(this.host, this.port);

        if (this.context == null) {
            builder.usePlaintext();
        } else {
            builder.sslContext(context);
        }

        return builder.keepAliveWithoutCalls(keepAlive).build();
    }

    @Override
    public <A> CompletableFuture<A> run(Function<ManagedChannel, CompletableFuture<A>> action) {
        return action.apply(this.channel);
    }

    @Override
    public void shutdown() throws InterruptedException {
        this.channel.shutdown().awaitTermination(this.timeouts.shutdownTimeout, this.timeouts.shutdownTimeoutUnit);
    }
}
