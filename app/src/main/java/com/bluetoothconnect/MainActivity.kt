package com.bluetoothconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluetoothconnect.ui.theme.BluetoothConnectTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothConnectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bluetoothHelper = rememberBluetoothHelper()
                    val scannedDevices by bluetoothHelper.scannedDevicesFlow.collectAsState()
                    val deviceConnectingWith by bluetoothHelper.deviceConnectingWith.collectAsState()

                    val isScanning by bluetoothHelper.isScanning.collectAsState()

                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {

                        stickyHeader {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Devices",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                        }
                        items(scannedDevices.toList()) { device ->
                            val isConnecting = deviceConnectingWith == device.address
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(text = "Device Name: ${device.name}")
                                    Text(text = "Device Address: ${device.address}")
                                    Button(
                                        enabled = deviceConnectingWith == null,
                                        onClick = {
                                            coroutineScope.launch {
                                                bluetoothHelper.connect(device.address)
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (isConnecting) {
                                                "Connecting..."
                                            } else {
                                                "Connect"
                                            }
                                        )
                                        if (isConnecting) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            AnimatedVisibility(visible = isScanning) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

