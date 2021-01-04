package com.eventstore.dbclient;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class Streams {
    private final GrpcClient client;
    private final UserCredentials credentials;

    public Streams(GrpcClient client, UserCredentials credentials) {
        this.client = client;
        this.credentials = credentials;
    }

    public CompletableFuture<WriteResult> appendToStream(String streamName, EventData... events) {
        return this.appendToStream(streamName, Arrays.stream(events).iterator());
    }

    public CompletableFuture<WriteResult> appendToStream(String streamName, Iterator<EventData> events) {
        return this.appendToStream(streamName, AppendToStreamOptions.get(), events);
    }

    public CompletableFuture<WriteResult> appendToStream(String streamName, AppendToStreamOptions options, EventData... events) {
        return this.appendToStream(streamName, options, Arrays.stream(events).iterator());
    }

    public CompletableFuture<WriteResult> appendToStream(String streamName, AppendToStreamOptions options, Iterator<EventData> events) {
        if (options == null)
            options = AppendToStreamOptions.get();

        if (!options.hasUserCredentials())
            options.authenticated(this.credentials);

        return new AppendToStream(this.client, streamName, events, options).execute();
    }

    public CompletableFuture<ReadResult> readStream(String streamName) {
        return this.readStream(streamName, Long.MAX_VALUE, ReadStreamOptions.get());
    }

    public CompletableFuture<ReadResult> readStream(String streamName, long maxCount) {
        return this.readStream(streamName, maxCount, ReadStreamOptions.get());
    }

    public CompletableFuture<ReadResult> readStream(String streamName, ReadStreamOptions options) {
        return this.readStream(streamName, Long.MAX_VALUE, ReadStreamOptions.get());
    }

    public CompletableFuture<ReadResult> readStream(String streamName, long maxCount, ReadStreamOptions options) {
        if (options == null)
            options = ReadStreamOptions.get();

        if (!options.hasUserCredentials())
            options.authenticated(this.credentials);

        return new ReadStream(this.client, streamName, maxCount, options).execute();
    }

    public CompletableFuture<ReadResult> readAll() {
        return this.readAll(Long.MAX_VALUE, ReadAllOptions.get());
    }

    public CompletableFuture<ReadResult> readAll(long maxCount) {
        return this.readAll(maxCount, ReadAllOptions.get());
    }

    public CompletableFuture<ReadResult> readAll(ReadAllOptions options) {
        return this.readAll(Long.MAX_VALUE, options);
    }

    public CompletableFuture<ReadResult> readAll(long maxCount, ReadAllOptions options) {
        if (options == null)
            options = ReadAllOptions.get();

        if (!options.hasUserCredentials())
            options.authenticated(this.credentials);

        return new ReadAll(this.client, maxCount, options).execute();
    }

    public SubscribeToStream subscribeToStream(String streamName, SubscriptionListener listener) {
        return this.subscribeToStream(streamName, listener, SubscribeToStreamOptions.get());
    }

    public SubscribeToStream subscribeToStream(String streamName, SubscriptionListener listener, SubscribeToStreamOptions options) {
        if (options == null)
            options = SubscribeToStreamOptions.get();

        if (!options.hasUserCredentials())
            options.authenticated(this.credentials);

        return new SubscribeToStream(this.client, streamName, listener, options);
    }

    public SubscribeToAll subscribeToAll(SubscriptionListener listener) {
        return this.subscribeToAll(listener, SubscribeToAllOptions.get());
    }

    public SubscribeToAll subscribeToAll(SubscriptionListener listener, SubscribeToAllOptions options) {
        if (options == null)
            options = SubscribeToAllOptions.get();

        if (!options.hasUserCredentials())
            options.authenticated(this.credentials);

        return new SubscribeToAll(this.client, listener, options);
    }

    public DeleteStream deleteStream(String streamName) {
        return new DeleteStream(this.client, streamName, this.credentials);
    }
}
