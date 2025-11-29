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
- Real-time audio playback with volume control.
- Drag-and-drop clip positioning on tracks.

---

## **Quick Start**

### **Prerequisites**
- **Java**: OpenJDK 22 or higher
- **Build Tool**: Maven 3.6+
- **C++ Compiler**: g++ (Linux/macOS) or MinGW (Windows)
- **IDE**: IntelliJ IDEA (recommended) or VS Code

### **Installation**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Dheeraj-Murthy/AudioEditor.git
   cd AudioEditor
   ```

2. **Compile Native Library**
   ```bash
   # On macOS/Linux
   cd native
   g++ -shared -fPIC -o libnative.so *.cpp
   
   # On macOS (alternative)
   g++ -shared -fPIC -o libnative.dylib *.cpp
   ```

3. **Setup IntelliJ IDEA**
   - Open the project in IntelliJ IDEA
   - Add VM options: `-Djava.library.path=native`
   - Run `Main.java`

4. **Alternative: Use Build Scripts**
   ```bash
   # Compile and run in one command
   ./scripts/build_and_run.sh
   ```

---

## **Project Structure**

```
AudioEditor/
├── native/                     # C++ native library
│   ├── *.cpp                   # C++ source files
│   ├── *.h                     # Header files
│   ├── libnative.so            # Compiled library (Linux)
│   └── libnative.dylib         # Compiled library (macOS)
├── src/main/java/              # Java source code
│   └── com/meenigam/           # Main package
│       ├── Components/         # UI components
│       ├── Panels/             # Main panels
│       ├── Utils/              # Utility classes
│       ├── Frame.java          # Main window
│       ├── Manager.java        # Application manager
│       └── Main.java           # Entry point
├── docs/                       # Documentation
├── scripts/                    # Build and test scripts
├── tests/                      # Test files
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

---

## **Detailed Setup**

For detailed step-by-step instructions, please refer to:
- 📖 [SETUP.md](docs/SETUP.md) - Complete setup guide
- 🧪 [TESTING.md](docs/TESTING.md) - Testing procedures
- 📊 [PROJECT_REPORT.pdf](docs/PROJECT_REPORT.pdf) - Formal project report

---

## **Testing**

### **Run All Tests**
```bash
./scripts/run_all_tests.sh
```

### **Individual Tests**
```bash
# Test native library compilation
./scripts/test_native_library.sh

# Test audio playback
./scripts/test_audio_playback.sh
```

### **Test Coverage**
- ✅ Native library compilation
- ✅ Audio file loading and playback
- ✅ UI component functionality
- ✅ Multi-track editing
- ✅ Audio effects processing
- ✅ Cross-platform compatibility

---

## **Build Scripts**

- `scripts/build_native.sh` - Compile native library
- `scripts/run_project.sh` - Run with correct VM options
- `scripts/test_audio_playback.sh` - Test audio functionality
- `scripts/run_all_tests.sh` - Execute all tests

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

---

## **Repository Information**

📁 **GitHub Repository**: [https://github.com/Dheeraj-Murthy/AudioEditor](https://github.com/Dheeraj-Murthy/AudioEditor)

📄 **Repository Link File**: [GITHUB_REPO_LINK.txt](GITHUB_REPO_LINK.txt)

---

## **Support**

For issues and questions:
1. Check the [SETUP.md](docs/SETUP.md) for installation problems
2. Refer to [TESTING.md](docs/TESTING.md) for testing issues
3. Create an issue on the GitHub repository 