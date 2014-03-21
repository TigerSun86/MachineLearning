package util;

/**
 * FileName: ChartData.java
 * @Description: Data for DisplayChart.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import java.util.LinkedHashMap;

public class ChartData {
    // String: data name; LinkedHashMap: category to value map.
    public LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
            new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
    public String windowTitle;
    public String chartTitle;
    public String categoryAxisLabel;
    public String valueAxisLabel;
}
