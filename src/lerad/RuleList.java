package lerad;

import java.util.LinkedList;

import util.Dbg;

import common.RawAttrList;

/**
 * FileName: RuleList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 2:25:50 PM
 */
public class RuleList extends LinkedList<Rule>  {
    private static final long serialVersionUID = 1L;

    public final String negClass; // Negative class.
    public final String posClass;

    public RuleList(final String negClass, final RawAttrList attrs) {
        this.negClass = negClass;
        assert attrs.t.valueList.size() == 2;
        assert attrs.t.valueList.contains(negClass);
        this.posClass =
                (attrs.t.valueList.get(0).equals(negClass)) ? attrs.t.valueList
                        .get(1) : attrs.t.valueList.get(0);
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("Pos: " + posClass + Dbg.NEW_LINE);
        sb.append("Neg: " + negClass + Dbg.NEW_LINE);
        for (Rule r : this) {
            sb.append(r.toString());
            sb.append(Dbg.NEW_LINE);
        }
        return sb.toString();
    }
}
