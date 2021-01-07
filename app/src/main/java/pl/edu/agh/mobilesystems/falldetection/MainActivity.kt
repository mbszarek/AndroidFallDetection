package pl.edu.agh.mobilesystems.falldetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerService

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

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

        Intent(this, AccelerometerService::class.java).also {
            startService(it)
        }

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val readingFragment = ReadingFragment()
        val configurationFragment = ConfigurationFragment()

        setCurrentFragment(readingFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.readings -> setCurrentFragment(readingFragment)
                R.id.configuration -> setCurrentFragment(configurationFragment)
            }

            true
        }
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this, AccelerometerService::class.java))
    }
}