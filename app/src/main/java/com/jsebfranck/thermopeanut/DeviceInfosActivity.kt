package com.jsebfranck.thermopeanut

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.jsebfranck.thermopeanut.listeners.OnDeviceReadListener

class DeviceInfosActivity : AppCompatActivity() {

    private lateinit var connector: BluetoothLEConnector
    private lateinit var deviceAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_infos)

        deviceAddress = intent.getStringExtra(DEVICE_ADDRESS)
        connector = BluetoothLEConnector(this)
        getDeviceInfos()

        findViewById<Button>(R.id.retryGetInfos).setOnClickListener {
            getDeviceInfos()
        }
    }

    fun getDeviceInfos() {
        connector.getDeviceInfos(deviceAddress, object : OnDeviceReadListener {
            override fun onRawMessage(device: BluetoothDevice, message: String) {
                runOnUiThread {
                    logMessage(message)
                }
            }
        })
    }

    fun logMessage(message: String) {
        findViewById<EditText>(R.id.deviceRawMessages).append("$message\n")
    }

    companion object {
        private const val DEVICE_ADDRESS = "deviceAddress"

        fun newIntent(context: Context, device: BluetoothDevice): Intent {
            val intent = Intent(context, DeviceInfosActivity::class.java)
            intent.putExtra(DEVICE_ADDRESS, device.address)
            return intent
        }
    }
}
