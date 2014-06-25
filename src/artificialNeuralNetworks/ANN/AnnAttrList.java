package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

import util.Dbg;

import common.RawAttr;
import common.RawAttrList;

/**
 * FileName: AnnAttrList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 12:38:26 AM
 */
public class AnnAttrList {
    public final RawAttrList rAttrs;
    public final ArrayList<String> xList;
    public final ArrayList<String> tList;

    public AnnAttrList(final RawAttrList rAttrs) {
        this.rAttrs = rAttrs;
        this.xList = new ArrayList<String>();
        this.tList = new ArrayList<String>();
        
        for (int i = 0; i < rAttrs.xList.size(); i++) {
            final RawAttr ra = rAttrs.xList.get(i);
            addOneAttr(this.xList, ra);
        }
        // Target attribute.
        final RawAttr ra = rAttrs.t;
        addOneAttr(this.tList, ra);
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("X:");
        sb.append(xList.toString());
        sb.append(Dbg.NEW_LINE + "T:");
        sb.append(tList.toString());
        return sb.toString();
    }

    private static void addOneAttr (final ArrayList<String> attrList,
            final RawAttr ra) {
        if (ra.isContinuous) {
            attrList.add(ra.name);
        } else if (ra.valueList.size() == 2) {
            // One discrete RawAttr has 2 possible values, so converts to one
            // AnnAttr.
            attrList.add(ra.name);
        } else { // More than 2 values.
            // One discrete RawAttr has n possible values, so converts to n
            // separate AnnAttr.
            for (String valueName : ra.valueList) {
                final String newValueName = ra.name + " is " + valueName;
                attrList.add(newValueName);
            }
        }
    }
}
