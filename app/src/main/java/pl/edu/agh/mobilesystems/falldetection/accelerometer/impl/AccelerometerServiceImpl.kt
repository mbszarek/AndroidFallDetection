package pl.edu.agh.mobilesystems.falldetection.accelerometer.impl

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerData
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService

class AccelerometerServiceImpl(private val sensorManager: SensorManager) : AccelerometerService, SensorEventListener {
    companion object Values {
        private const val TAG = "AccelerometerService"
    }

    private val sensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private var currentAccValue: AccelerometerData = AccelerometerData(0.0, 0.0, 0.0)

    init {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun getValue(): AccelerometerData {
        return currentAccValue
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            this.currentAccValue = parseEvent(event)
        } else {
            Log.d(TAG, "Null event received.")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy has changed")
    }

    private fun parseEvent(event: SensorEvent): AccelerometerData {
        val xCoordinate = event.values[0]
        val yCoordinate = event.values[1]
        val zCoordinate = event.values[2]

        return AccelerometerData(xCoordinate.toDouble(), yCoordinate.toDouble(), zCoordinate.toDouble())
    }
}