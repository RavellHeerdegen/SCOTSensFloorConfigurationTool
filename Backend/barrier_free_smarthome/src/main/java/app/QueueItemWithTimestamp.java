package app;

public class QueueItemWithTimestamp<E>{
    public long timestamp;
    public E item;

    public QueueItemWithTimestamp(E item, long timestamp) {
        this.item = item;
        this.timestamp = timestamp;
    }
}
