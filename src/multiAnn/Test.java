package multiAnn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;

import util.SysUtil;
import clustering.AHClustering;
import clustering.BisectingKmeans;
import clustering.DbscanClustering;
import clustering.EnemyCluster;
import clustering.Kmeans;
import common.Evaluator;
import common.Hypothesis;
import common.Learner;
import common.MappedAttrList;
import common.RawAttrList;
import common.RawExampleList;
import common.TrainTestSplitter;
import dataset.*;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 18, 2014 3:16:30 PM
 */
public class Test {
    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-attr.txt";
    /* private static final String TRAIN_FILE_URL =
     * "http://my.fit.edu/~sunx2013/MachineLearning/toyXor400-train.txt";
     * private static final String TEST_FILE_URL =
     * "http://my.fit.edu/~sunx2013/MachineLearning/toyXor400-test.txt"; */
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy4border-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy4border-test.txt";

    private static final Learner[] LEARNERS =
            {
                    new AnnLearner2(),
                    new MAnnLearner(new EnemyCluster(new DbscanClustering(3,
                            0.1, true, null))),
                    new MAnnLearner(new EnemyCluster(new DbscanClustering(3,
                            0.3, true, null))),
                    new MAnnLearner(new EnemyCluster(new BisectingKmeans(
                            BisectingKmeans.WayToPick.LARGEST, 6, 2, null))),
                    new MAnnLearner(new EnemyCluster(new BisectingKmeans(
                            BisectingKmeans.WayToPick.LARGEST, 6, 3, null))),
                    new MAnnLearner(new BisectingKmeans(
                            BisectingKmeans.WayToPick.LARGEST, 6, 2, null)),
                    new MAnnLearner(new BisectingKmeans(
                            BisectingKmeans.WayToPick.LARGEST, 6, 3, null)),
                    new MAnnLearner(new BisectingKmeans(
                            BisectingKmeans.WayToPick.LARGEST, 6, 4, null)),
                    new MAnnLearner(new AHClustering(AHClustering.Mode.UPGMA,
                            2, null)),
                    new MAnnLearner(new AHClustering(AHClustering.Mode.UPGMA,
                            3, null)),
                    new MAnnLearner(new AHClustering(AHClustering.Mode.UPGMA,
                            4, null)), new MAnnLearner(new Kmeans(2, null)),
                    new MAnnLearner(new Kmeans(3, null)),
                    new MAnnLearner(new Kmeans(4, null)),
                    new MAnnLearner(new DbscanClustering(3, 0.1, true, null)),
                    new MAnnLearner(new DbscanClustering(3, 0.3, true, null)),
                    new MAnnLearner(new DbscanClustering(3, 0.5, true, null)) };

    private static final String[] ALG_NAMES = { "ann", "ec-dbscan-0.1",
            "ec-dbscan-0.3", "ec-bkmeans-2", "ec-bkmeans-3", "bkmeans-2",
            "bkmeans-3", "bkmeans-4", "ah-upgma-2", "ah-upgma-3", "ah-upgma-4",
            "kmeans-2", "kmeans-3", "kmeans-4", "dbscan-0.1", "dbscan-0.3",
            "dbscan-0.5" };

    private static void multiTest (DataSet set) {
        final RawAttrList rawAttr = new RawAttrList(set.getAttrFileUrl());
        final RawExampleList originalExs =
                new RawExampleList(set.getDataFileUrl());
        // Map all attributes in range 0 to 1.
        final MappedAttrList mAttr = new MappedAttrList(originalExs, rawAttr);

        final PrintStream ps = System.out;
        try {

            final String dbgFileName =
                    Thread.currentThread().getContextClassLoader()
                            .getResource("").toString()
                            + set.getName() + ".txt";
            System.out.println("Writing result into " + dbgFileName);
            final File file = new File(new URL(dbgFileName).getPath());
            if (!file.exists()) {
                file.createNewFile();
            }
            System.setOut(new PrintStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out
                .printf("DataSet, Fold, AlgName, Parameter, Accuracy, Num of nn, "
                        + "Training Time, Predicting Time%n");

        final String[] trains = set.getKFoldCVTrains();
        final String[] tests = set.getKFoldCVTests();
        for (int fold = 0; fold < trains.length; fold++) {
            // int fold = 0;
            // Rescale (map) all data in range 0 to 1.
            final RawExampleList train =
                    mAttr.mapExs(new RawExampleList(trains[fold]), rawAttr);
            final RawExampleList test =
                    mAttr.mapExs(new RawExampleList(tests[fold]), rawAttr);
            testOneFold(train, test, rawAttr, set.getName(), fold);
        }

        System.out.close();
        System.setOut(ps);
        System.out.println("Writing finished");
    }

    private static void testOneFold (final RawExampleList train,
            final RawExampleList test, final RawAttrList attrs,
            final String setName, final int fold) {
        assert LEARNERS.length == ALG_NAMES.length;
        for (int algIdx = 0; algIdx < LEARNERS.length; algIdx++) {
            final Learner learner = LEARNERS[algIdx];
            final String algName = ALG_NAMES[algIdx];

            final long time1 = SysUtil.getCpuTime();
            final Hypothesis h = learner.learn(train, attrs);
            final long time2 = SysUtil.getCpuTime();
            final long trainingTime = (time2 - time1);

            final double accur = Evaluator.evaluate(h, test);
            final long time3 = SysUtil.getCpuTime();
            final long predictingTime = (time3 - time2);

            int numOfnn = 1;
            if (h instanceof MultiAnn) {
                numOfnn = ((MultiAnn) h).nnlist.size();
            }
            final String[] nameAndPara = getNameAndPara(algName);
            System.out.printf("%s, %d, %s, %s, %.4f, %d, %d, %d%n", setName,
                    fold, nameAndPara[0], nameAndPara[1], accur, numOfnn,
                    trainingTime, predictingTime);
        }
    }

    private static String[] getNameAndPara (String algName) {
        if (algName.equals("ann")) {
            return new String[] { "ann", "-1" };
        } else {
            final int idxD = algName.lastIndexOf('-');
            return new String[] { algName.substring(0, idxD),
                    algName.substring(idxD + 1, algName.length()) };
        }
    }

    public static void test (final String dataFile, final String attrFile) {
        final RawAttrList rawAttr = new RawAttrList(attrFile);
        final RawExampleList originalExs = new RawExampleList(dataFile);
        // Map all attributes in range 0 to 1.
        final MappedAttrList mAttr = new MappedAttrList(originalExs, rawAttr);
        // Rescale (map) all data in range 0 to 1.
        final RawExampleList exs = mAttr.mapExs(originalExs, rawAttr);

        for (int i = 0; i < 10; i++) {
            Collections.shuffle(exs); // Shuffle examples.
            final RawExampleList[] exs2 =
                    TrainTestSplitter.split(exs, rawAttr,
                            TrainTestSplitter.DEFAULT_RATIO);
            final RawExampleList train = exs2[0];
            final RawExampleList test = exs2[1];

            final Hypothesis h =
                    new MAnnLearner(new EnemyCluster(new DbscanClustering(3,
                            0.1, true, null))).learn(train, rawAttr);

            final double accur = Evaluator.evaluate(h, test);
            System.out.print("Mann " + accur);

            final Hypothesis h2 = new AnnLearner2().learn(train, rawAttr);
            final double accur2 = Evaluator.evaluate(h2, test);
            System.out.println(" Ann " + accur2);
        }
    }

    public static void toytest () {
        final RawAttrList rawAttr = new RawAttrList(ATTR_FILE_URL);

        final RawExampleList train = new RawExampleList(TRAIN_FILE_URL);

        RawExampleList test = new RawExampleList(TEST_FILE_URL);

        final Hypothesis h =
                new MAnnLearner(new BisectingKmeans(
                        BisectingKmeans.WayToPick.LARGEST, 6, 2, null)).learn(
                        train, rawAttr);

        final double accur = Evaluator.evaluate(h, test);
        System.out.print("Mann " + accur);

        final Hypothesis h2 = new AnnLearner2().learn(train, rawAttr);
        final double accur2 = Evaluator.evaluate(h2, test);
        System.out.println(" Ann " + accur2);

    }

    static DataSet set = new Iris();

    public static void main (String[] args) {
        // Dbg.dbgSwitch = true;
        // Dbg.defaultSwitch = true;
        // test(set.getTrainFileUrl(), set.getAttrFileUrl());
        // toytest();
        multiTest(set);
    }
}
