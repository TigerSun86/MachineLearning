package ripperk;

import java.util.ArrayList;
import java.util.LinkedList;

import common.Hypothesis;
import common.RawAttrList;

/**
 * FileName: RuleList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 2:25:50 PM
 */
public class RuleList extends LinkedList<Rule> implements Hypothesis {
    private static final long serialVersionUID = 1L;

    public String defaultPrediction = null;

    private final RawAttrList attrs;

    public RuleList(final RawAttrList attrs) {
        this.attrs = attrs;
    }

    @Override
    public String predict (ArrayList<String> in) {
        String ret = null;
        for (Rule r : this) {
            final String prediction = r.rulePredict(in, attrs);
            if (prediction != null) { // Accepted by r.
                ret = prediction;
                break;
            }
        }

        return (ret != null) ? ret : defaultPrediction;
    }

}
