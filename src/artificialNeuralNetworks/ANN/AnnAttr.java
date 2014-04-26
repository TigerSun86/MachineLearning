package artificialNeuralNetworks.ANN;

import common.Mappable;

/**
 * FileName: AnnAttr.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 21, 2014 2:56:02 PM
 */
public class AnnAttr implements Mappable {
    private final String name;
    private final double valueMax;
    private final double valueMin;
    private final double mappedMax = FloatConverter.HIGH_VALUE;
    private final double mappedMin = FloatConverter.LOW_VALUE;

    public AnnAttr(String name, double valueMax, double valueMin) {
        super();
        this.name = name;
        this.valueMax = valueMax;
        this.valueMin = valueMin;
    }

    public String getName () {
        return name;
    }

    @Override
    public double getValueMax () {
        return valueMax;
    }

    @Override
    public double getValueMin () {
        return valueMin;
    }

    @Override
    public double getMappedMax () {
        return mappedMax;
    }

    @Override
    public double getMappedMin () {
        return mappedMin;
    }

    @Override
    public String toString () {
        return String
                .format("%s max: %.3f min: %.3f", name, valueMax, valueMin);
    }
}
