package com.eventstore.dbclient;

public class PersistentSubscriptions {
    private final GrpcClient client;
    private final UserCredentials credentials;

    public PersistentSubscriptions(GrpcClient client, UserCredentials credentials) {
        this.client = client;
        this.credentials = credentials;
    }
}
