package pl.edu.agh.mobilesystems.falldetection.detection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerData;

public class KNNDetector {

    int QUEUE_LIMIT = 100;
    int N_NEIGHBORS = 5;
    public int FALL_THRESHOLD = 20;

    LimitedQueue<AccelerometerData> limitedQueue;

    public KNNDetector() {
        this.limitedQueue = new LimitedQueue<>(QUEUE_LIMIT);
    }

    public double newData(@NotNull AccelerometerData coordinates) {

        double distance = this.calculateKNN(coordinates);
        this.limitedQueue.add(coordinates);

        return distance;
    }

    private double calculateKNN(AccelerometerData newElement) {
        List<Double> distances = new ArrayList<>();
        for (AccelerometerData elem : this.limitedQueue) {
            distances.add(this.countEuclideanDistance(newElement, elem));
        }

        return distances.stream().sorted().limit(N_NEIGHBORS).mapToDouble(Double::doubleValue).sum();
    }

    private Double countEuclideanDistance(AccelerometerData newElement, AccelerometerData elem) {
        return Math.sqrt(
                Math.pow(newElement.getXCoordinate() - elem.getXCoordinate(), 2) +
                        Math.pow(newElement.getYCoordinate() - elem.getYCoordinate(), 2) +
                        Math.pow(newElement.getZCoordinate() - elem.getZCoordinate(), 2)
        );

    }
}
