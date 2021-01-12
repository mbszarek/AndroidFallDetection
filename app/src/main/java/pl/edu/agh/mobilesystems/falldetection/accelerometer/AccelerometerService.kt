package pl.edu.agh.mobilesystems.falldetection.accelerometer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import pl.edu.agh.mobilesystems.falldetection.detection.KNNDetector
import pl.edu.agh.mobilesystems.falldetection.restarter.Restarter
import pl.edu.agh.mobilesystems.falldetection.utils.Constants
import java.time.LocalDateTime


class AccelerometerService : Service(),
    SensorEventListener {
    private val kNNDetector = KNNDetector()
    private var lastSMSDateTime = LocalDateTime.now()
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var smsManager: SmsManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        smsManager = SmsManager.getDefault()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "service starting ", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

        val broadcastIntent = Intent().also {
            it.setAction("restartservice")
            it.setClass(this, Restarter::class.java)
        }

        sendBroadcast(broadcastIntent)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val accelerometerData = parseEvent(event)
            val distance = kNNDetector.newData(accelerometerData)
            AccelerometerDataStore.setAccelerometerData(accelerometerData)
            AccelerometerDataStore.setDistance(distance)
            if (distance > KNNDetector.FALL_THRESHOLD && lastSMSDateTime.plusSeconds(15)
                    .isBefore(LocalDateTime.now())
            ) {
                sendSms()
                lastSMSDateTime = LocalDateTime.now()
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
        val sharedPreferences = applicationContext.getSharedPreferences(
            Constants.AppName,
            Context.MODE_PRIVATE
        )
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

    private fun startForeground() {
        val channel = NotificationChannel(Constants.ChannelId, Constants.ChannelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, Constants.ChannelId)
            .setOngoing(true)
            .setContentTitle("Detecting fall detections in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(2, notification)

    }
}