package lerad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import common.Hypothesis;
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

    private static final int L = 1000;
    private static final int M = 4;
    private static final int S = 100;
    private static final double TRAIN_VAL_RATE = 9.0 / 10;

    private static class ExAndScore implements Comparable<ExAndScore> {
        public final RawExample e;
        public final double score;

        public ExAndScore(RawExample e, double score) {
            this.e = e;
            this.score = score;
        }

        @Override
        public int compareTo (ExAndScore o) {
            return Double.compare(this.score, o.score);
        }
    }

    public static void predict (RawExampleList dataSet, RawAttrList attrs,
            RuleList rl) {
        for (Rule r : rl) { // Count time from -1.
            r.lastT = -1;
        }
        final ArrayList<ExAndScore> exs = new ArrayList<ExAndScore>();
        for (int i = 0; i < dataSet.size(); i++) {
            final RawExample e = dataSet.get(i);
            double anomalyScore = 0;
            for (Rule r : rl) {
                anomalyScore += r.getTnr(e, attrs, i);
            }
            exs.add(new ExAndScore(e, anomalyScore));
        }
        // Sort descendingly by the anomalyScore.
        Collections.sort(exs, Collections.reverseOrder());

        final int numOfPos =
                TrainTestSplitter.splitSetbyClass(dataSet, attrs)[0].size();
        final int numOfNeg = (dataSet.size() - numOfPos);

        final double falseAlarmThreshold = 0.01;
        int tp = 0;
        int tn = 0;
        int fp = 0;
        int fn = 0;
        final String posClass = attrs.t.valueList.get(0);
        for (int i = 0; i < exs.size(); i++) {
            final double falsePos = ((double) fp) / numOfNeg;
            final boolean isPosPrediction =
                    (Double.compare(falsePos, falseAlarmThreshold) < 0) ? true
                            : false;
            final RawExample e = exs.get(i).e;
            if (e.t.equals(posClass)) { // Positive example.
                if (isPosPrediction) { // Positive prediction.
                    tp++;
                } else { // Negative prediction.
                    fn++;
                }
            } else { // Negative example.
                if (isPosPrediction) { // Positive prediction.
                    fp++;
                } else { // Negative prediction.
                    tn++;
                }
            }
        }
        final double detect = ((double) tp) / (tp + fn);
        final double falsePos = ((double) fp) / (tn + fp);
        System.out.println("detection rate is " + detect);
        System.out.println("false alarm rate is " + falsePos);

    }

    public static Hypothesis learn (RawExampleList dataSet, RawAttrList attrs) {
        final RawExampleList[] tempSet =
                TrainTestSplitter.split(dataSet, attrs, TRAIN_VAL_RATE);
        final RawExampleList train = tempSet[0];
        final RawExampleList val = tempSet[1];

        final double sRate =
                (S >= train.size()) ? 1.0 : (((double) S) / train.size());
        final RawExampleList sTrain =
                TrainTestSplitter.split(train, attrs, sRate)[0];
        final RuleList rl = generateRule(sTrain, attrs, L);
        coverageTest(rl, sTrain, attrs);
        // training pass 2
        updateConsequentsOfRuleList(rl, train, attrs);
        validation(rl, val, attrs);

        for (Rule r : rl) { // Update all n/r rate.
            r.updateNRRate(dataSet, attrs);
        }
        // predict(dataSet, attrs, rl);
        return rl;
    }

    private static RuleList generateRule (RawExampleList sTrain,
            RawAttrList attrs, final int l) {
        assert sTrain.size() >= 2;
        final Random random = new Random();
        final RuleList rl = new RuleList(null, attrs);

        for (int ltimes = 0; ltimes < l; ltimes++) {
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
                    iter.remove();
                    break;
                }
            }
        }
    }

}
