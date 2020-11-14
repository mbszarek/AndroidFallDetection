package pl.edu.agh.mobilesystems.falldetection.accelerometer.impl

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.internal.util.reflection.Whitebox
import pl.edu.agh.mobilesystems.falldetection.accelerometer.AccelerometerData

class AccelerometerServiceImplTest {
    private val sensorManager: SensorManager = Mockito.mock(SensorManager::class.java).also {
        Mockito.`when`(it.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION))
            .thenReturn(Mockito.mock(Sensor::class.java))
    }
    private lateinit var accelerometerService: AccelerometerServiceImpl

    @Before
    fun initTests() {
        accelerometerService = AccelerometerServiceImpl(sensorManager)
    }

    @Test
    fun getValueShouldReturnInitialValueAfterInit() {
        assertEquals(AccelerometerData.withInitialValues(), accelerometerService.getValue())
    }

    @Test
    fun getValueShouldReactOnSensorsChange() {
        val sensorEvent = Mockito.mock(SensorEvent::class.java).also {
            Whitebox.setInternalState(it, "values", floatArrayOf(1.0f, 1.0f, 1.0f))
        }
        accelerometerService.onSensorChanged(sensorEvent)

        assertEquals(AccelerometerData(1.0, 1.0, 1.0), accelerometerService.getValue())
    }
}