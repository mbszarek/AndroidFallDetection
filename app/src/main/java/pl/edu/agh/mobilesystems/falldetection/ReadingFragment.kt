package pl.edu.agh.mobilesystems.falldetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerDataStore
import kotlin.coroutines.CoroutineContext

class ReadingFragment : Fragment(R.layout.fragment_reading), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private lateinit var updateCoordJob: Job

    private lateinit var xCoordinateField: EditText
    private lateinit var yCoordinateField: EditText
    private lateinit var zCoordinateField: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reading, container, false)

        mJob = Job()

        xCoordinateField = view!!.findViewById(R.id.xCoord)
        yCoordinateField = view.findViewById(R.id.yCoord)
        zCoordinateField = view.findViewById(R.id.zCoord)

        updateCoordJob = launch {
            while (true) {
                val coordinates = AccelerometerDataStore.getAccelerometerData()
                xCoordinateField.setText(String.format("%.2f", coordinates.xCoordinate))
                yCoordinateField.setText(String.format("%.2f", coordinates.yCoordinate))
                zCoordinateField.setText(String.format("%.2f", coordinates.zCoordinate))
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