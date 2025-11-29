# Audio Editor Setup Guide

## **Table of Contents**
1. [System Requirements](#system-requirements)
2. [Installation Steps](#installation-steps)
3. [IDE Configuration](#ide-configuration)
4. [Native Library Compilation](#native-library-compilation)
5. [Troubleshooting](#troubleshooting)
6. [Platform-Specific Instructions](#platform-specific-instructions)

---

## **System Requirements**

### **Minimum Requirements**
- **Operating System**: Windows 10+, macOS 10.14+, Ubuntu 18.04+
- **Java**: OpenJDK 22 or higher
- **Memory**: 4GB RAM (8GB recommended)
- **Storage**: 500MB free space
- **Processor**: 64-bit CPU

### **Required Software**
- **Java Development Kit (JDK)**: OpenJDK 22+
- **Apache Maven**: 3.6.0+
- **C++ Compiler**: 
  - Linux: g++ 7.0+
  - macOS: Xcode Command Line Tools
  - Windows: MinGW-w64
- **Git**: For version control

---

## **Installation Steps**

### **Step 1: Install Java Development Kit**

#### **macOS**
```bash
# Using Homebrew
brew install openjdk@22

# Set JAVA_HOME
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@22' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

#### **Ubuntu/Debian**
```bash
sudo apt update
sudo apt install openjdk-22-jdk
sudo update-alternatives --config java
```

#### **Windows**
1. Download OpenJDK 22 from [Adoptium](https://adoptium.net/)
2. Run the installer
3. Set JAVA_HOME environment variable
4. Add %JAVA_HOME%\bin to PATH

### **Step 2: Install Maven**

#### **macOS**
```bash
brew install maven
```

#### **Ubuntu/Debian**
```bash
sudo apt install maven
```

#### **Windows**
1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract to a directory (e.g., C:\Program Files\Apache\maven)
3. Set MAVEN_HOME environment variable
4. Add %MAVEN_HOME%\bin to PATH

### **Step 3: Install C++ Compiler**

#### **macOS**
```bash
xcode-select --install
```

#### **Ubuntu/Debian**
```bash
sudo apt install build-essential g++
```

#### **Windows**
1. Download MinGW-w64 from [mingw-w64.org](https://www.mingw-w64.org/)
2. Add MinGW bin directory to PATH

### **Step 4: Clone the Repository**
```bash
git clone https://github.com/Dheeraj-Murthy/AudioEditor.git
cd AudioEditor
```

---

## **IDE Configuration**

### **IntelliJ IDEA Setup**

1. **Open Project**
   - File → Open → Select the AudioEditor directory
   - IntelliJ will detect the Maven project automatically

2. **Configure JDK**
   - File → Project Structure → Project Settings → Project
   - Set Project SDK to JDK 22
   - Set Project language level to 22

3. **Set VM Options**
   - Run → Edit Configurations
   - Add VM options: `-Djava.library.path=native`
   - Apply and OK

4. **Verify Setup**
   - Right-click `Main.java` → Run 'Main.main()'
   - Application should start successfully

### **VS Code Setup**

1. **Install Extensions**
   - Extension Pack for Java
   - Maven for Java

2. **Open Project**
   - File → Open Folder → Select AudioEditor directory

3. **Configure Java**
   - Press Ctrl+Shift+P → "Java: Configure Java Runtime"
   - Select JDK 22

---

## **Native Library Compilation**

### **Automatic Compilation (Recommended)**
```bash
# Use the provided script
./scripts/build_native.sh
```

### **Manual Compilation**

#### **Linux**
```bash
cd native
g++ -shared -fPIC -o libnative.so *.cpp
```

#### **macOS**
```bash
cd native
g++ -shared -fPIC -o libnative.dylib *.cpp
```

#### **Windows**
```bash
cd native
g++ -shared -o native.dll *.cpp
```

### **Verify Compilation**
```bash
# Check if library was created
ls -la native/libnative.*
```

---

## **Running the Application**

### **Method 1: Using IntelliJ IDEA**
1. Open `Main.java`
2. Right-click → Run 'Main.main()'
3. Application should start with the GUI

### **Method 2: Using Command Line**
```bash
# Compile the project
mvn clean compile

# Run with correct library path
java -Djava.library.path=native -cp target/classes com.meenigam.Main
```

### **Method 3: Using Scripts**
```bash
# Build and run in one command
./scripts/build_and_run.sh
```

---

## **Troubleshooting**

### **Common Issues**

#### **1. UnsatisfiedLinkError: Native Library Not Found**
**Problem**: Java cannot find the native library
**Solution**:
```bash
# Check if library exists
ls -la native/libnative.*

# Recompile if missing
./scripts/build_native.sh

# Verify library path
java -Djava.library.path=$(pwd)/native -cp target/classes com.meenigam.Main
```

#### **2. Audio Playback Not Working**
**Problem**: No sound when playing audio
**Solution**:
- Check system volume
- Verify audio file format (WAV only)
- Check console for audio mixer errors
- Try different audio output device

#### **3. Maven Compilation Errors**
**Problem**: Maven cannot compile the project
**Solution**:
```bash
# Clean and recompile
mvn clean compile

# Check Java version
java -version
javac -version

# Update Maven dependencies
mvn dependency:resolve
```

#### **4. GUI Not Displaying Properly**
**Problem**: Interface elements missing or misaligned
**Solution**:
- Check Java Swing compatibility
- Verify display scaling settings
- Try different Look and Feel

#### **5. Permission Issues (Linux/macOS)**
**Problem**: Cannot execute scripts or access files
**Solution**:
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Fix file permissions
chmod -R 755 .
```

### **Debug Mode**

Enable debug logging:
```bash
java -Djava.library.path=native -Ddebug=true -cp target/classes com.meenigam.Main
```

---

## **Platform-Specific Instructions**

### **macOS Specific**

#### **Security Settings**
If you get security warnings:
1. System Preferences → Security & Privacy
2. Allow apps downloaded from: App Store and identified developers
3. Click "Allow Anyway" if prompted

#### **Audio Permissions**
1. System Preferences → Security & Privacy → Privacy
2. Microphone → Allow Terminal/IntelliJ IDEA access

### **Linux Specific**

#### **Audio System**
Install PulseAudio for better audio support:
```bash
sudo apt install pulseaudio pulseaudio-utils
```

#### **Display Issues**
Install required libraries:
```bash
sudo apt install libgtk-3-0 libxrandr2 libasound2
```

### **Windows Specific**

#### **Path Issues**
Ensure paths don't contain spaces or special characters.

#### **Audio Drivers**
Update audio drivers if experiencing playback issues.

---

## **Verification Steps**

After installation, verify everything works:

1. **Java Setup**
   ```bash
   java -version  # Should show JDK 22
   mvn -version   # Should show Maven 3.6+
   ```

2. **Native Library**
   ```bash
   ls -la native/libnative.*  # Should show the compiled library
   ```

3. **Application Launch**
   ```bash
   ./scripts/run_project.sh  # Should start the GUI
   ```

4. **Audio Test**
   - Load a WAV file
   - Click play button
   - Verify audio playback

---

## **Getting Help**

If you encounter issues:

1. **Check Logs**: Look at console output for error messages
2. **Review Documentation**: Read [TESTING.md](TESTING.md) for testing procedures
3. **Check GitHub Issues**: Search existing issues on the repository
4. **Create New Issue**: Provide detailed error information and system details

---

## **Next Steps**

After successful setup:

1. Read [TESTING.md](TESTING.md) to learn about testing procedures
2. Explore the [PROJECT_REPORT.pdf](PROJECT_REPORT.pdf) for detailed project information
3. Start using the audio editor with your own audio files

---

**Last Updated**: November 2025  
**Version**: 1.0  
**Compatible**: Java 22+, Maven 3.6+