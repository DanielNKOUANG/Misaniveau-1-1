package com.example.misaniveau.ui.screen2


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misaniveau.R
import com.example.misaniveau.ui.components.ElementList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState



@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Scan(
    title: String,
    goToHome:() -> Unit,
    // Callback qui est appelé dans le MainActivity
    viewModel: ScanViewModel = viewModel()
) {
    // La liste des appareils scannés autour
    val list by viewModel.scanItemsFlow.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanningFlow.collectAsStateWithLifecycle()
    val isConnecting by viewModel.isConnectingFlow.collectAsStateWithLifecycle()
    val isConnectedToDevice by viewModel.isConnectedToDeviceFlow.collectAsStateWithLifecycle()
    val ledState by viewModel.connectedDeviceLedStateFlow.collectAsStateWithLifecycle()
    val deviceName by viewModel.connectedDeviceNameFlow.collectAsStateWithLifecycle()
    val ledOnCount by viewModel.ledOnCountFlow.collectAsStateWithLifecycle()
    val ledOffCount by viewModel.ledOffCountFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current
    // État de la demande de permission (granted, denied, shouldShowRationale)
    val toCheckPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        listOf(android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN)
    }
    val permissionState = rememberMultiplePermissionsState(toCheckPermissions)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.scan_TopAppBar_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B263B),
                    titleContentColor = Color.White,
                ),
                navigationIcon = {
                    IconButton(onClick = { goToHome() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.ArrowBack)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearScanList() },
                        enabled = list.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.Refresh)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
           if(permissionState.allPermissionsGranted) {
                FloatingActionButton(
                    onClick = {
                        if (isScanning) {
                            viewModel.stopScan()
                        } else {
                            viewModel.startScan(context)
                        }
                    },
                   containerColor = Color(0xFF4A6FA5)
                ) {
                    Icon(Icons.Filled.Search, stringResource(R.string.Search))
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Partie 1: Demander la permission
            // Vérifier si la permission est accordée
           if(permissionState.allPermissionsGranted){
               // Partie 2: Si oui, Activer le BLE
               checkBluetoothEnabled(context, notAvailable={})
               //return@Column
               // Partie 3: Indiquer les états de connexions
               if (isScanning) {
                   CircularProgressIndicator(color = Color.White)
                   Spacer(modifier = Modifier.padding(8.dp))
                   Text(text = stringResource(R.string.scan_isScanning_text))
               }
               if (isConnecting) {
                   CircularProgressIndicator(color = Color.White)
                   Spacer(modifier = Modifier.padding(8.dp))
                   Text(text = stringResource(R.string.scan_isConnecting_text))
               }else if (isConnectedToDevice) {
                   Spacer(modifier = Modifier.padding(8.dp))
                   DisplayConnectedActions(
                       ledState = ledState,
                       onToggleLed = { viewModel.toggleLed()},
                       onDisconnect = { viewModel.disconnect()},
                       deviceName = deviceName,
                       onRenameDevice = { newName -> viewModel.renameConnectedDevice(context, newName)},
                       onPlayPattern = { viewModel.playDemoPattern()},
                       onSendSOS = {viewModel.sendSOS()},
                       ledOnCount = ledOnCount,
                       ledOffCount = ledOffCount
                   )
               }else {
                   // Partie 4: Liste des périphériques
                   LazyColumn(modifier = Modifier.fillMaxSize()) {
                       items(list) { ble ->
                           ElementList(
                               title = ble.device.name ?:stringResource(R.string.scan_UnknownDevice_text),
                               content = ble.device.address,
                               image = R.drawable.device,
                               onClick = {viewModel.connectToScanResult(context, ble)}
                           )
                       }
                   }
               }
               //Spacer(modifier = Modifier.padding(8.dp))
           } else{
               //Demander la permission
               Button(onClick = {permissionState.launchMultiplePermissionRequest()}) {
                   Text(text = stringResource(R.string.scan_PermissionRequest_text))
               }
           }
        }
    }
}

@Composable
fun DisplayConnectedActions(
    ledState: Boolean,
    onToggleLed: () -> Unit,
    onDisconnect: () -> Unit,
    deviceName: String,
    onRenameDevice: (String) -> Unit,
    onPlayPattern: () -> Unit,
    onSendSOS: () -> Unit,
    ledOnCount: Int,
    ledOffCount: Int
){
    var newName by remember { mutableStateOf(deviceName) }
   Column( horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth())
   {
       Text(
           text = stringResource(R.string.scan_deviceConnected_text, deviceName)
       )
       Spacer(modifier = Modifier.height(16.dp))

       Image(
          painter = painterResource(if(ledState) R.drawable.gojo else R.drawable.sukuna),
           contentDescription = if(ledState) stringResource(R.string.scan_image_LedOn) else stringResource(R.string.scan_image_LedOff),
           modifier = Modifier.size(250.dp)
       )
       Spacer(modifier = Modifier.height(16.dp))
       Text(
           text = if(ledState) stringResource(R.string.scan_image_LedOn) else stringResource(R.string.scan_image_LedOff)
       )
       Spacer(modifier = Modifier.height(8.dp))
       Row(
           horizontalArrangement = Arrangement.spacedBy(16.dp),
           verticalAlignment = Alignment.CenterVertically
       ){
           Text(text = stringResource(R.string.scan_LedOnCount,ledOnCount) , color = Color(0xFF2862E1))
           Text(text = stringResource(R.string.scan_LedOffCount,ledOffCount),
               color = Color(0xFFF44336)
           )
       }
       Spacer(modifier = Modifier.height(24.dp))

       Row(
           horizontalArrangement = Arrangement.spacedBy(12.dp),
           modifier = Modifier.fillMaxWidth()
       ) {
           Button(onClick = onToggleLed, modifier = Modifier.weight(1f),
               colors = ButtonDefaults.buttonColors(
                   containerColor =  if(ledState) Color(0xFFF44336) else Color(0xFF2196F3) ,
                   contentColor = Color.White
               )){
               Text(if(ledState) stringResource(R.string.scan_ledOff_button) else stringResource(R.string.scan_ledOn_button))
           }
           Button(onClick = onDisconnect, modifier = Modifier.weight(1f),
               colors = ButtonDefaults.buttonColors(
                   containerColor =  Color(0xFF214536)  ,
                   contentColor = Color.White
               )){
               Text(text = stringResource(R.string.scan_disconnect_button))
           }
       }
       Spacer(modifier = Modifier.height(16.dp))

       Row(
           horizontalArrangement = Arrangement.spacedBy(12.dp),
           modifier = Modifier.fillMaxWidth()
       ) {
           Button(onClick = onPlayPattern, modifier = Modifier.weight(1f),
               colors = ButtonDefaults.buttonColors(
                   containerColor =  Color(0xFF8EA9C1)  ,
                   contentColor = Color.White
               )){
               Text(text = stringResource(R.string.scan_animation_button))
           }
           Button(onClick = onSendSOS, modifier = Modifier.weight(1f),
               colors = ButtonDefaults.buttonColors(
                   containerColor =  Color(0xFF78196745)  ,
                   contentColor = Color.White
               )
           ){
               Text(text = stringResource(R.string.scan_sos_button))
           }
       }
       Spacer(modifier = Modifier.height(24.dp))

       OutlinedTextField(
           value = newName,
           onValueChange = { newName = it },
           label = { Text("Nom du périphérique")}
       )
       Spacer(modifier = Modifier.height(8.dp))
       Button(onClick = { onRenameDevice(newName) }, modifier = Modifier.align(Alignment.End),
           colors = ButtonDefaults.buttonColors(
               containerColor =  Color(0xFF4A6FA5)  ,
               contentColor = Color.White
           )
           ){
           Text(text = stringResource(R.string.scan_rename_button))
       }
   }
}

@Composable
fun checkBluetoothEnabled(context: Context, notAvailable: () -> Unit = {}) {
    val bluetoothManager: BluetoothManager? = remember {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
    }
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    val enableBluetoothLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {}
    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    LaunchedEffect(bluetoothAdapter) {
        when {
            bluetoothAdapter == null -> { notAvailable() }
            !bluetoothAdapter.isEnabled -> {
                // Demander l'activation du Bluetooth
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }
}
