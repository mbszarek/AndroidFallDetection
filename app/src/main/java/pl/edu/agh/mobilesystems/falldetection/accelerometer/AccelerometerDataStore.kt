package pl.edu.agh.mobilesystems.falldetection.accelerometer

import java.util.concurrent.atomic.AtomicReference

object AccelerometerDataStore {
    private val accelerometerData: AtomicReference<AccelerometerData> =
        AtomicReference(AccelerometerData.withInitialValues())

    fun getAccelerometerData(): AccelerometerData = accelerometerData.get()

    fun setAccelerometerData(accelerometerData: AccelerometerData) {
        this.accelerometerData.set(accelerometerData)
    }

}