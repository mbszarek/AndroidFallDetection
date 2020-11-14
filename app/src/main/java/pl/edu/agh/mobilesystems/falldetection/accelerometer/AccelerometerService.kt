package pl.edu.agh.mobilesystems.falldetection.accelerometer

interface AccelerometerService {
    fun start()
    fun stop()
    fun getValue(): AccelerometerData
}