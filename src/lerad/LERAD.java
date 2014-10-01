package lerad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import util.Dbg;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: LERAD.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 23, 2014 9:43:52 PM
 */
public class LERAD {
    public static final String MODULE = "LER";
    public static final boolean DBG = true;

    public int L = 1000;
    public int M = 4;
    public int S = 100;
    public double valRate = 1.0 / 10;

    private static final boolean NO_NEED_WILD_CARD = true;

    /* Learning related begin ******* */
    public RuleList learn (RawExampleList dataSet, RawAttrList attrs) {
        final RawExampleList[] tempSet =
                TrainTestSplitter.split(dataSet, attrs, 1 - valRate);
        final RawExampleList train = tempSet[0];
        final RawExampleList val = tempSet[1];

        final double sRate =
                (S >= train.size()) ? 1.0 : (((double) S) / train.size());
        final RawExampleList sTrain =
                TrainTestSplitter.split(train, attrs, sRate)[0];
        final RuleList rl = generateRule(sTrain, attrs);
        Dbg.print(DBG, MODULE, "After generate:" + Dbg.NEW_LINE + rl.toString());
        coverageTest(rl, sTrain, attrs);
        Dbg.print(DBG, MODULE,
                "After coverageTest:" + Dbg.NEW_LINE + rl.toString());
        // training pass 2
        updateConsequentsOfRuleList(rl, train, attrs);
        Dbg.print(DBG, MODULE,
                "After training pass 2:" + Dbg.NEW_LINE + rl.toString());
        validation(rl, val, attrs);
        Dbg.print(DBG, MODULE,
                "After validation:" + Dbg.NEW_LINE + rl.toString());
        for (Rule r : rl) { // Update all n/r rate.
            r.updateNRRate(dataSet, attrs);
        }
        // predict(dataSet, attrs, rl);
        return rl;
    }

    private RuleList generateRule (RawExampleList sTrain, RawAttrList attrs) {
        assert sTrain.size() >= 2;
        final Random random = new Random();
        final RuleList rl = new RuleList(sTrain.get(0).t, attrs);

        for (int ltimes = 0; ltimes < L; ltimes++) {
            final int index1 = random.nextInt(sTrain.size());
            int index2 = index1;
            while (index2 == index1) {
                index2 = random.nextInt(sTrain.size());
            }
            final RawExample s1 = sTrain.get(index1);
            final RawExample s2 = sTrain.get(index2);
            // Get all matching attributes.
            final LinkedList<Integer> identicalA = new LinkedList<Integer>();
            for (int attrIndex = 0; attrIndex < s1.xList.size(); attrIndex++) {
                final String s1a = s1.xList.get(attrIndex);
                final String s2a = s2.xList.get(attrIndex);
                if (s1a.equals(s2a)) {
                    identicalA.add(attrIndex);
                }
            }

            int m = 0;
            while (!identicalA.isEmpty() && m < M) {
                final int attrIndex =
                        identicalA.remove(random.nextInt(identicalA.size()));
                final String a = s1.xList.get(attrIndex);
                final RuleCondition cond =
                        new RuleCondition(attrs.xList.get(attrIndex).name, a,
                                RuleCondition.OPT_EQ);
                final Rule r;
                if (m == 0) {
                    r = new Rule(cond);
                } else {
                    r = new Rule(rl.getLast());
                    r.add(cond);
                }
                rl.add(r);
                m++;
            }

        }
        if (NO_NEED_WILD_CARD) {
            // Delete all wild card.
            Iterator<Rule> iter = rl.iterator();
            while (iter.hasNext()) {
                final Rule r = iter.next();
                if (r.isEmpty()) {
                    iter.remove();
                }
            }
        }
        return rl;
    }

    private static void coverageTest (RuleList rl, RawExampleList sTrain,
            RawAttrList attrs) {
        updateConsequentsOfRuleList(rl, sTrain, attrs);
        for (Rule r : rl) { // Get all n/r rate.
            r.updateNRRate(sTrain, attrs);
        }
        // Sort descendingly by n/r rate.
        Collections.sort(rl, Collections.reverseOrder());

        final HashSet<RuleCondition> values = new HashSet<RuleCondition>();
        final Iterator<Rule> iter = rl.iterator();
        while (iter.hasNext()) {
            final Rule r = iter.next();
            boolean hasNewValue = false;
            for (RuleCondition c : r.consequents) {
                if (values.add(c)) {
                    hasNewValue = true;
                }
            }
            if (!hasNewValue) {
                iter.remove();
            }
        }
    }

    private static void updateConsequentsOfRuleList (RuleList rl,
            RawExampleList train, RawAttrList attrs) {
        for (Rule r : rl) {
            final int attrIndex =
                    attrs.indexOf(r.consequents.iterator().next().name);
            // Update all possible values occur in the train set for this
            // attribute.
            for (RawExample e : train) {
                if (r.isSatisfiedByAntecedent(e, attrs)) {
                    final String value = e.xList.get(attrIndex);
                    final RuleCondition c =
                            new RuleCondition(attrs.xList.get(attrIndex).name,
                                    value, RuleCondition.OPT_EQ);
                    r.consequents.add(c);
                }
            }
        }
    }

    private static void validation (RuleList rl, RawExampleList val,
            RawAttrList attrs) {
        final Iterator<Rule> iter = rl.iterator();
        while (iter.hasNext()) {
            final Rule r = iter.next();
            for (RawExample e : val) {
                if (r.isViolation(e, attrs)) {
                    Dbg.print(DBG, MODULE, "Validation: remove " + r.toString()
                            + " for example: " + e.toString());
                    iter.remove();
                    break;
                }
            }
        }
    }

    /* Learning related end ******* */

    /* Prediction related begin ******* */
    private static class ExAndScore implements Comparable<ExAndScore> {
        public final RawExample e;
        public final double score;
        public final ArrayList<Double[]> scoreDetail;
        public String result = null;

        public ExAndScore(RawExample e, double score,
                ArrayList<Double[]> scoreDetail) {
            this.e = e;
            this.score = score;
            this.scoreDetail = scoreDetail;
        }

        @Override
        public int compareTo (ExAndScore o) {
            return Double.compare(this.score, o.score);
        }

        @Override
        public String toString () {
            final StringBuilder sb = new StringBuilder();
            sb.append(e.toString() + " ");
            if (result != null) {
                sb.append(result + " ");
            }
            sb.append(String.format("%.2f ", score));
            for (Double[] d : scoreDetail) {
                sb.append(String.format("R%.0f %.0f*%.2f ", d[0], d[1], d[2]));
            }
            return sb.toString();
        }
    }

    private static String getExStrForDbg (final ArrayList<ExAndScore> exs) {
        final StringBuilder sb = new StringBuilder();
        for (ExAndScore e : exs) {
            sb.append(e);
            sb.append(Dbg.NEW_LINE);
        }
        return sb.toString();
    }

    public static double[][] getAUCby1PercentFp (RawExampleList dataSet,
            RawAttrList attrs, RuleList rl, double falsePosThreshold,
            boolean multiTest) {
        for (Rule r : rl) { // Count time from -1.
            r.lastT = -1;
        }
        final ArrayList<ExAndScore> exs = new ArrayList<ExAndScore>();
        for (int i = 0; i < dataSet.size(); i++) {
            final RawExample e = dataSet.get(i);
            double anomalyScore = 0;
            final ArrayList<Double[]> scoreDetail = new ArrayList<Double[]>();
            for (int j = 0; j < rl.size(); j++) {
                final Rule r = rl.get(j);
                final double[] score = r.getTnr(e, attrs, i);
                if (Double.compare(score[0], 0) > 0) {
                    anomalyScore += score[0];
                    scoreDetail.add(new Double[] { (double) j,
                            (double) score[1], r.nrRate });
                }
            }
            exs.add(new ExAndScore(e, anomalyScore, scoreDetail));
        }
        // Sort descendingly by the anomalyScore.
        Collections.sort(exs, Collections.reverseOrder());

        int numOfPos = -1;
        for (RawExampleList eArray : TrainTestSplitter.splitSetbyClass(dataSet,
                attrs)) {
            if (rl.posClass.equals(eArray.get(0).t)) {
                numOfPos = eArray.size();
                break;
            }
        }
        assert numOfPos != -1;
        final int numOfNeg = (dataSet.size() - numOfPos);

        if (multiTest) {// Return fp and detect.
            return multiFpResult(exs, attrs, numOfNeg, rl.posClass);
        } else { // Return fp and auc.
            final DetectAndFalsePos daf =
                    getDAF(exs, attrs, numOfNeg, falsePosThreshold, rl.posClass);
            final double auc = daf.falsePos * daf.detect / 2;

            Dbg.print(DBG, MODULE, "Result of examples:" + Dbg.NEW_LINE
                    + getExStrForDbg(exs));
            Dbg.print(DBG, MODULE, "detection rate is " + daf.detect);
            Dbg.print(DBG, MODULE, "false alarm rate is " + daf.falsePos);
            Dbg.print(DBG, MODULE, "AUC is " + auc);
            return new double[][] { { daf.falsePos, auc } };
        }
    }

    private static final double[] FP_RATES = new double[] { 0.01, 0.1, 0.2,
            0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

    private static double[][] multiFpResult (ArrayList<ExAndScore> exs,
            RawAttrList attrs, int numOfNeg, String posClass) {
        Dbg.print(DBG, MODULE, "Result of examples:" + Dbg.NEW_LINE
                + getExStrForDbg(exs));
        final double[][] auc = new double[FP_RATES.length][2];
        final double[][] fads = new double[FP_RATES.length][2];
        DetectAndFalsePos lastDaf = null;
        for (int i = 0; i < FP_RATES.length; i++) {
            final double fprth = FP_RATES[i];
            final DetectAndFalsePos daf =
                    getDAF(exs, attrs, numOfNeg, fprth, posClass);
            fads[i][0] = daf.falsePos;
            fads[i][1] = daf.detect;
            auc[i] = new double[2];
            auc[i][0] = daf.falsePos;
            if (i == 0) {
                auc[i][1] = daf.falsePos * daf.detect / 2;
            } else {
                final double h = daf.falsePos - lastDaf.falsePos;
                auc[i][1] =
                        auc[i - 1][1] + ((daf.detect + lastDaf.detect) * h / 2);
            }

            lastDaf = daf;
            Dbg.print(DBG, MODULE, String.format(
                    "fp %.4f detection %.4f auc %.4f", daf.falsePos,
                    daf.detect, auc[i][1]));
        }

        return fads;
    }

    private static class DetectAndFalsePos {
        public final double detect;
        public final double falsePos;

        public DetectAndFalsePos(final double detect, final double falsePos) {
            this.detect = detect;
            this.falsePos = falsePos;
        }
    }

    private static DetectAndFalsePos getDAF (ArrayList<ExAndScore> exs,
            RawAttrList attrs, int numOfNeg, double falsePosThreshold,
            String posClass) {
        int tp = 0;
        int tn = 0;
        int fp = 0;
        int fn = 0;
        for (int i = 0; i < exs.size(); i++) {
            final double falsePosRate = ((double) fp) / numOfNeg;
            final boolean isPosPrediction =
                    (Double.compare(falsePosRate, falsePosThreshold) < 0) ? true
                            : false;
            final ExAndScore eas = exs.get(i);
            if (eas.e.t.equals(posClass)) { // Positive example.
                if (isPosPrediction) { // Positive prediction.
                    eas.result = "tp";
                    tp++;
                } else { // Negative prediction.
                    eas.result = "fn";
                    fn++;
                }
            } else { // Negative example.
                if (isPosPrediction) { // Positive prediction.
                    eas.result = "fp";
                    fp++;
                } else { // Negative prediction.
                    eas.result = "tn";
                    tn++;
                }
            }
        }
        final double detect = ((double) tp) / (tp + fn);
        final double falsePos = ((double) fp) / (tn + fp);
        return new DetectAndFalsePos(detect, falsePos);
    }
    /* Prediction related end ******* */
}
