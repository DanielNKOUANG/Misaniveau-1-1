<p align="center">
  <img src="https://img.shields.io/badge/Bluetooth%20Low%20Energy-Technology-blue?style=for-the-badge&logo=bluetooth&logoColor=white"/>
</p>

<h1 align="center">🔵 Misaniveau 1.1 — Android BLE Application</h1>

<p align="center">A fully functional Android application designed to scan, connect and interact with an ESP32 device using Bluetooth Low Energy.</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-Compose-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/BLE-Supported-blue?style=for-the-badge&logo=bluetooth&logoColor=white"/>
</p>

---

## 📌 **Project Overview**

**Misaniveau 1.1** is an Android application developed as part of an academic project.  
It showcases practical mastery of:

- **Bluetooth Low Energy (BLE)**
- **Jetpack Compose**
- **MVVM architecture with ViewModel + StateFlow**
- **Runtime permission handling**
- **Navigation between multiple screens**
- **Internationalization (French & English)**

The application allows the user to scan BLE devices, connect to an ESP32, toggle an LED, send animations, rename the device, and receive characteristic notifications.

---

## 🔋 **Key Features**

### 🔍 **BLE Scan**
- Detect nearby BLE devices  
- Display compatible peripherals  
- Handle permissions properly (SCAN / CONNECT / LOCATION)

### 🔗 **Device Connection**
- Connect to an ESP32 GATT server  
- Receive notifications in real time  
- Detect disconnection events  
- Rename the connected device

### 💡 **LED Control**
- Turn LED **ON / OFF**  
- Display LED state  
- Count how many times the LED was toggled  
- Send custom binary animations  
- Send SOS pattern

### 🧩 **Animations Screen**
- Create / delete custom patterns  
- Store them temporarily  
- Send patterns via BLE

### 🌍 **Internationalization**
- Full support for:
  - 🇫🇷 French  
  - 🇬🇧 English  
- Every UI text pulled from `strings.xml`

### 🎨 **Modern UI (Compose)**
- Material Design 3  
- Floating Action Buttons  
- Spaced and styled text  
- Custom colors  
- Centered elements  
- Progress indicators

---

## 🏗 **Architecture & Technologies**

| Layer | Description |
|-------|-------------|
| **UI Layer** | Jetpack Compose (Screens, Composables, FABs, texts, icons) |
| **Navigation** | Compose Navigation |
| **Business Logic** | ViewModel + StateFlow |
| **BLE Handling** | Dedicated BLE Manager using `BluetoothGatt`, `BluetoothGattCallback`, read/write operations, notifications |
| **Permissions** | AndroidX Activity API + runtime checks |

---

## 📂 **Project Structure (simplified)**

app/
├── ui/
│ ├── HomeScreen.kt
│ ├── ScanScreen.kt
│ ├── DisplayConnectedScreen.kt
│ └── PatternsScreen.kt
├── viewmodel/
│ └── ScanViewModel.kt
├── ble/
│ └── BLEManager.kt
├── navigation/
│ └── AppNavigation.kt
├── res/
│ ├── values/strings.xml
│ └── values-en/strings.xml
└── AndroidManifest.xml


## 🚀 **How to Run the Project**

### Requirements
- Android Studio Flamingo / Hedgehog / newer  
- Minimum SDK : **26**  
- A BLE-compatible Android device  
- ESP32 with matching GATT service  

### Steps
1. Clone the repository :
   ```bash
   git clone https://github.com/DanielNKOUANG/Misaniveau-1-1.git
Open in Android Studio

Wait for Gradle sync

Enable Bluetooth + Location

Install and run on a physical device


🧠 What I Learned (Ce que j’ai appris)
This project allowed me to gain solid experience in:

Understanding and implementing Bluetooth Low Energy

Structuring an app with MVVM and reactive state management

Working with Jetpack Compose and MaterialDesign 3

Managing permissions correctly across different Android versions

Handling asynchronous BLE operations (read/write/notify)

Building multiple screens and navigating between them

Internationalizing an Android application

Debugging and testing BLE connections on real devices

👤 Author
Guy Bertrand Daniel Nkouang Mbarga
GitHub : DanielNKOUANG

📄 License
This project is for academic evaluation purposes.

