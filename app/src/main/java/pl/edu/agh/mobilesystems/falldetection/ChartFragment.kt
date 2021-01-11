package pl.edu.agh.mobilesystems.falldetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.*
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerDataStore
import pl.edu.agh.mobilesystems.falldetection.charts.ChartUtils
import kotlin.coroutines.CoroutineContext

class ChartFragment : Fragment(R.layout.fragment_chart), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private lateinit var updateCoordJob: Job

    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)

        mJob = Job()

        lineChart = view.findViewById(R.id.lineChart)


        updateCoordJob = launch {
            while (true) {
                val coordinates = AccelerometerDataStore.getAccelerometerData()

                ChartUtils.addEntry(
                    lineChart,
                    coordinates.xCoordinate,
                    coordinates.yCoordinate,
                    coordinates.zCoordinate,
                    0.0 //TODO TUTAJ WRZUCIĆ PARAMETR
                )

                delay(100)


            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateCoordJob.cancel()
    }
}