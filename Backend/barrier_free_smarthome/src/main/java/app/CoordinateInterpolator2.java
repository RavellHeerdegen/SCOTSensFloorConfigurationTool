package app;

import app.models.CarpetCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CoordinateInterpolator2 {

    Logger logger = LoggerFactory.getLogger(CoordinateInterpolator2.class);

    private Queue<QueueItemWithTimestamp<CarpetCoordinate>> queue = new ConcurrentLinkedQueue<QueueItemWithTimestamp<CarpetCoordinate>>();
    private long timeStep;
    private CarpetCoordinate virtualCoordinate;
    private Timer timer = new Timer();

    public CoordinateInterpolator2(long timeStepMillis){
        if (timeStepMillis >= 10) {
            this.timeStep = timeStepMillis;
            logger.debug("Created CoordinateInterpolator with time step {}", timeStepMillis);
        } else throw new IllegalArgumentException("timeStepMillis must be larger than 10");

    }

    /**
     * What to do with interpolated CarpetCoordinate?
     *
     * @param c
     */
    public abstract void onInterpolatedCoordinate(CarpetCoordinate c);

    public void start(){
        logger.debug("Starting CoordinateInterpolator...");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onTimeStep();
            }
        }, 0, this.timeStep);
    }

    public void stop() {
        logger.debug("Stopping CoordinateInterpolator...");
        this.timer.cancel();
    }

    public boolean addCoordinate(CarpetCoordinate c){
        QueueItemWithTimestamp<CarpetCoordinate> item = new QueueItemWithTimestamp<CarpetCoordinate>(c, System.currentTimeMillis() );
        if (this.queue.offer(item)) {
            onAddCoordinate();
            return true;
        }
        return false;
    }

    /**
     * Triggered when a CarpetCoordinate is added. Does nothing by now but can be overridden.
     */
    private void onAddCoordinate(){
        // Can be overridden
    }

    private void onTimeStep(){
        logger.trace("OnTimeStep called. {} coordinates in queue.", this.queue.size());
        if (this.queue.isEmpty()) {
            logger.debug("Nothing to do: No coordinates in queue.");
            return;
        }
        // Copy current queue to work on it
        Queue<QueueItemWithTimestamp<CarpetCoordinate>> currentQueue = new ConcurrentLinkedQueue<QueueItemWithTimestamp<CarpetCoordinate>>(this.queue);
        // Clear the queue on this class while working on the copied values
        this.queue.clear();

        // Convert to a list
        List<CarpetCoordinate> carpetCoordinates = new LinkedList<CarpetCoordinate>();
        for (QueueItemWithTimestamp<CarpetCoordinate> q : currentQueue) {
            carpetCoordinates.add(q.item);
        }
        // Calculate interpolated value
        CarpetCoordinate interpolatedCoordinate = CarpetCoordinateMaths.leastSumOfDistances(carpetCoordinates);
        logger.debug("---- Interpolated coordinate is: {}, {}", interpolatedCoordinate.x, interpolatedCoordinate.y);

        this.onInterpolatedCoordinate(interpolatedCoordinate);
    }
}
