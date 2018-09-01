package com.jsebfranck.thermopeanut.connectors

import android.bluetooth.BluetoothDevice
import android.content.Context

class BluetoothLEConnector(context: Context, device: BluetoothDevice) {
/*

    private val sensor = device
    private val activityContext = context

    fun getData(): String {

        // Various callback methods defined by the BLE API.
        val mGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                                 newState: Int) {
                val intentAction: String
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = ACTION_GATT_CONNECTED
                    mConnectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices())

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = ACTION_GATT_DISCONNECTED
                    mConnectionState = STATE_DISCONNECTED
                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }

            override// New services discovered
            fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                } else {
                    Log.w(TAG, "onServicesDiscovered received: $status")
                }
            }

            override// Result of a characteristic read operation
            fun onCharacteristicRead(gatt: BluetoothGatt,
                                     characteristic: BluetoothGattCharacteristic,
                                     status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }
        }

        sensor.connectGatt(activityContext, false, mGattCallback)

        return "no data"
    }*/

}
