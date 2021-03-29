package converter;


import EDflib.EDFException;
import EDflib.EDFreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Konverter für EKG Daten vom EDF Format ins WFDB Format.
 */
public class Converter {
    private EDFreader EDFFile;
    private final FileWriter headerWriter;
    private final RandomAccessFile signal;

    private String recordName;
    private int numberOfSignals;
    private long numberOfSignalsPerSample;

    private int[] initialValues;
    private int[] checksums;


    /**
     * Erzeugt die Dateien in die später geschrieben werden soll (.dat Datei und .hea Datei).
     * Liest die gegebene EDF Datei ein.
     *
     * @param inputFileName Name der Eingabedatei
     * @param outputFileName Name der Ausgabedatei
     * @throws IOException
     * @throws EDFException
     */
    public Converter(String inputFileName, String outputFileName) throws IOException, EDFException {
        getEdfFile(inputFileName);
        File header = new File(outputFileName + ".hea");
        headerWriter = new FileWriter(header);
        signal = new RandomAccessFile(outputFileName + ".dat", "rw");

    }


    /**
     * Konvertiert die gegebene EDF Datei ins WFDB Format.
     *
     * @throws IOException
     * @throws EDFException
     */
    public void convert() throws IOException, EDFException {
        getNonSignalSpecificInformation();
        initialValues = new int[numberOfSignals];
        checksums = new int[numberOfSignals];

        createSignalFile();
        for (int i = 0; i < numberOfSignals; i++) {
            getSignalSpecificInformation(i);
        }

        headerWriter.flush();
        headerWriter.close();
        signal.close();
    }


    /**
     * Entnimmt der eingelesenen EDF Datei alle wichtigen, nicht signalspizifischen, Informationen und schreibt diese in die Header Datei im WFDB Format.
     *
     * @throws EDFException
     * @throws IOException
     */
    private void getNonSignalSpecificInformation() throws EDFException, IOException {
        recordName = EDFFile.getPatient();
        numberOfSignals = EDFFile.getNumSignals();
        double samplingFrequency = EDFFile.getSampleFrequency(0);
        numberOfSignalsPerSample = EDFFile.getTotalSamples(0);

        String baseTime =
                EDFFile.getStartTimeHour() + ":" +
                EDFFile.getStartTimeMinute() + ":" +
                EDFFile.getStartTimeSecond();

        String baseDate =
                EDFFile.getStartDateDay() + "/" +
                EDFFile.getStartDateMonth() + "/" +
                EDFFile.getStartDateYear();

        String firstLine =
                recordName.trim() + " " +
                numberOfSignals + " " +
                samplingFrequency + " " +
                numberOfSignalsPerSample + " " +
                baseTime + " " +
                baseDate + "\n";

        headerWriter.write(firstLine);
    }


    /**
     * Entnimmt der EDF Datei alle signalspezifischen Informationen und schreibt diese in die Header Datei im WFDB Format.
     *
     * @param signalNumber Nummer des Signals
     * @throws EDFException
     * @throws IOException
     */
    private void getSignalSpecificInformation(int signalNumber) throws EDFException, IOException {
        String fileName = recordName.trim() + ".dat";
        int digitalMaximum = EDFFile.getDigitalMaximum(signalNumber);
        int format = Integer.toBinaryString(digitalMaximum).length() + 1;
        double gain = (EDFFile.getPhysicalMaximum(signalNumber) - EDFFile.getPhysicalMinimum(signalNumber)) /
                (EDFFile.getDigitalMaximum(signalNumber) - EDFFile.getDigitalMinimum(signalNumber));
        double ADCGain = 1.0 / gain;
        String baseline = "(0)";
        String unit = "/" + EDFFile.getPhysicalDimension(signalNumber).trim();
        int ADCResolution = Integer.toBinaryString(digitalMaximum).length() + 1;
        int ADCZero = 0;
        int initialValue = initialValues[signalNumber];
        int checksum = checksums[signalNumber];
        int blockSize = 0;
        String description = EDFFile.getSignalLabel(signalNumber);

        String output =
                fileName + " " +
                format + " " +
                ADCGain +
                baseline +
                unit + " " +
                ADCResolution + " " +
                ADCZero + " " +
                initialValue + " " +
                checksum + " " +
                blockSize + " " +
                description + "\n";

        headerWriter.write(output);
    }


    /**
     * entnimmt der EDF Datei alle Signalpegel und schreibt diese in die .dat Datei im WFDB Format.
     *
     * @throws IOException
     * @throws EDFException
     */
    private void createSignalFile() throws IOException, EDFException {
        int[] totalBuffer = new int[(int) (numberOfSignalsPerSample * numberOfSignals)];

        for (int i = 0; i < numberOfSignals; i++) {
            int[] buffer = new int[(int) numberOfSignalsPerSample];
            EDFFile.readDigitalSamples(i, buffer);
            checksums[i] = calculateChecksum(buffer);
            initialValues[i] = buffer[0];

            for (int j = 0; j < buffer.length; j++) {
                totalBuffer[(j * 12) + i] = buffer[j];
            }

        }

        byte[] outputBuffer = new byte[totalBuffer.length * 2];

        for (int i = 0; i < totalBuffer.length; i++) {
            outputBuffer[i * 2] = (byte) (totalBuffer[i] & 0xff);
            outputBuffer[(i * 2) + 1] = (byte) ((totalBuffer[i] >> 8) & 0xff);
        }

        signal.write(outputBuffer, 0, outputBuffer.length);
    }


    /**
     * Liest die EDF Datei mithilfe der Library ein.
     *
     * @param fileName Name der Datei die gelesen werden soll inklusive der .edf Endung
     * @throws IOException
     * @throws EDFException
     */
    private void getEdfFile(String fileName) throws IOException, EDFException {
        EDFFile = new EDFreader(fileName);
    }


    /**
     * Berechnet die 16-bit Prüfsumme für ein Signal und gibt diese zurück.
     *
     * @param buf Array mit allen Amplitudenwerten des Signals
     * @return Prüfsumme als Integer
     */
    private int calculateChecksum(int[] buf) {
        Checksum checksum = new CRC32();

        for (int item : buf) {
            checksum.update(item);
        }

        String value = Long.toBinaryString(checksum.getValue());

        if (value.length() < 32) {
            int numberOfZero = 32 - value.length();
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < numberOfZero; i++) {
                stringBuilder.append(0);
            }

            stringBuilder.append(value);
            return Integer.parseInt(stringBuilder.substring(16, 32), 2);
        }

        return Integer.parseInt(value.substring(16, 32), 2);

    }

}
