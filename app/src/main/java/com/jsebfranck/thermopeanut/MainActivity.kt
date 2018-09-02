package com.jsebfranck.thermopeanut

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import com.jsebfranck.thermopeanut.listeners.OnDiscoveredDeviceListener


class MainActivity : AppCompatActivity() {

    private lateinit var connector: BluetoothLEConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connector = BluetoothLEConnector(this)

        connector.setOnDiscoveredDeviceListener(object : OnDiscoveredDeviceListener {
            override fun onDiscoveredDevice(device: BluetoothDevice) {
                createDeviceButton(device)
            }
        })

        val scanSwitch = findViewById<Switch>(R.id.scanSwitch)
        scanSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                connector.startScan()
            } else {
                connector.stopScan()
            }
        }
    }

    fun createDeviceButton(device: BluetoothDevice) {
        val layout = findViewById<LinearLayout>(R.id.mainLayout)
        val deviceButton = Button(this)
        deviceButton.text = "${device.address}  ${device.name}"

        deviceButton.setOnClickListener {
            connector.stopScan()
            findViewById<Switch>(R.id.scanSwitch).isChecked = false
            startActivity(DeviceInfosActivity.newIntent(this, device))
        }
        layout.addView(deviceButton)
    }
}
