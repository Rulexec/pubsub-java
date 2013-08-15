package by.muna.pubsub;

import java.util.*;

public class PubSub<T> {
    public static interface PubSubEmptyListener {
        void onPubSubIsEmpty();
    }

    private Queue<PubSubListener<T>> listeners = new LinkedList<PubSubListener<T>>();

    private int count = 0;

    private PubSubEmptyListener emptyListener;

    public PubSub() {}

    public void setOnEmptyListener(PubSubEmptyListener listener) {
        this.emptyListener = listener;
    }

    public PubSubListener<T> subscribe(T listener) {
        PubSubListener<T> pubsubListener = new PubSubListener<T>(this, listener);

        this.listeners.add(pubsubListener);
        this.count++;

        return pubsubListener;
    }

    public void dispatch(PubSubEventRunner<T> runner, boolean allowDeferred) {
        Iterator<PubSubListener<T>> listeners = this.listeners.iterator();

        while (listeners.hasNext()) {
            boolean runned = listeners.next().dispatch(runner, allowDeferred);

            if (!runned) {
                listeners.remove();
            }
        }
    }

    void cancel(PubSubListener<T> listener) {
        this.count--;

        if (this.count == 0 && this.emptyListener != null) {
            this.emptyListener.onPubSubIsEmpty();
        }
    }
}
