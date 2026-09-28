# 🛡️ GuardianXR

**GuardianXR — ARCore-Based Spatial Safety Training System**

GuardianXR is an Android Augmented Reality (AR) application designed to provide interactive safety-training simulations for emergency situations. It uses Google ARCore to place virtual hazards in the user's real-world environment and guides the user through step-by-step emergency response procedures.

## 🚨 Training Modules

### 🔥 Fire & Explosion
- Scan the real-world environment
- Place a virtual fire hazard
- Follow safe approach and extinguisher procedures
- Confirm alarm and evacuation route
- Complete the emergency-response sequence

### ☣️ Gas Leak & Confined Space
- Scan the surrounding environment
- Place a virtual gas-leak hazard
- Establish an exclusion zone
- Perform PPE and buddy checks
- Confirm ventilation and escape route
- Complete the response sequence

## ✨ Key Features

- Real-time AR camera experience
- ARCore surface detection
- Horizontal and vertical plane detection
- Instant Placement fallback
- Spatially anchored AR hazards
- Interactive Fire and Gas simulations
- Step-by-step safety tasks
- Training completion tracking
- Reset and module-switching controls
- Portrait-oriented Android interface
- Physical-device tested

## 🛠️ Technology Stack

- Java
- Android Studio
- Google ARCore
- OpenGL ES
- Gradle
- Git & GitHub

## 📱 Requirements

- Android 10 (API 29) or later
- ARCore-supported Android device
- Rear camera
- Google Play Services for AR

## 🚀 How to Run

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle dependencies to sync.
4. Connect an ARCore-supported Android device.
5. Build and run the application.
6. Grant camera permission when requested.
7. Move the phone slowly to scan the environment.
8. Tap a detected surface to place a hazard.

## 📦 APK

A ready-to-install APK is available under the **Releases** section of this repository.

Open **GuardianXR v1.0.0** and download `app-debug.apk`.

> Android may require permission to install applications from unknown sources.

## 🧪 Tested Workflow

The application has been physically tested on an Android ARCore-capable device.

**Fire Module:**  
`STEP 0 → STEP 1 → STEP 2 → STEP 3 → Completed`

**Gas Module:**  
`STEP 0 → STEP 1 → STEP 2 → STEP 3 → Completed`

AR hazard placement, camera rendering, module switching, completion and reset functionality have been verified during prototype testing.

## 🎯 Project Objective

GuardianXR aims to make emergency-response training more interactive and accessible by allowing users to practise safety procedures through spatial AR simulations instead of relying only on static instructions.

## 📌 Version

**GuardianXR v1.0.0 — Initial Working Prototype**

---

### GuardianXR
**Spatial Safety Training through Augmented Reality**
