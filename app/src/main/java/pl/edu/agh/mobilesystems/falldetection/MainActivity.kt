package pl.edu.agh.mobilesystems.falldetection

import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.*
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService
import pl.edu.agh.mobilesystems.falldetection.accelerometer.impl.AccelerometerServiceImpl
import pl.edu.agh.mobilesystems.falldetection.charts.ChartUtils
import pl.edu.agh.mobilesystems.falldetection.detection.KNNDetector
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var accelerometerService: AccelerometerService

    private lateinit var mJob: Job
    private var isChartActive: Boolean = false;
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private lateinit var updateCoordJob: Job
    private lateinit var kNNDetector: KNNDetector

    private lateinit var xCoordinateField: EditText
    private lateinit var yCoordinateField: EditText
    private lateinit var zCoordinateField: EditText
    private lateinit var distanceField: EditText
    private lateinit var chartButton: Button
    private lateinit var inputsLayout: ConstraintLayout
    private lateinit var chartsLayout: ConstraintLayout
    private lateinit var lineChart: LineChart

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
        lineChart = findViewById(R.id.chart)

        inputsLayout = findViewById(R.id.inputsLayout)
        chartsLayout = findViewById(R.id.chartsLayout)
        chartButton = findViewById(R.id.chartButton)
        chartButton.setOnClickListener {
            isChartActive = !isChartActive
            if (inputsLayout.visibility == View.VISIBLE) {
                inputsLayout.visibility = View.GONE
                chartsLayout.visibility = View.VISIBLE
            } else {
                inputsLayout.visibility = View.VISIBLE
                chartsLayout.visibility = View.GONE
                lineChart.data.clearValues();
                lineChart.clear()
            }
        }



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
                if (isChartActive) {
                    ChartUtils.addEntry(
                        lineChart,
                        coordinates.xCoordinate,
                        coordinates.yCoordinate,
                        coordinates.zCoordinate,
                        distance
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accelerometerService.stop()
        updateCoordJob.cancel()
    }
}