package com.jsebfranck.thermopeanut

import android.bluetooth.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import java.util.concurrent.ConcurrentHashMap

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val sensorAddress = "00:A0:50:46:2A:3E" // SensePeanut
    private val foundDevices = ConcurrentHashMap<String, BluetoothDevice>()

    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            val deviceName = device.name
            val deviceHardwareAddress = device.address

            if (!foundDevices.containsKey(deviceHardwareAddress)) {
                foundDevices.put(deviceHardwareAddress, device)
                log("Found device $deviceName $deviceHardwareAddress")

                if (deviceHardwareAddress.equals(sensorAddress)) {
                    scanLeDevice(false)
                    getInfos(device)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        log("Bluetooth enable=${mBluetoothAdapter.isEnabled}")

        scanLeDevice(true)

        //val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(sensorAddress)
        //getInfos(device)
    }

    private fun log(message: String) {
        val logs: EditText = this.findViewById(R.id.logs)
        logs.append("$message\n")
        Log.i(TAG, message)
    }

    private fun getInfos(device: BluetoothDevice) {

        // Various callback methods defined by the BLE API.
        val mGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                                 newState: Int) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    log("Connection changed: $status $newState => Connected")

                    val isServiceDiscoveryStarted = gatt.discoverServices()
                    log("Service discovery started=$isServiceDiscoveryStarted")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    log("Connection changed: $status $newState => Disconnected")
                } else {
                    log("Connection changed: $status $newState => ?")
                }
            }

            override// New services discovered
            fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                log("Services discovered: $status") // BluetoothGatt.GATT_SUCCESS
            }

            override// Result of a characteristic read operation
            fun onCharacteristicRead(gatt: BluetoothGatt,
                                     characteristic: BluetoothGattCharacteristic,
                                     status: Int) {
                log("Characteristic readed: $status") // BluetoothGatt.GATT_SUCCESS
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                log("Characteristic changed")
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                log("Characteristic write")
            }

            override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
                log("Desc read")
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
                log("Desc write")
            }
        }

        var gatt = device.connectGatt(this, false, mGattCallback)
//        val isServiceDiscoveryStarted = gatt.discoverServices()
//        log("Service discovery started=$isServiceDiscoveryStarted")
    }

    private fun scanLeDevice(enable: Boolean) {
        log("Scan status= $enable")
        if (enable) {
            mBluetoothAdapter.startLeScan(mLeScanCallback)
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }
    }
}
