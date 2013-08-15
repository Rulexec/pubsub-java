package by.muna.pubsub.test;

import by.muna.pubsub.IControllable;
import by.muna.pubsub.PubSub;
import by.muna.pubsub.PubSubEventRunner;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BasicTest {
    private static interface TestEvent {
        void onEvent(int a);
    }

    @Test
    public void basicTest() {
        PubSub<TestEvent> pubsub = new PubSub<TestEvent>();

        final List<Integer> actual = new ArrayList<Integer>(8);

        TestEvent eventListener = new TestEvent() {
            @Override
            public void onEvent(int a) {
                actual.add(a);
            }
        };

        IControllable listener1 = pubsub.subscribe(eventListener);

        // adds 1
        pubsub.dispatch(this.createTestEventRunner(1), true);

        IControllable listener2 = pubsub.subscribe(eventListener);

        // adds 2, 2
        pubsub.dispatch(this.createTestEventRunner(2), true);

        listener2.pause();

        // adds 3, 4
        pubsub.dispatch(this.createTestEventRunner(3), true);
        pubsub.dispatch(this.createTestEventRunner(4), false);

        // adds 3
        listener2.resume();

        pubsub.setOnEmptyListener(new PubSub.PubSubEmptyListener() {
            @Override
            public void onPubSubIsEmpty() {
                actual.add(1000);
            }
        });

        listener1.cancel();

        // adds 5
        pubsub.dispatch(this.createTestEventRunner(5), true);

        // adds 1000
        listener2.cancel();

        // noop, all cancelled
        pubsub.dispatch(this.createTestEventRunner(0), true);

        List<Integer> expected = Arrays.asList(1, 2, 2, 3, 4, 3, 5, 1000);

        // because strings is can be viewed
        Assert.assertEquals(
            this.listToString(expected),
            this.listToString(actual)
        );
    }

    private PubSubEventRunner<TestEvent> createTestEventRunner(final int a) {
        return new PubSubEventRunner<TestEvent>() {
            @Override
            public void run(TestEvent listener) {
                listener.onEvent(a);
            }
        };
    }

    private String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;

        for (Object value : list) {
            if (!isFirst) sb.append(' ');
            else isFirst = false;

            sb.append(value.toString());
        }

        return sb.toString();
    }
}
