package com.jsebfranck.thermopeanut

import android.bluetooth.BluetoothDevice

interface OnDiscoveredDeviceListener {

    fun onDiscoveredDevice(device: BluetoothDevice)
}