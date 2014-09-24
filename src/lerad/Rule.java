package lerad;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import util.MyMath;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: Rule.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 2:31:33 PM
 */
public class Rule extends LinkedList<RuleCondition> implements Comparable<Rule> {
    private static final long serialVersionUID = 1L;
    public final HashSet<RuleCondition> consequents;

    public double nrRate;
    public double lastT;

    public Rule(RuleCondition prediction) {
        this.consequents = new HashSet<RuleCondition>();
        this.consequents.add(prediction);
        this.nrRate = 0;
    }

    /**
     * For clone rule.
     */
    public Rule(final Rule r) {
        super(r);
        this.consequents = new HashSet<RuleCondition>(r.consequents);
        this.nrRate = r.nrRate;
    }

    public void updateNRRate (RawExampleList set, RawAttrList attrs) {
        int n = 0;
        for (RawExample e : set) {
            if (this.isSatisfiedByAntecedent(e, attrs)) {
                n++;
            }
        }
        int r = consequents.size();
        nrRate = ((double) n) / r;
    }

    public boolean isSatisfiedByAntecedent (RawExample e, RawAttrList attrs) {
        for (RuleCondition c : this) {
            final int attrIndex = attrs.indexOf(c.name);
            final String value = e.xList.get(attrIndex);
            if (!c.value.equals(value)) {
                return false;
            }
        }

        return true;
    }

    public boolean isViolation (RawExample e, RawAttrList attrs) {
        if (isSatisfiedByAntecedent(e, attrs)) {
            final int attrIndex =
                    attrs.indexOf(consequents.iterator().next().name);
            final String value = e.xList.get(attrIndex);
            final RuleCondition cond =
                    new RuleCondition(attrs.xList.get(attrIndex).name, value,
                            RuleCondition.OPT_EQ);
            if (consequents.contains(cond)) {
                return false;
            } else { // Antecedents are satisfied but consequents are not.
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * @return Prediction of rule, if all conditions are satisfied; null,
     *         otherwise.
     * */
    public RuleCondition rulePredict (ArrayList<String> in, RawAttrList attrs) {
        RuleCondition ret = null;
        for (RuleCondition c : this) {
            final String name = c.name;
            final int index = attrs.indexOf(name);
            assert index != -1;
            final String value = in.get(index);
            if (!c.isSatisfied(value)) {
                ret = null; // return null;
                break;
            }
        }

        return ret;
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append("IF ");
        for (RuleCondition p : this) {
            sb.append("(");
            sb.append(p.toString());
            sb.append(")");
            sb.append("&&");
        }
        if (!this.isEmpty()) { // Delete redundant "&&" at the end.
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb.append("anything");
        }
        sb.append(" THEN ");
        if (consequents.size() == 1) {
            sb.append(consequents.iterator().next());
        } else {
            final Iterator<RuleCondition> iter = consequents.iterator();
            RuleCondition c = iter.next();
            sb.append(c.name);
            sb.append("==");
            sb.append(c.value);
            while (iter.hasNext()) {
                sb.append(" or ");
                sb.append(iter.next().value);
            }
        }

        sb.append(" " + Double.toString(MyMath.doubleRound(nrRate, 2)));
        return sb.toString();
    }

    @Override
    public int compareTo (Rule o) {
        return Double.compare(this.nrRate, o.nrRate);
    }

    public double getTnr (RawExample e, RawAttrList attrs, double curT) {
        if (isViolation(e, attrs)) {
            final double t = curT - lastT;
            lastT = t;
            return t * nrRate;
        } else {
            return 0;
        }
    }
}
