package com.jsebfranck.thermopeanut

import android.app.Activity
import android.bluetooth.*
import android.util.Log
import com.jsebfranck.thermopeanut.listeners.OnDeviceReadListener
import com.jsebfranck.thermopeanut.listeners.OnDiscoveredDeviceListener
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BluetoothLEConnector {

    private val TAG = "BluetoothLEConnector"
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var scanCallback: BluetoothAdapter.LeScanCallback
    private lateinit var _activity: Activity
    private lateinit var onDiscoveredDeviceListener: OnDiscoveredDeviceListener
    private val foundDevices = ConcurrentHashMap<String, BluetoothDevice>()

    constructor(activity: Activity) {
        _activity = activity
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        log("Bluetooth enable=${bluetoothAdapter.isEnabled}")

        this.scanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
            _activity.runOnUiThread {
                if (!foundDevices.containsKey(device.address)) {
                    foundDevices.put(device.address, device)

                    if (onDiscoveredDeviceListener != null) {
                        onDiscoveredDeviceListener.onDiscoveredDevice(device)
                    }
                }
            }
        }
    }

    fun startScan() {
        log("Start scanning")
        bluetoothAdapter.startLeScan(scanCallback)
    }

    fun stopScan() {
        log("Stop scanning")
        bluetoothAdapter.stopLeScan(scanCallback)
    }

    fun setOnDiscoveredDeviceListener(onDiscoveredDeviceListener: OnDiscoveredDeviceListener) {
        this.onDiscoveredDeviceListener = onDiscoveredDeviceListener
    }

    fun getDeviceInfos(deviceAddress: String, onDeviceReadListener: OnDeviceReadListener) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)

        onDeviceReadListener.onRawMessage(device, "Get device infos ${device.address}  ${device.name}")

        val gattCallback = object : BluetoothGattCallback() {
            val characteristicsToRead = LinkedList<BluetoothGattCharacteristic>()
            val descriptorsToRead = LinkedList<BluetoothGattDescriptor>()
            var readFailsCount = 0

            fun readNext(gatt: BluetoothGatt) {
                if (!characteristicsToRead.isEmpty()) {
                    val characteristic = characteristicsToRead.pop()
                    val status = gatt.readCharacteristic(characteristic)
                    if (!status) {
                        handleReadError(gatt)
                    }
                    onDeviceReadListener.onRawMessage(device, "Try read characteristic: $status}")
                } else if (!descriptorsToRead.isEmpty()) {
                    val descriptor = descriptorsToRead.pop()

                    val status = gatt.readDescriptor(descriptor)
                    if (!status) {
                        handleReadError(gatt)
                    }
                    onDeviceReadListener.onRawMessage(device, "Try read descriptor: $status}")
                }
            }

            fun handleReadError(gatt: BluetoothGatt) {
                readFailsCount++
                if (readFailsCount < 5) {
                    readNext(gatt)
                }
            }

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                                 newState: Int) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    onDeviceReadListener.onRawMessage(device, "Connection changed: $status $newState => Connected")

                    val isServiceDiscoveryStarted = gatt.discoverServices()
                    onDeviceReadListener.onRawMessage(device, "Service discovery started=$isServiceDiscoveryStarted")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    onDeviceReadListener.onRawMessage(device, "Connection changed: $status $newState => Disconnected")
                } else {
                    onDeviceReadListener.onRawMessage(device, "Connection changed: $status $newState => ?")
                }
            }

            override// New services discovered
            fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                onDeviceReadListener.onRawMessage(device, "Services count: ${gatt.services.size}")

                gatt.services.forEach {
                    it.characteristics.forEach {
                        characteristicsToRead.add(it)
                        it.descriptors.forEach {
                            descriptorsToRead.add(it)
                        }
                    }
                }

                readNext(gatt)
            }

            override
            fun onCharacteristicRead(gatt: BluetoothGatt,
                                     characteristic: BluetoothGattCharacteristic,
                                     status: Int) {
                onDeviceReadListener.onRawMessage(device, "Characteristic readed: $status ${characteristic.value}") // BluetoothGatt.GATT_SUCCESS
                readNext(gatt)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                onDeviceReadListener.onRawMessage(device, "Characteristic changed")
            }

            override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
                onDeviceReadListener.onRawMessage(device, "Descriptor readed: $status")
            }
        }

        device.connectGatt(_activity, false, gattCallback)
    }

    private fun log(message: String) {
        Log.i(TAG, message)
    }
}