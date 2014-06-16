package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     ENNSPOCNN.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 16, 2014 12:26:51 AM 
 */
public class ENNSPOCNN  implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        RawExampleList exs2 =  ENN.reduce(exs, attrs);
        return POCNN.sPocNN(exs2, attrs);
    }
}
