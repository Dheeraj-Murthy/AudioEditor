#include <cmath>
#include <cstdint>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <string>
#include <vector>
using namespace std;

// Struct to store WAV file header information
struct WAVHeader {
    char riffHeader[4];     // "RIFF"
    uint32_t chunkSize;     // Overall size of file in bytes
    char waveHeader[4];     // "WAVE"
    char fmtHeader[4];      // "fmt "
    uint32_t fmtChunkSize;  // Size of the fmt chunk
    uint16_t audioFormat;   // Audio format (1 for PCM)
    uint16_t numChannels;   // Number of channels
    uint32_t sampleRate;    // Sampling frequency (Hz)
    uint32_t byteRate;      // (SampleRate * NumChannels * BitsPerSample) / 8
    uint16_t blockAlign;    // (NumChannels * BitsPerSample) / 8
    uint16_t bitsPerSample; // Bits per sample
    char dataHeader[4];     // "data"
    uint32_t dataSize;      // Size of the data section
};

// Function to convert little-endian to system endianness if needed
template <typename T>
T convertEndian(T value) {
    uint8_t *bytes = reinterpret_cast<uint8_t *>(&value);
    T result = 0;
    for (size_t i = 0; i < sizeof(T); ++i) {
        result |= (static_cast<T>(bytes[i]) << (i * 8));
    }
    return result;
}

// Function to locate the "data" chunk, skipping other chunks like "LIST"
streampos locateDataChunk(ifstream &file, WAVHeader &header) {
    char chunkID[4];
    uint32_t chunkSize;

    while (file.read(chunkID, 4)) {
        file.read(reinterpret_cast<char *>(&chunkSize), sizeof(chunkSize));
        chunkSize = convertEndian(chunkSize); // Convert to host endianness

        if (string(chunkID, 4) == "data") {
            // Copy "data" ID and size into the header struct
            copy(begin(chunkID), end(chunkID), begin(header.dataHeader));
            header.dataSize = chunkSize;
            cout << "Found 'data' chunk. Size: " << chunkSize << " bytes\n";
            return file.tellg();
        } else {
            cout << "Skipping chunk: " << string(chunkID, 4) << " (" << chunkSize << " bytes)\n";
            file.seekg(chunkSize, ios::cur); // Skip this chunk
        }
    }

    throw runtime_error("No 'data' chunk found in the WAV file.");
}

// Function to read the WAV header
streampos readWAVFile(const string &filePath, WAVHeader &header) {
    ifstream file(filePath, ios::binary);
    if (!file.is_open()) {
        throw runtime_error("Failed to open WAV file.");
    }

    // Read the first 12 bytes of the RIFF chunk
    file.read(reinterpret_cast<char *>(&header.riffHeader), 4); // "RIFF"
    file.read(reinterpret_cast<char *>(&header.chunkSize), 4);  // Chunk size
    file.read(reinterpret_cast<char *>(&header.waveHeader), 4); // "WAVE"

    // Convert multibyte fields from little-endian to host endianness
    header.chunkSize = convertEndian(header.chunkSize);

    // Read the "fmt " subchunk
    file.read(reinterpret_cast<char *>(&header.fmtHeader), 4); // "fmt "
    file.read(reinterpret_cast<char *>(&header.fmtChunkSize), 4);
    header.fmtChunkSize = convertEndian(header.fmtChunkSize);

    file.read(reinterpret_cast<char *>(&header.audioFormat), sizeof(header.audioFormat));
    file.read(reinterpret_cast<char *>(&header.numChannels), sizeof(header.numChannels));
    file.read(reinterpret_cast<char *>(&header.sampleRate), sizeof(header.sampleRate));
    file.read(reinterpret_cast<char *>(&header.byteRate), sizeof(header.byteRate));
    file.read(reinterpret_cast<char *>(&header.blockAlign), sizeof(header.blockAlign));
    file.read(reinterpret_cast<char *>(&header.bitsPerSample), sizeof(header.bitsPerSample));

    // Convert multibyte fields
    header.audioFormat = convertEndian(header.audioFormat);
    header.numChannels = convertEndian(header.numChannels);
    header.sampleRate = convertEndian(header.sampleRate);
    header.byteRate = convertEndian(header.byteRate);
    header.blockAlign = convertEndian(header.blockAlign);
    header.bitsPerSample = convertEndian(header.bitsPerSample);

    // Locate the "data" chunk
    streampos pos = locateDataChunk(file, header);

    file.close();

    return pos;
}

// Function to display WAV header information
void displayWAVHeader(const WAVHeader &header) {
    cout << "RIFF Header: " << string(header.riffHeader, 4) << "\n";
    cout << "Chunk Size: " << header.chunkSize << "\n";
    cout << "WAVE Header: " << string(header.waveHeader, 4) << "\n";
    cout << "FMT Header: " << string(header.fmtHeader, 4) << "\n";
    cout << "FMT Chunk Size: " << header.fmtChunkSize << "\n";
    cout << "Audio Format: " << header.audioFormat << "\n";
    cout << "Number of Channels: " << header.numChannels << "\n";
    cout << "Sample Rate: " << header.sampleRate << "\n";
    cout << "Byte Rate: " << header.byteRate << "\n";
    cout << "Block Align: " << header.blockAlign << "\n";
    cout << "Bits Per Sample: " << header.bitsPerSample << "\n";
    cout << "Data Header: " << string(header.dataHeader, 4) << "\n";
    cout << "Data Size: " << header.dataSize << "\n";

    int bytesPerSample = header.bitsPerSample / 8;
    int totalSamples = header.dataSize / bytesPerSample;
    double samples = totalSamples * 1.0;
    double duration = samples / (header.sampleRate * header.numChannels);
    int minutes = static_cast<int>(duration) / 60;
    if (minutes != 0) {
        double seconds = duration - (minutes * 60);
        cout << "Duration: " << minutes << " minutes " << fixed << setprecision(3) << seconds << " seconds\n";
    } else {
        cout << "Duration: " << fixed << setprecision(3) << duration << " seconds\n";
    }
}

// Function to write the WAV file with the looped audio
void writeWAVFile(const string &filePath, const WAVHeader &header, const vector<char> &data) {
    ofstream file(filePath, ios::binary);
    if (!file.is_open()) {
        throw runtime_error("Failed to open output file.");
    }

    // Write the WAV header
    file.write(reinterpret_cast<const char *>(&header.riffHeader), 4);
    file.write(reinterpret_cast<const char *>(&header.chunkSize), 4);
    file.write(reinterpret_cast<const char *>(&header.waveHeader), 4);
    file.write(reinterpret_cast<const char *>(&header.fmtHeader), 4);
    file.write(reinterpret_cast<const char *>(&header.fmtChunkSize), 4);
    file.write(reinterpret_cast<const char *>(&header.audioFormat), sizeof(header.audioFormat));
    file.write(reinterpret_cast<const char *>(&header.numChannels), sizeof(header.numChannels));
    file.write(reinterpret_cast<const char *>(&header.sampleRate), sizeof(header.sampleRate));
    file.write(reinterpret_cast<const char *>(&header.byteRate), sizeof(header.byteRate));
    file.write(reinterpret_cast<const char *>(&header.blockAlign), sizeof(header.blockAlign));
    file.write(reinterpret_cast<const char *>(&header.bitsPerSample), sizeof(header.bitsPerSample));
    file.write(reinterpret_cast<const char *>(header.dataHeader), 4);
    file.write(reinterpret_cast<const char *>(&header.dataSize), sizeof(header.dataSize));

    // Write the looped audio data
    file.write(data.data(), data.size());

    file.close();
}

// Function to loop the audio data
void loopAudio(const string &inputFilePath, int loopCount, const string &outputFilePath) {
    if (loopCount <= 0) {
        throw runtime_error("Loop count must be a positive integer.");
    }

    WAVHeader header;
    streampos pos = readWAVFile(inputFilePath, header);

    // Rdsead the audio data from the "data" chunk
    ifstream file(inputFilePath, ios::binary);
    file.seekg(pos, ios::beg); // Skip the header and "fmt" chunk

    vector<char> audioData(header.dataSize);
    file.read(audioData.data(), audioData.size());
    file.close();

    // Create the looped audio data
    vector<char> loopedData(audioData.size() * loopCount);
    for (int i = 0; i < loopCount; ++i) {
        copy(audioData.begin(), audioData.end(), loopedData.begin() + i * audioData.size());
    }

    // Update the header for the new data size
    WAVHeader outputHeader = header;
    outputHeader.dataSize = loopedData.size();
    outputHeader.chunkSize = sizeof(WAVHeader) + loopedData.size();

    // Write the looped audio to the output WAV file
    writeWAVFile(outputFilePath, outputHeader, loopedData);
}

// Function to trim the audio based on a split time in milliseconds
void trimAudio(const string &inputFilePath, int splitTimeMs, const string &outputFilePath, int choice) {
    WAVHeader header;
    streampos pos = readWAVFile(inputFilePath, header);

    ifstream file(inputFilePath, ios::binary);
    file.seekg(pos, ios::beg); // Skip the header and "fmt" chunk

    // Calculate number of samples to trim based on sample rate and bits per sample
    int bytesPerSample = header.bitsPerSample / 8;
    int totalSamples = header.dataSize / bytesPerSample;
    int splitSample = (splitTimeMs / 1000.0) * header.sampleRate * header.numChannels; // Convert ms to samples

    // Read the audio data
    vector<char> audioData(header.dataSize);
    file.read(audioData.data(), audioData.size());
    file.close();

    // Split the audio into left and right parts
    vector<char> leftPart(audioData.begin(), audioData.begin() + splitSample * bytesPerSample);
    vector<char> rightPart(audioData.begin() + splitSample * bytesPerSample, audioData.end());

    // Write the selected part to the output file
    if (choice == 1) {
        header.dataSize = leftPart.size();
        header.chunkSize = sizeof(WAVHeader) + leftPart.size();
        writeWAVFile(outputFilePath, header, leftPart);
        cout << "Left part saved to: " << outputFilePath << endl;
    } else if (choice == 2) {
        header.dataSize = rightPart.size();
        header.chunkSize = sizeof(WAVHeader) + rightPart.size();
        writeWAVFile(outputFilePath, header, rightPart);
        cout << "Right part saved to: " << outputFilePath << endl;
    } else {
        cout << "Invalid choice.\n";
    }
}

void ampScale(string input, string output, double factor) {

    WAVHeader header;
    streampos pos = readWAVFile(input, header);

    ifstream inFile(input, ios::binary);

    vector<int16_t> aData(header.dataSize / sizeof(int16_t));
    inFile.seekg(pos);
    inFile.read(reinterpret_cast<char *>(aData.data()), header.dataSize);
    inFile.close();

    for (auto &sample : aData) {
        int32_t newSample = static_cast<int32_t>(sample * factor);

        // Capping limits
        if (newSample > 32767)
            newSample = 32767;
        else if (newSample < -32768)
            newSample = -32768;

        sample = static_cast<int16_t>(newSample);
    }

    ofstream outFile(output, ios::binary);

    if (!outFile) {
        std::cerr << "Error opening output file." << std::endl;
        return;
    }

    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(aData.data()), header.dataSize);
    cout << "Output file created successfully." << endl;
    outFile.close();

    return;
}

// Function to dynamically add or reduce bits based on manipulation factor
void frequencyManipulator(const string &input, const string &output, float manipulationFactor) {
    WAVHeader header;
    streampos pos = readWAVFile(input, header);

    if (manipulationFactor <= 0) {
        throw invalid_argument("Manipulation factor must be greater than 0.");
    }

    // Read audio data
    ifstream inFile(input, ios::binary);
    inFile.seekg(pos);
    vector<int16_t> audioData(header.dataSize / sizeof(int16_t));
    inFile.read(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    inFile.close();

    // Adjust audio data
    vector<int16_t> adjustedData;
    if (manipulationFactor > 1) {
        // Downsample: Skip samples
        size_t step = static_cast<size_t>(manipulationFactor);
        for (size_t i = 0; i < audioData.size(); i += step) {
            adjustedData.push_back(audioData[i]);
        }
    } else {
        // Calculate step size for interpolation
        float step = 1 / manipulationFactor;

        for (size_t i = 0; i < audioData.size() - 1; ++i) {
            adjustedData.push_back(audioData[i]); // Add the original sample
            // Calculate and add interpolated samples
            for (float pos = 1; pos < step; ++pos) {
                float fraction = pos / step;
                int16_t interpolated = audioData[i] + static_cast<int16_t>((audioData[i + 1] - audioData[i]) * fraction);
                adjustedData.push_back(interpolated);
            }
        }
    }

    // Update header with new data size
    header.dataSize = adjustedData.size() * sizeof(int16_t);
    header.chunkSize = 36 + header.dataSize;

    // Write to output file
    ofstream outFile(output, ios::binary);
    if (!outFile.is_open()) {
        throw runtime_error("Failed to open output WAV file.");
    }

    outFile.write(reinterpret_cast<char *>(&header), sizeof(WAVHeader));
    outFile.write(reinterpret_cast<char *>(adjustedData.data()), header.dataSize);
    outFile.close();
}

// Function to dynamically add or reduce bits based on manipulation factor
void time_scalar(const string &input, const string &output, int timeToBeScaled) {
    WAVHeader header;
    streampos pos = readWAVFile(input, header);

    // Read audio data
    ifstream inFile(input, ios::binary);
    inFile.seekg(pos);
    vector<int16_t> audioData(header.dataSize / sizeof(int16_t));
    inFile.read(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    inFile.close();

    float manipulationFactor = (audioData.size() * 1000 / header.sampleRate) / (2 * (timeToBeScaled * 1.0f));
    if (manipulationFactor <= 0) {
        throw invalid_argument("Manipulation factor must be greater than 0.");
    }

    cout << manipulationFactor << endl;
    // Adjust audio data
    vector<int16_t> adjustedData;
    if (manipulationFactor > 1) {
        // Downsample: Skip samples
        size_t step = static_cast<size_t>(manipulationFactor);
        for (size_t i = 0; i < audioData.size(); i += step) {
            adjustedData.push_back(audioData[i]);
        }
    } else {
        // Calculate step size for interpolation
        float step = 1 / manipulationFactor;

        for (size_t i = 0; i < audioData.size() - 1; ++i) {
            adjustedData.push_back(audioData[i]); // Add the original sample
            // Calculate and add interpolated samples
            for (float pos = 1; pos < step; ++pos) {
                float fraction = pos / step;
                int16_t interpolated = audioData[i] + static_cast<int16_t>((audioData[i + 1] - audioData[i]) * fraction);
                adjustedData.push_back(interpolated);
            }
        }
    }

    // Update header with new data size
    header.dataSize = adjustedData.size() * sizeof(int16_t);
    header.chunkSize = 36 + header.dataSize;

    // Write to output file
    ofstream outFile(output, ios::binary);
    if (!outFile.is_open()) {
        throw runtime_error("Failed to open output WAV file.");
    }

    outFile.write(reinterpret_cast<char *>(&header), sizeof(WAVHeader));
    outFile.write(reinterpret_cast<char *>(adjustedData.data()), header.dataSize);
    outFile.close();
}

void applyCompression(const string &inputFilePath, const string &outputFilePath,
                      double threshold, double ratio) {
    WAVHeader header;
    streampos pos = readWAVFile(inputFilePath, header);

    double attack = 0.01;
    double release = 0.1;

    vector<int16_t> audioData(header.dataSize / sizeof(int16_t));
    ifstream inFile(inputFilePath, ios::binary);
    inFile.seekg(pos);
    inFile.read(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    inFile.close();

    double gain = 1.0;
    double attackCoeff = exp(-1.0 / (header.sampleRate * attack));
    double releaseCoeff = exp(-1.0 / (header.sampleRate * release));

    for (size_t i = 0; i < audioData.size(); ++i) {
        double sample = audioData[i] / 32768.0; // Normalize to [-1, 1]
        double absSample = fabs(sample);

        if (absSample > threshold) {
            double overThreshold = absSample / threshold;
            double targetGain = pow(overThreshold, -ratio);
            gain = gain < targetGain ? gain + (1 - attackCoeff) * (targetGain - gain)
                                     : gain + (1 - releaseCoeff) * (targetGain - gain);
        } else {
            gain = gain + (1 - releaseCoeff) * (1.0 - gain);
        }

        audioData[i] = static_cast<int16_t>(sample * gain * 32768);
    }

    ofstream outFile(outputFilePath, ios::binary);
    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    outFile.close();

    cout << "Compression applied. Output written to " << outputFilePath << endl;
}

void applyLowPassFilter(const string &inputFilePath, const string &outputFilePath, double cutoffFrequency) {

    WAVHeader header;
    streampos pos = readWAVFile(inputFilePath, header);

    vector<int16_t> audioData(header.dataSize / sizeof(int16_t));
    ifstream inFile(inputFilePath, ios::binary);
    inFile.seekg(pos);
    inFile.read(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    inFile.close();

    double RC = 1.0 / (2 * M_PI * cutoffFrequency);
    double dt = 1.0 / header.sampleRate;
    double alpha = dt / (RC + dt);

    int16_t prevSample = audioData[0];
    for (size_t i = 1; i < audioData.size(); ++i) {
        audioData[i] = static_cast<int16_t>(alpha * audioData[i] + (1 - alpha) * prevSample);
        prevSample = audioData[i];
    }

    ofstream outFile(outputFilePath, ios::binary);
    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    outFile.close();

    cout << "Low-pass filter applied. Output written to " << outputFilePath << endl;
}

void applyHighPassFilter(const string &inputFilePath, const string &outputFilePath, double cutoffFrequency) {

    WAVHeader header;
    streampos pos = readWAVFile(inputFilePath, header);

    vector<int16_t> audioData(header.dataSize / sizeof(int16_t));
    ifstream inFile(inputFilePath, ios::binary);
    inFile.seekg(pos);
    inFile.read(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    inFile.close();

    double RC = 1.0 / (2 * M_PI * cutoffFrequency);
    double dt = 1.0 / header.sampleRate;
    double alpha = RC / (RC + dt);

    int16_t prevSample = audioData[0];
    int16_t prevFiltered = audioData[0];
    for (size_t i = 1; i < audioData.size(); ++i) {
        int16_t currentSample = audioData[i];
        audioData[i] = static_cast<int16_t>(alpha * (prevFiltered + currentSample - prevSample));
        prevSample = currentSample;
        prevFiltered = audioData[i];
    }

    ofstream outFile(outputFilePath, ios::binary);
    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(audioData.data()), header.dataSize);
    outFile.close();

    cout << "High-pass filter applied. Output written to " << outputFilePath << endl;
}

void normalize(string input, string output) {

    WAVHeader header;
    streampos pos = readWAVFile(input, header);

    ifstream inFile(input, ios::binary);

    vector<int16_t> aData(header.dataSize / sizeof(int16_t));
    inFile.seekg(pos);
    inFile.read(reinterpret_cast<char *>(aData.data()), header.dataSize);
    inFile.close();

    int16_t maxSample = 0;
    for (auto &sample : aData) {
        if (abs(sample) > maxSample)
            maxSample = abs(sample);
    }

    cout << maxSample << endl;

    float currDb = 20 * log10(maxSample / 32768.0);
    float ratio = pow(10, (0 - currDb) / 20.0);

    for (auto &sample : aData) {

        int32_t newSample = static_cast<int32_t>(sample * ratio);
        if (newSample > 32767)
            newSample = 32767;
        if (newSample < -32768)
            newSample = -32768;

        sample = static_cast<int16_t>(newSample);
    }

    ofstream outFile(output, ios::binary);

    if (!outFile) {
        std::cerr << "Error opening output file." << std::endl;
        return;
    }

    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(aData.data()), header.dataSize);
    outFile.close();

    return;
}

// Function to apply reverb effect on the audio data
void applyReverb(string input, string output, int reverbLevel) {
    // Read the WAV file
    WAVHeader header;
    streampos pos = readWAVFile(input, header);

    int sampleRate = header.sampleRate;

    // Read the audio data into a vector
    ifstream inputFile(input, ios::binary);
    inputFile.seekg(pos);
    vector<char> audioData(header.dataSize);
    inputFile.read(audioData.data(), header.dataSize);
    inputFile.close();

    // Convert audio data to int16_t
    vector<int16_t> audio(audioData.size() / 2);
    for (size_t i = 0; i < audio.size(); ++i) {
        audio[i] = *reinterpret_cast<int16_t *>(&audioData[i * 2]);
    }

    // Define delay times (in samples) and decay factors for different reverb levels
    int delaySamples = 0;
    float decay = 0.0f;

    if (reverbLevel == 1) {
        delaySamples = static_cast<int>(sampleRate * 0.05); // 50ms delay
        decay = 0.3f;                                       // Low reverb
    } else if (reverbLevel == 2) {
        delaySamples = static_cast<int>(sampleRate * 0.1); // 100ms delay
        decay = 0.5f;                                      // Medium reverb
    } else if (reverbLevel == 3) {
        delaySamples = static_cast<int>(sampleRate * 0.2); // 200ms delay
        decay = 0.7f;                                      // High reverb
    } else {
        throw runtime_error("Invalid reverb level");
    }

    // Apply the reverb effect
    for (size_t i = delaySamples; i < audioData.size(); ++i) {
        // Mix the original sample with a delayed version of itself
        int32_t delayedSample = static_cast<int32_t>(audioData[i - delaySamples]);
        int32_t mixedSample = static_cast<int32_t>(audioData[i]) + static_cast<int32_t>(delayedSample * decay);

        // Ensure the mixed sample is within the 16-bit PCM range
        if (mixedSample > 32767) mixedSample = 32767;
        if (mixedSample < -32768) mixedSample = -32768;

        audioData[i] = static_cast<int16_t>(mixedSample);
    }

    // Write the modified audio data to the output file
    ofstream outFile(output, ios::binary);

    if (!outFile) {
        cerr << "Error opening output file." << endl;
        return;
    }

    outFile.write(reinterpret_cast<char *>(&header), sizeof(header));
    outFile.write(reinterpret_cast<char *>(audio.data()), header.dataSize);
    cout << "Output file created successfully." << endl;
    outFile.close();

    return;
}

void superimposeWAVFiles(
    const string &baseFilePath,
    const string &overlayFilePath,
    const string &outputFilePath,
    int offsetMs) {
    WAVHeader baseHeader, overlayHeader;

    // Read the base WAV file
    streampos pos = readWAVFile(baseFilePath, baseHeader);
    ifstream baseFile(baseFilePath, ios::binary);
    baseFile.seekg(pos); // Seek to the start of the audio data
    vector<char> baseData(baseHeader.dataSize);
    baseFile.read(baseData.data(), baseHeader.dataSize);
    baseFile.close();

    // Read the overlay WAV file
    readWAVFile(overlayFilePath, overlayHeader);
    ifstream overlayFile(overlayFilePath, ios::binary);
    overlayFile.seekg(pos); // Seek to the start of the audio data
    vector<char> overlayData(overlayHeader.dataSize);
    overlayFile.read(overlayData.data(), overlayHeader.dataSize);
    overlayFile.close();

    // Ensure the two files are compatible
    if (baseHeader.sampleRate != overlayHeader.sampleRate ||
        baseHeader.numChannels != overlayHeader.numChannels) {
        throw runtime_error("The sample rate or channel count of the two WAV files do not match.");
    }

    // Calculate offset in bytes
    int offsetBytes = (offsetMs / 1000.0) * baseHeader.byteRate;

    // Ensure the base file can accommodate the overlay at the offset
    if (offsetBytes + overlayData.size() > baseData.size()) {
        baseData.resize(offsetBytes + overlayData.size(), 0); // Extend with silence
        baseHeader.dataSize = baseData.size();
        baseHeader.chunkSize = baseHeader.dataSize + sizeof(WAVHeader) - 8;
    }

    // Convert char data to int16_t (16-bit PCM data)
    vector<int16_t> baseAudio(baseData.size() / 2);
    vector<int16_t> overlayAudio(overlayData.size() / 2);

    // Convert to 16-bit audio samples
    for (size_t i = 0; i < baseAudio.size(); ++i) {
        baseAudio[i] = *reinterpret_cast<int16_t *>(&baseData[i * 2]);
    }

    for (size_t i = 0; i < overlayAudio.size(); ++i) {
        overlayAudio[i] = *reinterpret_cast<int16_t *>(&overlayData[i * 2]);
    }

    // Set a scaling factor for the overlay (for louder overlay)
    float overlayScale = 1.0f; // 1.0 means no scaling, adjust as needed

    // Superimpose the audio data with adjusted scaling for overlay
    for (size_t i = 0; i < overlayAudio.size(); ++i) {
        size_t index = offsetBytes / 2 + i;
        if (index >= baseAudio.size()) break;

        int16_t baseSample = baseAudio[index];
        int16_t overlaySample = overlayAudio[i];

        // Apply scaling to overlay and mix with the base sample
        int32_t mixedSample = baseSample + static_cast<int32_t>(overlaySample * overlayScale);

        // Ensure the mixed sample is within the 16-bit range
        if (mixedSample > 32767) mixedSample = 32767;
        if (mixedSample < -32768) mixedSample = -32768;

        baseAudio[index] = static_cast<int16_t>(mixedSample);
    }

    // Write the superimposed audio to the output file
    ofstream outFile(outputFilePath, ios::binary);

    if (!outFile) {
        std::cerr << "Error opening output file." << std::endl;
        return;
    }

    outFile.write(reinterpret_cast<char *>(&baseHeader), sizeof(baseHeader));
    outFile.write(reinterpret_cast<char *>(baseAudio.data()), baseHeader.dataSize);
    cout << "Output file created successfully." << endl;
    outFile.close();

    return;
    cout << "Superimposed WAV file saved to: " << outputFilePath << endl;
}

void utilityBelt(int input, string inputFilePath, string outputFilePath, vector<string> params) {
    streampos pos;

    cout << "0  : Details\n1  : Loop\n2  : Trim\n3  : Clip Gain\n4  : Frequency Scaling\n5  : Time Scaling\n6  : Compressing\n7  : Audio Filter\n8  : Normalize\n9  : Reverb\n10 : Superimposition\n11 : EXIT" << endl;
    cout << "Enter function: ";
    // cin >> input;

    switch (input) {
        case 0: // Details
            try {
                WAVHeader header;
                pos = readWAVFile(inputFilePath, header);
                streampos headerOffset = ios::cur;
                displayWAVHeader(header);
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;

        case 1: // Loop
            try {
                int loopCount = stoi(params[0]);
                // Loop the audio and write to output file
                loopAudio(inputFilePath, loopCount, outputFilePath);
                cout << "Hwllo";

            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;

        case 2: // Trim
            try {
                int splitTimeMs = stoi(params[0]);
                int choice = stoi(params[1]);

                // Trim the audio file
                trimAudio(inputFilePath, splitTimeMs, outputFilePath, choice);

            } catch (const runtime_error &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;

        case 3: // amp
        {

            double factor = stod(params[0]);

            try {
                ampScale(inputFilePath, outputFilePath, factor);

                // displayWAVHeader(header);
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;
        }

        case 4: // Frequency manupilator

            try {
                float manipulationFactor = stod(params[0]);

                frequencyManipulator(inputFilePath, outputFilePath, manipulationFactor);

            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;

        case 5: // Time Scaler

            try {

                float timeToBeScaled = stof(params[0]);

                frequencyManipulator(inputFilePath, outputFilePath, timeToBeScaled);

            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }

            break;

        case 6: // Compressor
        {

            double threshold = stod(params[0]);
            double ratio = stod(params[1]);

            applyCompression(inputFilePath, outputFilePath, threshold, ratio);
            break;
        }

        case 7: // audiofilter
        {
            double cutoffFrequency = stod(params[0]);
            char filterType = (params[1][0]);

            try {
                if (filterType == 'L' || filterType == 'l') {
                    applyLowPassFilter(inputFilePath, outputFilePath, cutoffFrequency);
                } else if (filterType == 'H' || filterType == 'h') {
                    applyHighPassFilter(inputFilePath, outputFilePath, cutoffFrequency);
                } else {
                    cerr << "Invalid filter type!" << endl;
                }
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }
            break;
        }

        case 8: // normalize
            try {
                normalize(inputFilePath, outputFilePath);

                // displayWAVHeader(header);
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }
            break;

        case 9: // reverb
        {

            int reverbLevel = stoi(params[0]);

            try {
                // Apply reverb
                applyReverb(inputFilePath, outputFilePath, reverbLevel);

                cout << "Reverb applied and file saved to: " << outputFilePath << endl;
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }
            break;
        }

        case 10: // superimposition
        {

            string overlayFilePath;
            int offsetMs;

            cout << "Enter the path to the overlay WAV file: ";
            cin >> overlayFilePath;
            cout << "Enter the offset in milliseconds: ";
            cin >> offsetMs;

            try {
                superimposeWAVFiles(inputFilePath, overlayFilePath, outputFilePath, offsetMs);
            } catch (const exception &e) {
                cerr << "Error: " << e.what() << endl;
            }
            break;
        }
    }

    return;
}

int main1() {
    int n;
    cout << "Enter function: ";
    cin >> n;
    string inputFilePath;
    cout << "Enter the path of the WAV file: ";
    cin >> inputFilePath;

    // utilityBelt(n, inputFilePath, inputFilePath, );
    return 0;
}