package com.eventstore.dbclient;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SubscribePersistentSubcription extends PersistenSubscriptionTestsBase {
    class Foo {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubscribePersistentSubcription.Foo foo1 = (SubscribePersistentSubcription.Foo) o;
            return foo == foo1.foo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }

        private boolean foo;

        public boolean isFoo() {
            return foo;
        }

        public void setFoo(boolean foo) {
            this.foo = foo;
        }
    }

    @Test
    public void testSubscribePersistentSub() throws Throwable {
        EventStoreDBClient streamsClient = server.getClient();
        String streamName = "aStream-" + UUID.randomUUID().toString();

        client.create(streamName, "aGroup")
                .get();

        EventDataBuilder builder = EventData.builderAsJson("foobar", new SubscribePersistentSubcription.Foo());

        streamsClient.appendToStream(streamName, builder.build(), builder.build(), builder.build())
                .get();

        final CompletableFuture<Integer> result = new CompletableFuture<>();

        SubscribePersistentSubscriptionOptions connectOptions = SubscribePersistentSubscriptionOptions.get()
                .setBufferSize(32);

        client.subscribe(streamName, "aGroup", connectOptions, new PersistentSubscriptionListener() {
            private int count = 0;

            @Override
            public void onEvent(PersistentSubscription subscription, ResolvedEvent event) {
                ++this.count;

                subscription.ack(event);

                if (this.count == 6) {
                    result.complete(this.count);
                    subscription.stop();
                }
            }

            @Override
            public void onError(PersistentSubscription subscription, Throwable throwable) {
                result.completeExceptionally(throwable);
            }

            @Override
            public void onCancelled(PersistentSubscription subscription) {
                result.complete(count);
            }
        }).get();

        streamsClient.appendToStream(streamName, builder.build(), builder.build(), builder.build())
                .get();

        Assert.assertEquals(6, result.get().intValue());
    }
}
