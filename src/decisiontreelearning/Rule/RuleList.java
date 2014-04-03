package decisiontreelearning.Rule;

/**
 * FileName: RuleList.java
 * @Description: RuleList structure, a set of rules.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import java.util.ArrayList;

import util.Dbg;

public class RuleList {
    public final ArrayList<Rule> list;

    public RuleList() {
        list = new ArrayList<Rule>();
    }

    public RuleList(RuleList rlist) {
        // New list need new copy of each rule. Because the element inside the
        // rule might be changed later, so there will be problem if just
        // keep the copy of address of the rule.
        list = new ArrayList<Rule>();
        for (Rule r : rlist.list) {
            list.add(new Rule(r));
        }
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        for (Rule r : list) {
            sb.append(r.toString());
            sb.append(Dbg.NEW_LINE);
        }
        return sb.toString();
    }

}
