package artificialNeuralNetworks.ANN;

import java.util.ArrayList;
import java.util.List;

import util.Dbg;

import common.MappedAttr;
import common.MappedAttrList;
import common.RawAttr;
import common.RawAttrList;
import common.RawExampleList;

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
    public final ArrayList<MappedAttr> xList;
    public final ArrayList<MappedAttr> tList;

    public AnnAttrList(final RawExampleList exs, final RawAttrList rAttrs) {
        this.rAttrs = rAttrs;
        this.xList = new ArrayList<MappedAttr>();
        this.tList = new ArrayList<MappedAttr>();
        
        final MappedAttrList mal = new MappedAttrList(exs, rAttrs);
        for (int i = 0; i < rAttrs.xList.size(); i++) {
            final RawAttr ra = rAttrs.xList.get(i);
            final MappedAttr ma = mal.xList.get(i);
            addOneAttr(this.xList, ra, ma);
        }
        // Target attribute.
        final RawAttr ra = rAttrs.t;
        addOneAttr(this.tList, ra, mal.t);
    }

    public List<MappedAttr> getAnnAttrsAt (final int indexOfRawAttr) {
        assert indexOfRawAttr >= 0 && indexOfRawAttr < rAttrs.xList.size();
        int fromIndex = 0;
        for (int i = 0; i < indexOfRawAttr; i++) {
            final int length = getLength(rAttrs.xList.get(i));
            fromIndex += length;
        }
        final int toIndex =
                fromIndex + getLength(rAttrs.xList.get(indexOfRawAttr));
        return xList.subList(fromIndex, toIndex);
    }

    public List<Double> getAnnValuesAt (final int indexOfRawAttr,
            final ArrayList<Double> values) {
        assert indexOfRawAttr >= 0 && indexOfRawAttr < rAttrs.xList.size();
        int fromIndex = 0;
        for (int i = 0; i < indexOfRawAttr; i++) {
            final int length = getLength(rAttrs.xList.get(i));
            fromIndex += length;
        }
        final int toIndex =
                fromIndex + getLength(rAttrs.xList.get(indexOfRawAttr));
        return values.subList(fromIndex, toIndex);
    }

    private int getLength (RawAttr rAttr) {
        if (rAttr.isContinuous) {
            return 1;
        } else if (rAttr.valueList.size() == 2) { // Have 2 possible values.
            return 1;
        } else {
            return rAttr.valueList.size();
        }
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

    private static void addOneAttr (final ArrayList<MappedAttr> attrList,
            final RawAttr ra, final MappedAttr ma) {
        if (ra.isContinuous) {
            attrList.add(ma);
        } else if (ra.valueList.size() == 2) {
            // One discrete RawAttr has 2 possible values, so converts to one
            // AnnAttr.
            attrList.add(new MappedAttr(ra.name));
        } else { // More than 2 values.
            // One discrete RawAttr has n possible values, so converts to n
            // separate AnnAttr.
            for (String valueName : ra.valueList) {
                final String newValueName = ra.name + " is " + valueName;
                attrList.add(new MappedAttr(newValueName));
            }
        }
    }
}
