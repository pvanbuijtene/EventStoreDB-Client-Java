package com.eventstore.dbclient;

public class EventStoreDBClientSettings {
    private boolean dnsDiscover;
    private int maxDiscoverAttempts;
    private int discoveryInterval;
    private int gossipTimeout;
    private NodePreference nodePreference;
    private boolean tls;
    private boolean tlsVerifyCert;
    private boolean throwOnAppendFailure;
    private Credentials defaultCredentials;
    private Endpoint[] hosts;
    private boolean keepAlive;

    public boolean isDnsDiscover() {
        return dnsDiscover;
    }

    public int getMaxDiscoverAttempts() {
        return maxDiscoverAttempts;
    }

    public int getDiscoveryInterval() {
        return discoveryInterval;
    }

    public int getGossipTimeout() {
        return gossipTimeout;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public boolean isTls() {
        return tls;
    }

    public boolean isTlsVerifyCert() {
        return tlsVerifyCert;
    }

    public boolean isThrowOnAppendFailure() {
        return throwOnAppendFailure;
    }

    public Credentials getDefaultCredentials() {
        return defaultCredentials;
    }

    public Endpoint[] getHosts() {
        return hosts;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }


    public EventStoreDBClientSettings(
            boolean dnsDiscover,
            int maxDiscoverAttempts,
            int discoveryInterval,
            int gossipTimeout,
            NodePreference nodePreference,
            boolean tls,
            boolean tlsVerifyCert,
            boolean throwOnAppendFailure,
            Credentials defaultCredentials,
            Endpoint[] hosts,
            boolean keepAlive
    ) {
        this.dnsDiscover = dnsDiscover;
        this.maxDiscoverAttempts = maxDiscoverAttempts;
        this.discoveryInterval = discoveryInterval;
        this.gossipTimeout = gossipTimeout;
        this.nodePreference = nodePreference;
        this.tls = tls;
        this.tlsVerifyCert = tlsVerifyCert;
        this.throwOnAppendFailure = throwOnAppendFailure;
        this.defaultCredentials = defaultCredentials;
        this.hosts = hosts;
        this.keepAlive = keepAlive;
    }

    public static ConnectionSettingsBuilder builder() {
        return new ConnectionSettingsBuilder();
    }

    protected static class Credentials {
        public String login;
        public String password;

        public Credentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public UserCredentials toUserCredentials() {
            return new UserCredentials(login, password);
        }
    }
}
