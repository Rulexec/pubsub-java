package by.muna.pubsub;

import java.util.LinkedList;
import java.util.Queue;

public class PubSubListener<T> implements IControllable {
    private PubSub<T> pubsub;
    private T listener;

    private boolean paused = false;
    private boolean cancelled = false;

    private Queue<PubSubEventRunner<T>> deferredRunners = new LinkedList<PubSubEventRunner<T>>();

    PubSubListener(PubSub<T> pubsub, T listener) {
        this.pubsub = pubsub;
        this.listener = listener;
    }

    boolean dispatch(PubSubEventRunner<T> runner, boolean allowDeferred) {
        if (this.cancelled) return false;

        synchronized (this) {
            if (this.cancelled) return false;

            if (!this.paused) {
                runner.run(this.listener);
            } else {
                if (allowDeferred) {
                    this.deferredRunners.add(runner);
                }
            }
        }

        return true;
    }

    @Override
    public void pause() {
        synchronized (this) {
            this.paused = true;
        }
    }
    @Override
    public void resume() {
        synchronized (this) {
            this.paused = false;

            while (!this.deferredRunners.isEmpty()) {
                this.deferredRunners.poll().run(this.listener);
            }
        }
    }

    @Override
    public void cancel() {
        if (this.cancelled) return;

        synchronized (this) {
            if (this.cancelled) return;

            this.cancelled = true;
        }

        this.pubsub.cancel(this);

        this.pubsub = null;
        this.deferredRunners = null;
    }
}
