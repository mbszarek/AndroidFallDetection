package pl.edu.agh.mobilesystems.falldetection.accelerometer

import java.util.concurrent.atomic.AtomicReference

object AccelerometerDataStore {
    private val accelerometerData: AtomicReference<AccelerometerData> =
        AtomicReference(AccelerometerData.withInitialValues())

    private val distance: AtomicReference<Double> = AtomicReference(0.0)

    fun getAccelerometerData(): AccelerometerData = accelerometerData.get()

    fun setAccelerometerData(accelerometerData: AccelerometerData) {
        this.accelerometerData.set(accelerometerData)
    }

    fun getDistance(): Double = distance.get()

    fun setDistance(newDistance: Double) {
        distance.set(newDistance)
    }

}