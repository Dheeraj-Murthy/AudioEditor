# Audio Editor Project: **The Kitchen**

## **Overview**

This project is a feature-rich **Audio Editor** that combines Java's
user-friendly interface capabilities with C++'s performance-oriented audio
processing power. By leveraging the **Java Native Interface (JNI)**, the
application bridges the gap between platform-independent UI development and
high-performance native processing. It supports essential functionalities like
**amplitude scaling, frequency scaling, clipping, looping, reverb, and
multi-track editing**, among others.

---

## **Features**

### **Core Functionalities**

- **Amplitude Scaling**: Modify the amplitude of audio clips.
- **Compression**: Apply dynamic range compression with customizable thresholds
  and ratios.
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
- **Maven**: 3.6+
- **C++ Compiler**: g++ (Linux/macOS) or MinGW (Windows)
- **Make**: Build automation tool (included with most systems)
- **IDE**: IntelliJ IDEA (optional) or VS Code (optional)

### **Installation and Running**

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Dheeraj-Murthy/AudioEditor.git
   cd AudioEditor
   ```

2. **Build and Run (Recommended)**

   ```bash
   # Build everything and run in one command
   make dev

   # Or step by step
   make build    # Build native library + Java code
   make run      # Run the application
   ```

3. **Using IntelliJ IDEA (Optional)**
   - Open the project in IntelliJ IDEA
   - Add VM options: `-Djava.library.path=native`
   - Run `Main.java`

4. **Check Available Commands**
   ```bash
   make help     # Show all available commands
   ```

---

## **Build and Test Commands**

### **Build Commands**

```bash
make build          # Build everything (native + Java)
make native-lib     # Build C++ native library only
make java-compile   # Compile Java code only
make clean          # Clean build artifacts
make clean-all      # Clean all generated files
```

### **Test Commands**

```bash
make test           # Run all tests with detailed output
make test-unit      # Run unit tests (component isolation)
make test-integration # Run integration tests (component interaction)
make test-system    # Run system tests (end-to-end workflows)
make test-acceptance # Run acceptance tests (user scenarios)
make test-quick     # Run unit + integration tests (faster)
```

### **Development Commands**

```bash
make run            # Build and run application
make debug          # Run in debug mode (port 5005)
make dev            # Clean, build, and run (full workflow)
make dev-quick      # Quick development cycle
make help           # Show all available commands
make check-tools    # Verify required tools are installed
```

### **Utility Commands**

```bash
make deps           # Install/update dependencies
make package        # Package application for distribution
make dist           # Create complete distribution package
make docs           # Generate documentation
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
├── src/test/java/              # Test source code
│   ├── unit/                   # Unit tests
│   ├── integration/            # Integration tests
│   ├── system/                 # System tests
│   └── acceptance/             # Acceptance tests
├── docs/                       # Documentation
├── scripts/                    # Legacy build and test scripts
├── Makefile                    # Primary build automation
├── pom.xml                     # Maven configuration
├── PROJECT_REPORT.md           # Comprehensive project report
└── README.md                   # This file
```

---

## **Testing**

### **Using Makefile (Recommended)**

```bash
# Run all tests with descriptive output
make test

# Run specific test categories
make test-unit         # Test individual components
make test-integration  # Test component interactions
make test-system       # Test complete workflows
make test-acceptance   # Test user scenarios
```

### **Test Categories**

- **Unit Tests**: TestAudioPlayer, TestCallNative, TestClip, TestManager
- **Integration Tests**: TestAudioPlayback, TestJNIIntegration
- **System Tests**: TestFullWorkflow
- **Acceptance Tests**: TestUserScenarios

### **Test Output Features**

- **Descriptive Headings**: Clear category identification
- **Detailed Listings**: What each test class validates
- **Color-coded Output**: Easy-to-read test results
- **Progress Tracking**: Visual separation of test phases

### **Legacy Scripts (Alternative)**

```bash
./scripts/run_all_tests.sh
./scripts/test_native_library.sh
./scripts/test_audio_playback.sh
```

---

## **Detailed Setup**

For detailed step-by-step instructions, please refer to:

- 📖 [SETUP.md](docs/SETUP.md) - Complete setup guide
- 🧪 [TESTING.md](docs/TESTING.md) - Testing procedures
- 📊 [PROJECT_REPORT.md](PROJECT_REPORT.md) - Comprehensive project report

---

## **Key Features**

### **Audio Editing Capabilities**

- **Multi-track Editing**: Layer and edit multiple audio files
- **Real-time Waveform Display**: Visual audio representation
- **Audio Effects**: Amplitude scaling, frequency scaling, reverb, compression
- **Clip Management**: Trimming, looping, superimposition
- **File Support**: WAV file import/export
- **Cross-platform**: Windows, macOS, Linux compatibility

### **User Interface Enhancements**

- **Floating Audio Editing Button**: Always accessible "⋮ Edit" button
- **Track Selection**: Visual highlighting and feedback
- **Context Menus**: Right-click options for tracks
- **Glass Pane System**: Advanced overlay management
- **Responsive Design**: Intuitive Swing-based interface

### **Development Features**

- **Comprehensive Testing**: 4-tier test architecture
- **Native Integration**: High-performance C++ audio processing
- **Build Automation**: Cross-platform Makefile system
- **IDE Support**: IntelliJ IDEA integration
- **Documentation**: Complete setup and testing guides

---

## **Development Workflow**

### **Recommended Daily Workflow**

```bash
# Start development
make dev-quick      # Quick compile and run

# Full development cycle
make dev            # Clean, build, and run

# During development
make test-quick     # Run core tests quickly
make run            # Just run the application
```

### **Continuous Integration**

```bash
# Full CI pipeline
make ci             # Clean, check tools, build, test, package
```

### **Debugging and Profiling**

```bash
make debug          # Run with debug enabled (port 5005)
make profile        # Run with profiling enabled
```

### **Legacy Scripts**

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

📁 **GitHub Repository**:
[https://github.com/Dheeraj-Murthy/AudioEditor](https://github.com/Dheeraj-Murthy/AudioEditor)

📄 **Repository Link File**: [GITHUB_REPO_LINK.txt](GITHUB_REPO_LINK.txt)

---

## **Support**

For issues and questions:

1. Check the [SETUP.md](docs/SETUP.md) for installation problems
2. Refer to [TESTING.md](docs/TESTING.md) for testing issues
3. Create an issue on the GitHub repository

