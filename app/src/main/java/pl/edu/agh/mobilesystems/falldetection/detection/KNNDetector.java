package pl.edu.agh.mobilesystems.falldetection.detection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kotlin.Triple;
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerData;

public class KNNDetector {

    int QUEUE_LIMIT = 100;
    int N_NEIGHBORS = 3;
    int WINDOW_SIZE = 4;
    public final static int FALL_THRESHOLD = 20;

    LimitedQueue<AccelerometerData> limitedQueue;

    public KNNDetector() {
        this.limitedQueue = new LimitedQueue<>(QUEUE_LIMIT);
    }

    public double newData(@NotNull AccelerometerData coordinates) {

        this.limitedQueue.addFirst(coordinates);

        if (this.limitedQueue.size() == QUEUE_LIMIT){
            return this.calculateKNN();
        }
        else {
            return  0;
        }

    }

    private double calculateKNN() {
        List<Double> distances = new ArrayList<>();
        double x = 0.0, y = 0.0, z = 0.0;
        AccelerometerData element;
        Iterator iterator = limitedQueue.iterator();
        for(int i = 0; i < WINDOW_SIZE; i++){
            element = (AccelerometerData) iterator.next();
            x += element.getXCoordinate();
            y += element.getYCoordinate();
            z += element.getZCoordinate();
        }
        Triple<Double, Double, Double> newElement = new Triple<>(x, y, z);

        for (int i = WINDOW_SIZE; i < QUEUE_LIMIT; i += WINDOW_SIZE) {
            x = 0.0; y = 0.0; z = 0.0;
            for (int j = 0; j < WINDOW_SIZE; j++){
                element = (AccelerometerData) iterator.next();
                x += element.getXCoordinate();
                y += element.getYCoordinate();
                z += element.getZCoordinate();
            }
            distances.add(this.countDistanceVertical(newElement, new Triple<>(x, y, z)));
        }

        return distances.stream().sorted().limit(N_NEIGHBORS).mapToDouble(Double::doubleValue).sum();
    }

    private Double countEuclideanDistance(Triple<Double, Double, Double> newElement,
                                          Triple<Double, Double, Double> elem) {
        return Math.sqrt(
                Math.pow(newElement.getFirst() - elem.getFirst(), 2) +
                        Math.pow(newElement.getSecond() - elem.getSecond(), 2) +
                        Math.pow(newElement.getThird() - elem.getThird(), 2)
        );

    }

    private Double countDistanceVertical(Triple<Double, Double, Double> newElement,
                                         Triple<Double, Double, Double> elem) {
        return Math.abs(newElement.getSecond() - elem.getSecond());
    }
}
