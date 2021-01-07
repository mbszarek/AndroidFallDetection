package pl.edu.agh.mobilesystems.falldetection.accelerometer

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import pl.edu.agh.mobilesystems.falldetection.utils.Constants


class AccelerometerService : Service(),
    SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var smsManager: SmsManager

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        smsManager = SmsManager.getDefault()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting ", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val accelerometerData = parseEvent(event)
            AccelerometerDataStore.setAccelerometerData(accelerometerData)

            if (Math.random() > 0.9) {
                sendSms()
            }
        } else {
            Log.d(Constants.AccelerometerService, "Null event received.")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(Constants.AccelerometerService, "Accuracy has changed")
    }

    private fun parseEvent(event: SensorEvent): AccelerometerData {
        val xCoordinate = event.values[0]
        val yCoordinate = event.values[1]
        val zCoordinate = event.values[2]

        return AccelerometerData(
            xCoordinate.toDouble(),
            yCoordinate.toDouble(),
            zCoordinate.toDouble()
        )
    }

    private fun sendSms() {
        val sharedPreferences =
            applicationContext.getSharedPreferences(Constants.AppName, Context.MODE_PRIVATE)
        val iceNumber = sharedPreferences.getString(Constants.IceNumberField, null)


        iceNumber?.also { number ->
            Log.d(Constants.AccelerometerService, "Sending sms to number $number")

            val intent = Intent(applicationContext, AccelerometerService::class.java)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

            smsManager.sendTextMessage(
                number,
                null,
                Constants.WarningMessage,
                pendingIntent,
                null
            )
        }

        if (iceNumber == null) {
            Log.e(Constants.AccelerometerService, "ICE number is not set!")
        }
    }
}