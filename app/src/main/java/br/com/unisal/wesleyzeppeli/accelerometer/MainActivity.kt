package br.com.unisal.wesleyzeppeli.accelerometer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.AccelerometerTheme
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.GravityMain
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.LinearAccelMain
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.XAxis
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.YAxis
import br.com.unisal.wesleyzeppeli.accelerometer.ui.theme.ZAxis
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null

    var gravityAccel: Float = 0f
    var linearAccel: Float = 0f

    val gravity = FloatArray(3)
    val LinearAcceleration = FloatArray(3)

    var xAxisVisor by mutableStateOf("0.0 ms/s2")
    var yAxisVisor by mutableStateOf("0.0 ms/s2")
    var zAxisVisor by mutableStateOf("0.0 ms/s2")

    var gravityAccelVisor by mutableStateOf("0.00 ms/s2")
    var linearAccelVisor by mutableStateOf("0.00 ms/s2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            AccelerometerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    createInterface()
                }
            }
        }
    }

    @Composable
    fun createInterface() {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(text = "Axis Acceleration Reading", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                Text(text = "made by Kerilet", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Axis:", fontSize = 32.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 46.dp, end = 46.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "X", fontSize = 30.sp, color = XAxis)
                    Text(text = xAxisVisor, fontSize = 30.sp, color = XAxis)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 46.dp, end = 46.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Y", fontSize = 30.sp, color = YAxis)
                    Text(text = yAxisVisor, fontSize = 30.sp, color = YAxis)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 46.dp, end = 46.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Z", fontSize = 30.sp, color = ZAxis)
                    Text(text = zAxisVisor, fontSize = 30.sp, color = ZAxis)
                }
            }

            Column() {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 36.dp, end = 46.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Gravity Acceleration", fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = gravityAccelVisor, fontSize = 20.sp, color = GravityMain)
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 36.dp, end = 46.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Linear Acceleration", fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = linearAccelVisor, fontSize = 20.sp, color = LinearAccelMain)
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER)
            return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val alpha: Float = 0.8f

        gravity[0] = alpha * gravity[0] + (1 - alpha) * x
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z

        LinearAcceleration[0] = x - gravity[0]
        LinearAcceleration[1] = y - gravity[1]
        LinearAcceleration[2] = z - gravity[2]

        gravityAccel = sqrt(
            gravity[0].pow(2) + gravity[1].pow(2) + gravity[2].pow(2)
        )
        linearAccel = sqrt(
            LinearAcceleration[0].pow(2) + LinearAcceleration[1].pow(2) + LinearAcceleration[2].pow(2)
        )

        xAxisVisor = "%.1f m/s²".format(gravity[0])
        yAxisVisor = "%.1f m/s²".format(gravity[1])
        zAxisVisor = "%.1f m/s²".format(gravity[2])

        gravityAccelVisor = "%.2f m/s²".format(gravityAccel)
        linearAccelVisor = "%.2f m/s²".format(linearAccel)
    }

}