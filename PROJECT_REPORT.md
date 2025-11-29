# Audio Editor Project Report

**Team Members:**

- M S Dheeraj Murthy (IMT2023552)
- Mathew Joseph (IMT2023008)
- Ayush Tiwari (IMT2023524)
- Mathew Joseph (IMT2023008)
- Priyanshu Pattnaik (IMT2023046)

[GitHub URL: https://github.com/Dheeraj-Murthy/AudioEditor.git](https://github.com/Dheeraj-Murthy/AudioEditor.git)

## Project Overview

The Audio Editor is a comprehensive Java-based desktop application for audio
editing and manipulation. Built with Java 22 and featuring native C++
integration through JNI, this project provides professional-grade audio editing
capabilities with an intuitive user interface.

## Architecture

### Core Components

#### **Main Application**

- **Main.java**: Entry point and application initialization
- **Frame.java**: Main application window with glass pane overlay system
- **Manager.java**: Central state management and coordination

#### **Audio Components**

- **AudioPlayer.java**: Audio playback and control functionality
- **Clip.java**: Audio clip management and manipulation
- **WavFileCreator.java**: WAV file generation and export
- **WaveformPanel.java**: Visual waveform representation and editing

#### **User Interface**

- **ControlPanel.java**: Main controls and toolbar
- **TrackEditor.java**: Multi-track editing interface
- **StagingArea.java**: Clip organization and management
- **MultiInputDialog.java**: Complex user input handling

#### **Native Integration**

- **callNative.java**: JNI interface for native operations
- **callNative.cpp/h**: C++ native library implementation

## Key Features

### **Audio Editing Capabilities**

- Multi-track audio editing
- Real-time waveform visualization
- Clip-based audio manipulation
- WAV file import/export
- Native audio processing

### **User Interface Enhancements**

- Floating audio editing button (always accessible)
- Track selection with visual feedback
- Right-click context menus
- Glass pane overlay system
- Responsive design patterns

### **Testing Framework**

- Comprehensive test suite with 4 categories
- Unit tests for component isolation
- Integration tests for component interaction
- System tests for end-to-end workflows
- Acceptance tests for user scenarios

## Technical Implementation

### **Build System**

- **Maven**: Java dependency management and compilation
- **Makefile**: Cross-platform build automation
- **CMake**: Native library compilation
- **Shell Scripts**: Development workflow automation

### **Testing Infrastructure**

- **JUnit 5**: Primary testing framework
- **Mockito**: Mocking and test doubles
- **TestFX**: UI testing capabilities
- **Maven Surefire/Failsafe**: Test execution and reporting

### **Native Integration**

- **JNI**: Java-Native interface
- **C++11**: Native library implementation
- **Cross-platform**: Linux, macOS, Windows support
- **Dynamic linking**: Runtime library loading

## Recent Improvements

### **Enhanced User Experience**

1. **Floating Audio Editing Button**
   - Fixed accessibility issue with scrollable track editor
   - Implemented glass pane overlay system
   - Always-visible "⋮ Edit" button in top-right corner
   - Shows selected track name

2. **Track Selection System**
   - Visual highlighting of selected tracks
   - Multiple access methods (floating button + right-click)
   - Context-aware menu options
   - Improved user feedback

### **Build System Improvements**

1. **Enhanced Makefile**
   - Comprehensive test categories with descriptions
   - Color-coded output for better readability
   - Cross-platform compatibility
   - Detailed help documentation

2. **Test Categories**
   - Unit Tests: Component isolation testing
   - Integration Tests: Component interaction testing
   - System Tests: End-to-end workflow testing
   - Acceptance Tests: User scenario testing

## File Structure

```
AudioEditor/
├── src/main/java/com/meenigam/
│   ├── Components/          # Core audio components
│   ├── Panels/             # UI panels and editors
│   ├── Utils/              # Utility classes
│   ├── Frame.java          # Main application window
│   ├── Main.java           # Application entry point
│   └── Manager.java        # State management
├── src/test/java/com/meenigam/
│   ├── unit/               # Unit tests
│   ├── integration/        # Integration tests
│   ├── system/             # System tests
│   └── acceptance/         # Acceptance tests
├── native/                 # C++ native library
├── scripts/                # Development scripts
├── docs/                   # Documentation
├── Makefile               # Build automation
├── pom.xml                # Maven configuration
└── README.md              # Project documentation
```

## Development Workflow

### **Building the Project**

```bash
make build          # Build everything
make native-lib     # Build native library only
make java-compile   # Compile Java code only
```

### **Running Tests**

```bash
make test           # Run all tests
make test-unit      # Run unit tests only
make test-quick     # Run unit + integration tests
```

### **Development**

```bash
make dev            # Clean, build, and run
make debug          # Run in debug mode
make run            # Build and run application
```

<div style="display: flex; flex-direction: column; margin: 0; padding: 0;">
  <h2 style="margin-bottom: 0;">Test Output</h2>
  <hr/>
  <img src="Test-script-Output.png" style="margin-top: 0;" />
</div>

<!-- ## Test Output -->
<!---->
<!-- ![Test Script](Test-script-Output.png) -->

<!-- <center> -->
<!-- <img src="Test-script-Output.png" alt="drawing" /> -->
<!-- </center> -->

## Known Issues

### **Native Library Crashes**

- C++ code experiencing "stoi: no conversion" errors
- Affects TestCallNative during test execution
- Requires investigation of native string parsing
- Separate from Java application functionality

### **Test Environment**

- Some tests may fail due to native library issues
- Java components test successfully in isolation
- Integration tests affected by native crashes
- Application runs correctly outside test environment

## Future Enhancements

### **Immediate Priorities**

1. Fix native library C++ crashes
2. Improve error handling in JNI layer
3. Enhanced test stability
4. Performance optimization

### **Long-term Goals**

1. Additional audio format support
2. Advanced audio effects
3. Plugin architecture
4. Cloud integration
5. Mobile application development

## Conclusion

The Audio Editor project represents a comprehensive approach to desktop audio
application development. With its robust architecture, extensive testing
framework, and user-centric design, it provides a solid foundation for
professional audio editing capabilities.

The recent improvements in user experience, testing infrastructure, and build
system have significantly enhanced the project's maintainability and usability.
The floating audio editing button and enhanced test output demonstrate the
project's commitment to continuous improvement and user satisfaction.

Despite the current native library challenges, the Java application core remains
stable and functional, providing an excellent platform for future development
and enhancement.
