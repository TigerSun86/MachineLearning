package lerad;

import java.util.ArrayList;
import java.util.LinkedList;

import util.Dbg;
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

    public final String defaultPrediction;

    private final RawAttrList attrs;

    public RuleList(final String def, final RawAttrList attrs) {
        this.defaultPrediction = def;
        this.attrs = attrs;
        assert attrs.t.valueList.contains(def);
    }

    @Override
    public String predict (ArrayList<String> in) {
        String ret = null;
/*        for (Rule r : this) {
            final String consequents = r.rulePredict(in, attrs);
            if (consequents != null) { // Accepted by r.
                ret = consequents;
                break;
            }
        }*/

        return (ret != null) ? ret : defaultPrediction;
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("Default: " + defaultPrediction + Dbg.NEW_LINE);
        for (Rule r : this) {
            sb.append(r.toString());
            sb.append(Dbg.NEW_LINE);
        }
        return sb.toString();
    }
}
