package pl.edu.agh.mobilesystems.falldetection

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mServiceIntent: Intent
    private lateinit var accelerometerService: AccelerometerService

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        }

        accelerometerService = AccelerometerService()
        mServiceIntent = Intent(this, accelerometerService.javaClass)

        if (!isMyServiceRunning(accelerometerService.javaClass)) {
            startService(mServiceIntent)
        }

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val readingFragment = ReadingFragment()
        val configurationFragment = ConfigurationFragment()
        val chartFragment = ChartFragment()

        setCurrentFragment(readingFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.readings -> setCurrentFragment(readingFragment)
                R.id.configuration -> setCurrentFragment(configurationFragment)
                R.id.charts -> setCurrentFragment(chartFragment)
            }

            true
        }
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this, AccelerometerService::class.java))
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningServices(Integer.MAX_VALUE).forEach { service ->
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false

    }
}