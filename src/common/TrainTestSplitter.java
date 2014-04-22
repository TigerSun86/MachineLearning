package common;

import java.util.Collections;
import java.util.Random;

/**
 * FileName: TrainTestSplitter.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 20, 2014 11:45:34 PM
 */
public class TrainTestSplitter {
    public static final double DEFAULT_RATIO = 0.667;
    
/*    private static final String FILE =
            "http://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/wdbc.data";*/
    private static final String FILE =
            "http://archive.ics.uci.edu/ml/machine-learning-databases/wine/wine.data";
   
    public static void main (String[] args) {
        final String fName = deleteCommaAndPutClassBack(FILE);
        generateTrainTest(fName);
    }

    private static String deleteIDAndCommaAndPutClassBack (final String fileName) {
        final RawExampleList exs = new RawExampleList();
        final DataReader in = new DataReader(fileName);
        int numOfAttr = -1;
        while (true) {
            final String line = in.nextLine();
            if (line == null) {
                break;
            }
            if (line.length() <= 1) {
                continue; // Skip empty line.
            }
            final String[] examStr = line.split(",");
            final RawExample ex = new RawExample();
            // 0 is id, 1 is class.
            for (int i = 2; i <= examStr.length - 1; i++) {
                ex.xList.add(examStr[i]);
            }
            ex.t = examStr[1];

            if (numOfAttr == -1) { // Initialize.
                numOfAttr = ex.xList.size();
            } else if (numOfAttr == ex.xList.size()) {
                exs.add(ex);
            } else {
                System.err.println("Inconsistent number of attributes in line "
                        + (in.getLineNumber() + 1));
            }
        } // End of while (true) {
        in.close();

        final String name =
                fileName.substring(fileName.lastIndexOf('/') + 1,
                        fileName.lastIndexOf('.'));
        final String resourcePath =
                Thread.currentThread().getContextClassLoader().getResource("")
                        .toString();
        final String newName = resourcePath + name + ".txt";
        System.out.println(newName);
        writeExamples(exs, newName);
        return newName;
    }
    private static String deleteCommaAndPutClassBack (final String fileName) {
        final RawExampleList exs = new RawExampleList();
        final DataReader in = new DataReader(fileName);
        int numOfAttr = -1;
        while (true) {
            final String line = in.nextLine();
            if (line == null) {
                break;
            }
            if (line.length() <= 1) {
                continue; // Skip empty line.
            }
            final String[] examStr = line.split(",");
            final RawExample ex = new RawExample();
            // 0 is class.
            for (int i = 1; i <= examStr.length - 1; i++) {
                ex.xList.add(examStr[i]);
            }
            ex.t = examStr[0];

            if (numOfAttr == -1) { // Initialize.
                numOfAttr = ex.xList.size();
            } else if (numOfAttr == ex.xList.size()) {
                exs.add(ex);
            } else {
                System.err.println("Inconsistent number of attributes in line "
                        + (in.getLineNumber() + 1));
            }
        } // End of while (true) {
        in.close();

        final String name =
                fileName.substring(fileName.lastIndexOf('/') + 1,
                        fileName.lastIndexOf('.'));
        final String resourcePath =
                Thread.currentThread().getContextClassLoader().getResource("")
                        .toString();
        final String newName = resourcePath + name + ".txt";
        System.out.println(newName);
        writeExamples(exs, newName);
        return newName;
    }

    private static void generateTrainTest (final String fileName) {
        final RawExampleList exs = new RawExampleList(fileName);
        Collections.shuffle(exs); // Shuffle examples.
        final RawExampleList[] exs2 = split(exs, DEFAULT_RATIO);
        final RawExampleList train = exs2[0];
        final RawExampleList test = exs2[1];

        final String name =
                fileName.substring(fileName.lastIndexOf('/') + 1,
                        fileName.lastIndexOf('.'));

        final String resourcePath =
                Thread.currentThread().getContextClassLoader().getResource("")
                        .toString();
        final String trainName = resourcePath + name + "-train.txt";
        final String testName = resourcePath + name + "-test.txt";
        writeExamples(train, trainName);
        System.out.println(trainName);
        writeExamples(test, testName);
        System.out.println(testName);
    }

    private static void writeExamples (final RawExampleList exs,
            final String fileName) {
        DataWriter out = new DataWriter(fileName);
        if (out.hasNoException()) {
            out.write(exs.toString());
            out.close();
        }
    }

    /**
     * public static RawExampleList[] split(RawExampleList exs, double ratio)
     * 
     * Splits given RawExampleList into 2 lists by given ratio in percentage.
     * The first output RawExampleList has the number of examples of the ratio,
     * the second one has remaining examples. Splits examples randomly.
     * 
     * @return: An array with 2 ExampleSets.
     */
    public static RawExampleList[] split (final RawExampleList exs,
            final double ratio) {
        final RawExampleList[] exArray = new RawExampleList[2];
        if (Double.compare(ratio, 1) >= 0) { // Special case.
            exArray[0] = exs;
            exArray[1] = new RawExampleList();
            return exArray;
        } else if (Double.compare(ratio, 0) <= 0) { // Special case.
            exArray[0] = new RawExampleList();
            exArray[1] = exs;
            return exArray;
        }

        exArray[0] = new RawExampleList();
        exArray[1] = new RawExampleList();

        final Random ran = new Random(); // Randomly split example set.
        final int numOfFirst = (int) Math.round(exs.size() * ratio);
        final int numOfSecond = exs.size() - numOfFirst;
        for (RawExample e : exs) {
            if (exArray[0].size() >= numOfFirst) {
                // Already added enough examples into exArray[0].
                exArray[1].add(e);
            } else if (exArray[1].size() >= numOfSecond) {
                // Already added enough examples into exArray[1].
                exArray[0].add(e);
            } else {
                if (Double.compare(ran.nextDouble(), ratio) < 0) {
                    // Probability of ratio to add example into exArray[0].
                    exArray[0].add(e);
                } else { // Remain probability for exArray[1].
                    exArray[1].add(e);
                }
            }
        }

        return exArray;
    }
}
