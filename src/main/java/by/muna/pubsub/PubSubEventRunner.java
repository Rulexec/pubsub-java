package by.muna.pubsub;

public interface PubSubEventRunner<T> {
    void run(T listener);
}
