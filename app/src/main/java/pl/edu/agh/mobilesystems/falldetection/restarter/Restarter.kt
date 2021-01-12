package pl.edu.agh.mobilesystems.falldetection.restarter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Broadcast Listened", "Service has tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

        context!!.startForegroundService(Intent(context, AccelerometerService::class.java))
    }
}