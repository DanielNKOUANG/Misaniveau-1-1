package com.example.misaniveau.ui.screen2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.misaniveau.data.ble.BluetoothLEManager
import java.util.UUID
import com.google.accompanist.permissions.ExperimentalPermissionsApi


class ScanViewModel : ViewModel() {

    private var currentBluetoothGatt : BluetoothGatt? = null
    private var currentDeviceAddress : String? = null

    // Flow permettant de savoir si la LED est allumée ou éteinte
    val connectedDeviceLedStateFlow = MutableStateFlow(false)

    // Si la valeur est "1", la LED est allumée, sinon elle est éteinte
    // connectedDeviceLedStateFlow.value = value == "1"
    // Compter le nombre d'allumager et d'extinction
    val ledOnCountFlow = MutableStateFlow(0)
    val ledOffCountFlow = MutableStateFlow(0)
    // Pour connaître le nom du device
    val connectedDeviceNameFlow = MutableStateFlow<String>("Inconnu au bataillon")
    // Le processus de scan
    private var scanJob: Job? = null

    // Durée du scan
    private val scanDuration = 10000L

    /**
     * Le scanner bluetooth
     */
// La liste des appareils scannés autour
    val scanItemsFlow = MutableStateFlow<List<ScanResult>>(emptyList())

    // Boolean permettant de savoir si nous sommes en train de scanner
    val isScanningFlow = MutableStateFlow(false)

    private val scanFilters: List<ScanFilter> = listOf(
        // À décommenter pour filtrer les périphériques
         ScanFilter.Builder().setServiceUuid(ParcelUuid(BluetoothLEManager.DEVICE_UUID)).build()
    )
    // Les options de scan (mode faible latence)
    private val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    // Liste des résultats du scan, Le Set sera utilisé pour éviter les doublons
    private val scanResultsSet = mutableMapOf<String, ScanResult>()
    // Flow permettant de savoir si nous sommes en train de nous connecter
    val isConnectingFlow = MutableStateFlow(false)

    // Flow permettant de savoir si un appareil est connecté
    val isConnectedToDeviceFlow = MutableStateFlow(false)

    // Sera mis à jour dans la méthode onNotify du GattCallback
// Si la valeur est "1", la LED est allumée, sinon elle est éteinte
// connectedDeviceLedStateFlow.value = value == "1"
    @SuppressLint("MissingPermission")
    fun startScan(context: Context) {
        if(!hasBleScanPermission(context)){
            return
        }
        // Récupération du scanner BLE
        val bluetoothLeScanner = (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter.bluetoothLeScanner
        // Si nous sommes déjà en train de scanner, on ne fait rien
        if (isScanningFlow.value) return

        // Définition du processus de scan (Coroutine)
        // Une coroutine est un moyen de gérer des tâches asynchrones de manière plus simple et plus lisible
        scanJob = CoroutineScope(Dispatchers.IO).launch {
            // On indique que nous sommes en train de scanner
            isScanningFlow.value = true

            // Objet qui sera appelé à chaque résultat de scan
            val scanCallback = object : ScanCallback() {
                /**
                 * Le callback appelé à chaque résultat de scan (nouvel appareil trouvé)
                 * Il n'est pas dédoublonné, c'est à nous de le faire (il peut être appelé plusieurs fois pour le même appareil)
                 */
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    super.onScanResult(callbackType, result)
                    // On ajoute le résultat dans le set, si il n'y est pas déjà
                    // L'ajout retourne null si l'élément n'était pas déjà présent
                    if (scanResultsSet.put(result.device.address, result) == null) {
                        // On envoie la nouvelle liste des appareils scannés
                        scanItemsFlow.value = scanResultsSet.values.toList()
                    }
                }
            }

            // On lance le scan BLE a la souscription de scanFlow
            bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback)
            // On attend la durée du scan (10 secondes)
            delay(scanDuration)
            // Lorsque scanFlow est stoppé, on stop le scan BLE
            bluetoothLeScanner.stopScan(scanCallback)
            // On indique que nous ne sommes plus en train de scanner
            isScanningFlow.value = false
        }
    }
    fun stopScan() {
        scanJob?.cancel()
        isScanningFlow.value = false
    }
    fun clearScanList() {
        scanItemsFlow.value = emptyList()
    }
    fun hasBleScanPermission(context : Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    }
    @SuppressLint("MissingPermission")
    fun connectToScanResult(context: Context, result: ScanResult){
        connect(context, result.device)
    }

    @SuppressLint("MissingPermission")
    fun connect(context: Context, bluetoothDevice: BluetoothDevice) {
        // On arrête le scan si il est en cours
        stopScan()
        // On indique que nous sommes en train de nous connecter (pour afficher un loader par exemple)
        isConnectingFlow.value = true
        // On ferme une eventuelle connexion
        currentBluetoothGatt?.close()
        currentBluetoothGatt = null
        //On conserve l'addresse du périphérique
        currentDeviceAddress = bluetoothDevice.address

        //On charge l'alias
        val alias = loadAlias(context,bluetoothDevice.address)
        //nom intial
        connectedDeviceNameFlow.value = alias ?: (bluetoothDevice.name ?: "Inconnu au bataillon")
        // On tente de se connecter à l'appareil
        // On utilise le GattCallback pour gérer les événements BLE (connexion, déconnexion, notifications).
        currentBluetoothGatt = bluetoothDevice.connectGatt(
            context,
            false,
            BluetoothLEManager.GattCallback(
                // La connexion a réussi (onServicesDiscovered)
                onConnect = {
                    isConnectedToDeviceFlow.value = true
                    isConnectingFlow.value = false
                    // On active les notifications pour recevoir les événements de la LED et du compteur
                     enableNotify()
                },

                // Nouvelle valeur reçue sur une caractéristique de type notification
                onNotify = { characteristic, value ->
                    when (characteristic.uuid) {
                        BluetoothLEManager.CHARACTERISTIC_NOTIFY_STATE -> connectedDeviceLedStateFlow.value =
                            value == "1"
                        // Implémenter les autres caractéristiques ici (count, wifi)
                    }
                },

                // L'ESP32 s'est déconnecté (BluetoothGatt.STATE_DISCONNECTED)
                onDisconnect = {
                  disconnect()
                }
            )
        )
    }

    fun disconnect(){
        isConnectedToDeviceFlow.value = false
        isConnectingFlow.value = false
        currentBluetoothGatt?.close()
        currentBluetoothGatt = null
    }


    fun toggleLed() {
        // Si la LED est ON, on envoie 0 pour l'OFF, sinon 1 pour l'ON
        val newValue = if( connectedDeviceLedStateFlow.value) "0" else "1"
        //On met à jour les compteurs
        if(!connectedDeviceLedStateFlow.value){
            ledOnCountFlow.value = ledOnCountFlow.value + 1
        } else{
            ledOffCountFlow.value = ledOffCountFlow.value + 1
        }
        // On met à jour l'état, en attendant la notif
        connectedDeviceLedStateFlow.value = !connectedDeviceLedStateFlow.value
        writeCharacteristic(BluetoothLEManager.CHARACTERISTIC_TOGGLE_LED_UUID, newValue)

    }
    fun renameConnectedDevice(context: Context,newName: String){
        val gatt = currentBluetoothGatt ?: return
        val address = currentDeviceAddress ?: return
        if(newName.isBlank()) return
        connectedDeviceNameFlow.value = newName
        saveAlias(context, address, newName)
        val service = gatt.getService(BluetoothLEManager.DEVICE_UUID) ?: return
        val characteristic =
            service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_SET_DEVICE_NAME) ?: return

        characteristic.value = newName.toByteArray(Charsets.UTF_8)
        gatt.writeCharacteristic(characteristic)
    }
    fun playLedPattern(pattern: String, delayMs: Long = 650L){
        CoroutineScope(Dispatchers.IO).launch{
            for(c in pattern){
                // 1 = allumé, 0= éteint
                val isOn = (c == '1')
                val value = if(c == '1') "1" else "0"
                //On change l'etat local
                connectedDeviceLedStateFlow.value = isOn
                // On gère les compteurs
                if(isOn){
                    ledOnCountFlow.value = ledOnCountFlow.value + 1
                } else{
                    ledOffCountFlow.value = ledOffCountFlow.value + 1
                }
                //On send ça à l'esp32
                writeCharacteristic(BluetoothLEManager.CHARACTERISTIC_TOGGLE_LED_UUID, value)
                delay(delayMs)
            }
        }
    }
    //Je suppose que c'est ma propre démo
    fun playDemoPattern() {
        val pattern = "101000101101100111010101"
        playLedPattern(pattern, delayMs = 750L)
    }
    //Envoi d'un SOS en morse, j'ai pris ça chez l'ia parcontre
    fun sendSOS(){
        // En morse : point = court, trait = long
        val dot ="1" // ON court
        val dash ="111" //ON long
        val sep = "0" // pause courte entre point/traits
        val letterSep = "000" // pause entre lettres
        val s = dot + sep + dot + sep + dot //...
        val o = dash + sep + dash + sep + dash //---
        val sos = s + letterSep + o + letterSep + s
        playLedPattern(sos, delayMs = 750L)
    }

    // Fonctions permettant de conserver des paramètres, à savoir le nom du périphérique
    private fun saveAlias(context: Context, address: String, alias: String){
        val prefs = context.getSharedPreferences("device_aliases", Context.MODE_PRIVATE)
        prefs.edit().putString(address, alias).apply()
    }
    private fun loadAlias(context: Context,address: String): String?{
        val prefs = context.getSharedPreferences("device_aliases", Context.MODE_PRIVATE)
        return prefs.getString(address, null)
    }

    private fun getMainService(): BluetoothGattService? = currentBluetoothGatt?.getService(BluetoothLEManager.DEVICE_UUID)

    @SuppressLint("MissingPermission")
    private fun writeCharacteristic(uuid: UUID, value: String) {
        // Récupération du service principal (celui de l'ESP32)
        getMainService()?.let { service ->
            // Récupération de la caractéristique
            val characteristic = service.getCharacteristic(uuid)

            if (characteristic == null) {
                Log.e("BluetoothLEManager", "La caractéristique $uuid n'a pas été trouvée")
                return
            }

            Log.i("BluetoothLEManager", "Ecriture de la valeur $value dans la caractéristique $uuid")

            // En fonction de la version de l'OS, on utilise la méthode adaptée
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // On écrit la valeur dans la caractéristique
                currentBluetoothGatt?.writeCharacteristic(characteristic, value.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            } else {
                // On écrit la valeur dans la caractéristique
                characteristic.setValue(value)
                currentBluetoothGatt?.writeCharacteristic(characteristic)

            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableNotify() {
        getMainService()?.let { service ->
            // Indique que le GATT Client va écouter les notifications sur le charactérisque
            val notificationStatus = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_NOTIFY_STATE)
            val notificationLedCount = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_GET_COUNT)
            val wifiScan = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_GET_SET_WIFI)

            listOf(notificationStatus, notificationLedCount, wifiScan).forEach { characteristic ->
                currentBluetoothGatt?.setCharacteristicNotification(characteristic, true)
                characteristic.getDescriptor(BluetoothLEManager.CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID)?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        currentBluetoothGatt?.writeDescriptor(it, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    } else {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        currentBluetoothGatt?.writeDescriptor(it)
                    }
                }
            }
        }
    }

}