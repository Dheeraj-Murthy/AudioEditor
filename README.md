# Audio Editor Project: **The Kitchen**

## **Overview**

This project is a feature-rich **Audio Editor** that combines Java's user-friendly interface capabilities with C++'s
performance-oriented audio processing power. By leveraging the **Java Native Interface (JNI)**, the application bridges
the gap between platform-independent UI development and high-performance native processing. It supports essential
functionalities like **amplitude scaling, frequency scaling, clipping, looping, reverb, and multi-track editing**, among
others.

---

## **Features**

### **Core Functionalities**

- **Amplitude Scaling**: Modify the amplitude of audio clips.
- **Compression**: Apply dynamic range compression with customizable thresholds and ratios.
- **Frequency Scaling**: Adjust pitch and frequency for audio manipulation.
- **Looping**: Select and loop specific portions of an audio clip.
- **Reverb**: Alter persistence factors for creating reverberation effects.
- **Time Scaling**: Change the playback duration of audio clips.
- **Clip Trimming**: Cut or trim unwanted parts of the audio.
- **Clip Superimposition**: Merge multiple audio clips into one.
- **Audio Filtering**: Apply frequency-based filters to audio clips.
- **Multi-Track Editing**: Layer multiple audio files and edit them in parallel.

### **Additional Capabilities**

- Visual waveform display (amplitude vs. time graph).
- Cross-platform compatibility (Windows, macOS, Linux).
- Responsive and intuitive Java Swing-based UI.
- Support for .wav file input and output.

---

## **Getting Started**

### **System Requirements**

- **Frontend**: Java (Swing)
- **Backend**: C++
- **Middleware**: JNI for communication between Java and C++
- **Platforms Supported**: macOS, Ubuntu, Windows
- **Tools**:
    - OpenJDK 22.0.1
    - g++
    - NeoVim
    - IntelliJ IDEA
    - VS Code
    - Git

---

## **Installation Guide**

### Clone the Repository

Run the following command:  
    `git@github.com:Dheeraj-Murthy/AudioEditor.git`

### Setup

1. **Java & C++**: Ensure you have OpenJDK and g++ installed.
2. **Compile Native Code**:
    - On **Ubuntu** or **macOS**:  
      `cd native`  
      `g++ -shared -o libaudioeffects.so -fPIC *.cpp`
    - On **Windows**:  
      Follow the instructions in the project documentation for your specific setup.
3. **Run the Project**:
    - Open the project in IntelliJ IDEA.
    - Configure the JNI library path to point to the compiled `libaudioeffects.so` (or equivalent for your OS).
    - Run `Main.java` to start the application.

---

## **Workflow**

1. **UI Development**:
    - Build the frontend in Java using Swing.
    - Create user-friendly controls for playback, amplitude scaling, and clip editing.
2. **Audio Processing Implementation**:
    - Implement audio manipulation functionalities in C++.
    - Example functions include amplitude scaling, frequency manipulation, and reverb.
3. **JNI Bridge**:
    - Use JNI to enable communication between Java and C++.
    - Write Java native methods that invoke the C++ backend logic.

---

## **Folder Structure**

- **native/**: Contains the C++ implementation of audio editing functions.
- **src/main/java/**: Contains all Java code, including the UI logic.

---

## **Testing**

- **Unit Testing**:
    - Validate individual functionalities like amplitude scaling, trimming, and looping.
- **Integration Testing**:
    - Test the interaction between the Java frontend and C++ backend via JNI.
- **Performance Testing**:
    - Measure processing times for large audio files.
- **Error Handling**:
    - Check robustness against invalid inputs and exceptions.

---

## **Contributors**

- **M S Dheeraj Murthy** (IMT2023552)  
  Email: ms.dheerajmurthy@iiitb.ac.in
- **Mathew Joseph** (IMT2023008)  
  Email: Mathew.joseph@iiitb.ac.in
- **Ayush Tiwari** (IMT2023524)  
  Email: Ayush.tiwari524@iiitb.ac.in
- **Lesin** (IMT2023565)  
  Email: Lesin.565@iiitb.ac.in
- **Priyanshu Pattnaik** (IMT2023046)  
  Email: Priyanshu.Pattnaik@iiitb.ac.in

---

## **License**

This project is licensed under the MIT License. 