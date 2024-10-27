#include <cstring>
#include <fstream>
#include <iostream>
#include <vector>
using namespace std;

#pragma pack(push, 1)
struct WAVheader {
  char riff[4];           // "RIFF"
  uint32_t fileSize;      // File size in bytes
  char wave[4];           // "WAVE"
  char fmt[4];            // "fmt "
  uint32_t fmtSize;       // Size of the fmt chunk
  uint16_t audioFormat;   // Audio format (1 for PCM)
  uint16_t numChannels;   // Number of channels
  uint32_t sampleRate;    // Sample rate
  uint32_t byteRate;      // Byte rate
  uint16_t blockAlign;    // Block align
  uint16_t bitsPerSample; // Bits per sample
  char data[4];           // "data"
  uint32_t dataSize;
};
#pragma pack(pop)

void amp(string input, string output, float factor) {

  ifstream inFile(input, ios::binary);
  if (!inFile) {
    cerr << "Error opening input file." << endl;
    return;
  }

  WAVheader header;
  inFile.read(reinterpret_cast<char *>(&header), sizeof(header));

  if (strncmp(header.riff, "RIFF", 4) != 0 ||
      strncmp(header.wave, "WAVE", 4) != 0) {
    cerr << "Not a valid WAV file." << endl;
    return;
  }

  vector<int16_t> aData(header.dataSize / sizeof(int16_t));
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
  outFile.close();

  return;
}

int main(int argc, char *argv[]) {

  // Check if the correct number of arguments is provided
  if (argc != 2) {
    std::cerr << "Usage: " << argv[0] << " <filename.wav>" << std::endl;
    return 1; // Exit with an error code
  }

  // Get the filename from the command-line argument
  const string input_file_path = argv[1];
  string output_file_path;
  float factor;

  cout << "Enter name of output file (without extension): ";
  cin >> output_file_path;
  output_file_path += ".wav";

  cout << "Enter amplitude factor: ";
  cin >> factor;

  amp(input_file_path, output_file_path, factor);
  cout << "Amplitude increased successfully." << endl;

  return 0;
}
