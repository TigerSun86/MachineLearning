package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

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
    public final ArrayList<String> xList;
    public final ArrayList<String> tList;

    public AnnAttrList(final RawAttrList ral) {
        this.xList = new ArrayList<String>();
        this.tList = new ArrayList<String>();
        for (RawAttr ra : ral.xList) {
            if (ra.isContinuous) {
                this.xList.add(ra.name);
            } else if (ra.valueList.size() == 2) { // Have 2 possible values.
                this.xList.add(ra.name);
            } else { // More than 2 values.
                for (String valueName : ra.valueList) { // multiple attributes.
                    final String newValueName = ra.name + " is " + valueName;
                    this.xList.add(newValueName);
                }
            }
        }
        // Target attribute.
        final RawAttr ra = ral.t;
        if (ra.isContinuous) {
            this.tList.add(ra.name);
        } else if (ra.valueList.size() == 2) { // Have 2 possible values.
            this.tList.add(ra.name); // Only one attribute.
        } else { // More than 2 values.
            for (String valueName : ra.valueList) { // multiple attributes.
                final String newValueName = ra.name + " is " + valueName;
                this.tList.add(newValueName);
            }
        }

    }
}
