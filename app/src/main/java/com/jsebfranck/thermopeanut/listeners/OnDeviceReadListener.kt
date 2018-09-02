package com.jsebfranck.thermopeanut.listeners

import android.bluetooth.BluetoothDevice

interface OnDeviceReadListener {

    fun onRawMessage(device: BluetoothDevice, message: String)
}