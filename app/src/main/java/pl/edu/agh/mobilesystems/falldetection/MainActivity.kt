package pl.edu.agh.mobilesystems.falldetection

import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService
import pl.edu.agh.mobilesystems.falldetection.accelerometer.impl.AccelerometerServiceImpl
import pl.edu.agh.mobilesystems.falldetection.detection.KNNDetector
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var accelerometerService: AccelerometerService

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private lateinit var updateCoordJob: Job
    private lateinit var kNNDetector: KNNDetector

    private lateinit var xCoordinateField: EditText
    private lateinit var yCoordinateField: EditText
    private lateinit var zCoordinateField: EditText
    private lateinit var distanceField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mJob = Job()
        accelerometerService =
                AccelerometerServiceImpl(getSystemService(Context.SENSOR_SERVICE) as SensorManager).also {
                    it.start()
                }
        kNNDetector = KNNDetector()

        xCoordinateField = findViewById(R.id.xCoord)
        yCoordinateField = findViewById(R.id.yCoord)
        zCoordinateField = findViewById(R.id.zCoord)
        distanceField = findViewById(R.id.distance)

        updateCoordJob = launch {
            while (true) {
                val coordinates = accelerometerService.getValue()
                val distance = kNNDetector.newData(coordinates)
                if (distance > kNNDetector.FALL_THRESHOLD) {
                    distanceField.setTextColor(Color.RED)
                } else {
                    distanceField.setTextColor(Color.BLACK)
                }
                distanceField.setText(String.format("%.2f", distance))
                xCoordinateField.setText(String.format("%.2f", coordinates.xCoordinate))
                yCoordinateField.setText(String.format("%.2f", coordinates.yCoordinate))
                zCoordinateField.setText(String.format("%.2f", coordinates.zCoordinate))
                delay(100)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accelerometerService.stop()
        updateCoordJob.cancel()
    }
}