package pl.edu.agh.mobilesystems.falldetection

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerDataStore
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService
import pl.edu.agh.mobilesystems.falldetection.detection.KNNDetector
import pl.edu.agh.mobilesystems.falldetection.utils.Constants
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class ReadingFragment : Fragment(R.layout.fragment_reading), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private lateinit var updateCoordJob: Job
    private lateinit var smsManager: SmsManager
    private lateinit var xCoordinateField: EditText
    private lateinit var yCoordinateField: EditText
    private lateinit var zCoordinateField: EditText
    private lateinit var distanceField: EditText
    private var lastSMSDateTime = LocalDateTime.now()

    private lateinit var writer: FileWriter
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button
    var isRunning: Boolean = false


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
        distanceField = view.findViewById(R.id.distance)

        smsManager = SmsManager.getDefault()

        val kNNDetector = KNNDetector()
        updateCoordJob = launch {
            while (true) {
                val coordinates = AccelerometerDataStore.getAccelerometerData()
                val distance = kNNDetector.newData(coordinates)
                if (distance > kNNDetector.FALL_THRESHOLD) {
                    distanceField.setTextColor(Color.RED)
                    if (lastSMSDateTime.plusSeconds(15).isBefore(LocalDateTime.now())) {
                        sendSms()
                        lastSMSDateTime = LocalDateTime.now()
                    }
                } else {
                    distanceField.setTextColor(Color.BLACK)
                }
                distanceField.setText(String.format("%.2f", distance))
                xCoordinateField.setText(String.format("%.2f", coordinates.xCoordinate))
                yCoordinateField.setText(String.format("%.2f", coordinates.yCoordinate))
                zCoordinateField.setText(String.format("%.2f", coordinates.zCoordinate))

                if (isRunning) {
                    writer.write(
                        String.format(
                            Locale.US,
                                "%d,%f,%f,%f,%f\n",
                            System.currentTimeMillis(),
                            distance,
                            coordinates.xCoordinate,
                            coordinates.yCoordinate,
                            coordinates.zCoordinate,
                        )
                    );
                }
                delay(100)
            }
        }

        buttonStart = view.findViewById(R.id.buttonStart);
        buttonStop = view.findViewById(R.id.buttonStop);

        buttonStart.setOnClickListener {
            buttonStart.isEnabled = false;
            buttonStop.isEnabled = true;

            try {
                writer = FileWriter(
                    File(
                        getStorageDir(),
                        "knn_" + System.currentTimeMillis() + ".csv"
                    )
                );
            } catch (e: IOException) {
                e.printStackTrace();
            }


            isRunning = true;
        }
        buttonStop.setOnClickListener {
            buttonStart.isEnabled = true;
            buttonStop.isEnabled = false;
            isRunning = false;
            try {
                writer.close();
                Toast.makeText(context, "File saved to " + getStorageDir(), Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }


        return view
    }

    private fun getStorageDir(): String? {
        return context?.getExternalFilesDir(null)?.getAbsolutePath()
        //  return "/storage/emulated/0/Android/data/com.iam360.sensorlog/";
    }

    private fun sendSms() {
        val applicationContext = activity!!.applicationContext
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

    override fun onDestroyView() {
        super.onDestroyView()
        updateCoordJob.cancel()
    }
}