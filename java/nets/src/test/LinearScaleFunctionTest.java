package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import alfred.scaling.ScaleFunction;
import alfred.scaling.ScaleFunctions;

public class LinearScaleFunctionTest {

    private static final double PRECISION = 0.001;

    @Test
    public void test1() {
        ScaleFunction sf = ScaleFunctions.createLinearScaleFunction(0, 1, 0, 1);
        assertEquals(1, sf.normalize(1.0), PRECISION);
        assertEquals(0, sf.normalize(0.0), PRECISION);
        assertEquals(0.3, sf.normalize(0.3), PRECISION);
        assertEquals(0.7, sf.normalize(0.7), PRECISION);

        assertEquals(1, sf.denormalize(1.0), PRECISION);
        assertEquals(0, sf.denormalize(0.0), PRECISION);
        assertEquals(0.3, sf.denormalize(0.3), PRECISION);
        assertEquals(0.7, sf.denormalize(0.7), PRECISION);
    }

    @Test
    public void test2() {
        ScaleFunction sf = ScaleFunctions.createLinearScaleFunction(0, 2, 0, 1);
        assertEquals(0.5, sf.normalize(1.0), PRECISION);
        assertEquals(1.0, sf.normalize(2.0), PRECISION);
        assertEquals(0.75, sf.normalize(1.5), PRECISION);
        assertEquals(0.25, sf.normalize(0.5), PRECISION);

        assertEquals(1.0, sf.denormalize(0.5), PRECISION);
        assertEquals(2.0, sf.denormalize(1.0), PRECISION);
        assertEquals(1.5, sf.denormalize(0.75), PRECISION);
        assertEquals(0.5, sf.denormalize(0.25), PRECISION);
    }

    @Test
    public void test3() {
        ScaleFunction sf = ScaleFunctions.createLinearScaleFunction(-1, 1, 0, 1);
        assertEquals(0.5, sf.normalize(0.0), PRECISION);
        assertEquals(1.0, sf.normalize(1.0), PRECISION);
        assertEquals(0, sf.normalize(-1.0), PRECISION);
        assertEquals(0.75, sf.normalize(0.5), PRECISION);

        assertEquals(0.0, sf.denormalize(0.5), PRECISION);
        assertEquals(1.0, sf.denormalize(1.0), PRECISION);
        assertEquals(-1.0, sf.denormalize(0), PRECISION);
        assertEquals(0.5, sf.denormalize(0.75), PRECISION);
    }

}
