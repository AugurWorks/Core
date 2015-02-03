package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import alfred.scaling.ScaleFunction;
import alfred.scaling.ScaleFunctions;

import com.google.common.collect.Lists;

public class SigmoidScaleFunctionTest {

    private static final double PRECISION = 0.001;

    @Test
    public void test1() {
        List<BigDecimal> values = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.ONE, new BigDecimal(0.1), new BigDecimal(0.8));
        ScaleFunction sf = ScaleFunctions.createSigmoidScaleFunction(values);
        for (double i = 0.05; i < 1; i+=0.05) {
            assertEquals(i, sf.denormalize(sf.normalize(i)), PRECISION);
        }
    }

}
