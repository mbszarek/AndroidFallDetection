package pl.edu.agh.mobilesystems.falldetection.accelerometer

data class AccelerometerData(
    val xCoordinate: Double,
    val yCoordinate: Double,
    val zCoordinate: Double
) {
    companion object {
        fun withInitialValues(): AccelerometerData {
            return AccelerometerData(0.0, 0.0, 0.0)
        }
    }
}