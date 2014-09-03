package ripperk;

import java.util.Arrays;
import java.util.HashSet;

import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: RIPPERk.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 2:23:04 PM
 */
public class RIPPERk {
    private static class Node implements Comparable<Node> {
        public final RawExampleList exs;

        public Node(RawExampleList exs) {
            this.exs = exs;
        }

        @Override
        public int compareTo (Node o) { // Ascending order by size.
            return this.exs.size() - o.exs.size();
        }

    }

    public static RuleList learn (RawExampleList exs, final RawAttrList attrs) {
        final RawExampleList[] subSets =
                TrainTestSplitter.splitSetbyClass(exs, attrs);
        final Node[] ascendingSets = new Node[subSets.length];
        for (int i = 0; i < subSets.length; i++) {
            ascendingSets[i] = new Node(subSets[i]);
        }
        Arrays.sort(ascendingSets);

        final RuleList ruleList = new RuleList(attrs);

        // Create rule set for each class, except for last class (default).
        for (int i = 0; i < ascendingSets.length - 1; i++) {
            final RawExampleList pos = new RawExampleList();
            pos.addAll(ascendingSets[i].exs);
            // All remaining classes are neg set.
            final RawExampleList neg = new RawExampleList();
            for (int j = i + 1; j < ascendingSets.length; j++) {
                neg.addAll(ascendingSets[j].exs);
            }
            final RuleList subRuleList = learnTwoClass(pos, neg, attrs);
            ruleList.addAll(subRuleList); // Add result to total rule list.
        }
        // The class has highest # of instances is the default class.
        final String def = ascendingSets[ascendingSets.length - 1].exs.get(0).t;
        ruleList.setDefault(def);

        return ruleList;
    }

    private static final double GROW_RATE = 2.0 / 3;

    /**
     * Assume # of pos is less than # of neg.
     */
    private static RuleList learnTwoClass (final RawExampleList posIn,
            final RawExampleList negIn, final RawAttrList attrs) {
        RawExampleList pos = new RawExampleList();
        pos.addAll(posIn);
        RawExampleList neg = new RawExampleList();
        neg.addAll(negIn);

        final RuleList ruleList = new RuleList(attrs);
        boolean isRunning = true;
        while (!pos.isEmpty() && isRunning) {
            final RawExampleList[] subPos =
                    TrainTestSplitter.splitSetWithConsistentClassRatio(pos,
                            attrs, GROW_RATE);
            final RawExampleList growPos = subPos[0];
            final RawExampleList prunePos = subPos[1];
            final RawExampleList[] subNeg =
                    TrainTestSplitter.splitSetWithConsistentClassRatio(neg,
                            attrs, GROW_RATE);
            final RawExampleList growNeg = subNeg[0];
            final RawExampleList pruneNeg = subNeg[1];

            Rule r = growRule(growPos, growNeg, attrs);
            r = pruneRule(r, prunePos, pruneNeg);

            if (needQuit(r, prunePos, pruneNeg)) {
                isRunning = false;
            } else {
                ruleList.add(r);
                // Remove examples covered by rule from pos/neg.
                pos = getUncoveredExs(r, pos, attrs);
                neg = getUncoveredExs(r, neg, attrs);
            }
        }

        // The default class setting here is useless, just for format. The upper
        // method will assign default class. Here just using the class of first
        // example in neg set.
        final String def = negIn.get(0).t;
        ruleList.setDefault(def);

        return ruleList;
    }

    private static RawExampleList getUncoveredExs (final Rule r,
            final RawExampleList exs, final RawAttrList attrs) {
        final RawExampleList newExs = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            final String prediction = r.rulePredict(exs.get(i).xList, attrs);
            if (prediction == null) { // Not covered by rule.
                newExs.add(exs.get(i));
            }
        }
        return newExs;
    }

    private static boolean needQuit (Rule r, RawExampleList prunePos,
            RawExampleList pruneNeg) {
        // TODO Auto-generated method stub
        return false;
    }

    private static Rule pruneRule (Rule r, RawExampleList prunePos,
            RawExampleList pruneNeg) {
        // TODO Auto-generated method stub
        return null;
    }

    private static Rule growRule (final RawExampleList growPos,
            final RawExampleList growNeg, final RawAttrList attrs) {

        // Get all possible conditions.
        final RuleCondition[][] conds = new RuleCondition[attrs.xList.size()][];
        for (int i = 0; i < attrs.xList.size(); i++) {
            final RawAttr attr = attrs.xList.get(i);
            if (attr.isContinuous) {
                final HashSet<String> valueSet = new HashSet<String>();
                for (RawExample e : growPos){
                    final String value = e.xList.get(i);
                    valueSet.add(value);
                }
                for (RawExample e : growNeg){
                    final String value = e.xList.get(i);
                    valueSet.add(value);
                }
                conds[i] = new RuleCondition[valueSet.size() * 2];
                int j = 0;
                for (String value: valueSet){
                    conds[i][j * 2] =
                            new RuleCondition(attr.name, value,
                                    RuleCondition.OPT_LE);
                    conds[i][j * 2 + 1] =
                            new RuleCondition(attr.name, value,
                                    RuleCondition.OPT_GE);
                    j++;
                }
            } else { // Discrete attribute.
                conds[i] = new RuleCondition[attr.valueList.size()];
                // All possible conditions for this discrete attribute.
                for (int j = 0; j < attr.valueList.size(); j++) {
                    conds[i][j] =
                            new RuleCondition(attr.name, attr.valueList.get(j),
                                    RuleCondition.OPT_EQ);
                }
            }

        }
        // Loop until
        // Measure FOIL for each condition.
        // Pick the max one, add to rule.
        // Loop end
        // Return rule.
        return null;
    }
}
