package common;

import java.util.ArrayList;

/**
 * FileName: RawAttrList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 12, 2014 3:33:30 PM
 */
public class RawAttrList {
    public final ArrayList<RawAttr> xList;
    public RawAttr t;

    public RawAttrList(final String fileName) {
        this.xList = new ArrayList<RawAttr>();
        this.t = null;

        final DataReader in = new DataReader(fileName);
        boolean isX = true;
        while (true) {
            final String attrStr = in.nextLine();
            if (attrStr == null) {
                break;
            }

            if (attrStr.length() <= 1) {
                isX = false; // After empty line is target values.
                continue;
            }

            final String[] attrArr = attrStr.split(" ");
            final RawAttr attr = new RawAttr(attrArr[0]);
            if (attrArr.length == 2
                    && attrArr[1].equalsIgnoreCase("continuous")) {
                attr.isContinuous = true;
            } else {
                attr.isContinuous = false;
                for (int i = 1; i < attrArr.length; i++) {
                    attr.valueList.add(attrArr[i]);
                }
            }

            if (isX) {
                this.xList.add(attr);
            } else {
                this.t = attr;
            }
        } // End of while (true) {
        assert this.t != null;
        in.close();
    }

}
