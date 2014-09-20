package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FileName: AccuracyFileReader.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 16, 2014 6:29:32 PM
 */
public class AccuracyFileReader {
    private static final String ACCURACY = "accur ([0-9]\\.[0-9]+)";
    private static final String SIZE = "size ([0-9]+)";
    public static void main (String[] args) throws IOException {
        extractAccuracy(args[0],SIZE);
    }

    private static void extractAccuracy (final String filepath,
            final String regExp) throws IOException {
        final BufferedReader reader =
                new BufferedReader(new FileReader(filepath));
        boolean isRunning = true;
        boolean isData = false;
        String dataSet = null;
        while (isRunning) {
            final String line = reader.readLine();
            if (line == null) {
                isRunning = false;
            } else if (line.startsWith("Data set:")) {
                dataSet = line.split(": ")[1];
            } else if (line.startsWith("Noise: 0.00")) {
                isData = true;
            } else if (isData) {
                if (line.matches("^([0-9A-Za-z]+):.*")
                        && !line.startsWith("Noise:")) {
                    printData(line, dataSet, regExp);
                } else {
                    isData = false;
                }
            }
        }
        reader.close();
    }

    private static void printData (final String line, final String dataSet,
            final String regExp) {
        Pattern pattern = Pattern.compile("([A-Za-z0-9]+):");
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        final String alg = matcher.group(1);

        Pattern pattern2 = Pattern.compile("size ([0-9]+)");
        matcher = pattern2.matcher(line);
        while (matcher.find()) {
            final String AccurOrSize = matcher.group(1);
            System.out.printf("%s %s %s%n", alg, dataSet, AccurOrSize);
        }
    }
}
