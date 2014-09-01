package ripperk;

import java.util.ArrayList;
import java.util.LinkedList;

import common.RawAttrList;

/**
 * FileName: Rule.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 2:31:33 PM
 */
public class Rule extends LinkedList<RuleCondition> {
    private static final long serialVersionUID = 1L;
    public final String prediction;

    public Rule(String prediction) {
        this.prediction = prediction;
    }

    /**
     * @return Prediction of rule, if all conditions are satisfied; null,
     *         otherwise.
     * */
    public String rulePredict (ArrayList<String> in, RawAttrList attrs) {
        String ret = prediction;
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
}
