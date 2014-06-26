package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     ENNRPOCNN.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 16, 2014 12:28:04 AM 
 */
public class ENNRPOCNN implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        RawExampleList exs2 =  new ENN().reduce(exs, attrs);
        return POCNN.rPocNN(exs2, attrs);
    }
}
