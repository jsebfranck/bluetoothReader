package com.jsebfranck.thermopeanut.listeners

import android.bluetooth.BluetoothDevice

interface OnDiscoveredDeviceListener {

    fun onDiscoveredDevice(device: BluetoothDevice)
}