package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alfred.RectNetFixed;
import alfred.scaling.ScaleFunctions.ScaleFunctionType;

public class RectNetFixedTest {
    private RectNetFixed net;
    private Random random = new Random();
    private static double EPSILON = 0.000001;
    private static int NUMINPUTS = 4;
    private static int DEPTH = 2;
    private String prefix;

    @Before
    public void setUp() throws Exception {
        net = new RectNetFixed(DEPTH, NUMINPUTS);
        prefix = System.getProperty("user.dir");
        prefix = prefix + File.separator + "nets" + File.separator + "src" + File.separator +
                "test" + File.separator + "test_train_files" + File.separator;
    }

    @After
    public void tearDown() throws Exception {
        net = null;
    }

    @Test
    public void testGetOutput() {
        // rectangular example
        int width = 2;
        int height = 3;
        net = new RectNetFixed(width, height);
        BigDecimal[] inpts = new BigDecimal[height];
        for (int i = 0; i < height; i++) {
            inpts[i] = BigDecimal.valueOf(1.0);
        }
        net.setInputs(inpts);
        testFirstLayerWeightsHelper(net);
        // i worked this out by hand ... :(
        BigDecimal weight = BigDecimal.valueOf(0.1);
        for (int leftCol = 0; leftCol < width - 1; leftCol++) {
            int rightCol = leftCol + 1;
            for (int leftRow = 0; leftRow < height; leftRow++) {
                for (int rightRow = 0; rightRow < height; rightRow++) {
                    net.setWeight(leftCol, leftRow, rightCol, rightRow, weight);
                }
            }
        }
        for (int leftRow = 0; leftRow < height; leftRow++) {
            net.setOutputNeuronWeight(leftRow, weight);
        }
        testFirstLayerWeightsHelper(net);
        // answer should be 0.6529:
        BigDecimal output = net.getOutput();
        // use a small epsilon because i only used a few digits in matlab
        assertEquals("output should be 0.6529", output.doubleValue(), 0.6529, 0.00005);

        // square example
        width = 2;
        height = 2;
        net = new RectNetFixed(width, height);
        inpts = new BigDecimal[height];
        inpts[0] = BigDecimal.valueOf(0.2);
        inpts[1] = BigDecimal.valueOf(0.8);
        net.setInputs(inpts);
        testFirstLayerWeightsHelper(net);
        // i worked this out by hand ... :(
        weight = BigDecimal.valueOf(0.2);
        for (int leftCol = 0; leftCol < width - 1; leftCol++) {
            int rightCol = leftCol + 1;
            for (int leftRow = 0; leftRow < height; leftRow++) {
                for (int rightRow = 0; rightRow < height; rightRow++) {
                    net.setWeight(leftCol, leftRow, rightCol, rightRow, weight);
                }
            }
        }
        for (int leftRow = 0; leftRow < height; leftRow++) {
            net.setOutputNeuronWeight(leftRow, BigDecimal.valueOf(0.4));
        }
        testFirstLayerWeightsHelper(net);
        // answer should be 0.8487:
        System.out.println("\n\n\n");
        output = net.getOutput();
        // use a small epsilon because i only used a few digits in matlab
        assertEquals("output should be 0.8487", output.doubleValue(), 0.8487, 0.00005);
        testFirstLayerWeightsHelper(net);
    }

    /**
     * Contains constructor tests, and trivial getX getY tests.
     */
    @Test
    public void testRectNetFixed() {
        net = null;
        net = new RectNetFixed();
        assertNotNull(net);
        assertEquals(net.getX(), 5);
        assertEquals(net.getY(), 10);
        net = null;

        int x = random.nextInt(10) + 1;
        int y = random.nextInt(1000) + 1;
        net = new RectNetFixed(x, y);
        assertNotNull(net);
        assertEquals(net.getX(), x);
        assertEquals(net.getY(), y);
        try {
            net = new RectNetFixed(0, 10);
            fail("Should not be able to construct a 0 depth net.");
        } catch (Exception e) {
            // should go here
            assertTrue(true);
        }
        try {
            net = new RectNetFixed(10, 0);
            fail("Should not be able to construct a 0 input net.");
        } catch (Exception e) {
            // should go here
            assertTrue(true);
        }

        net = null;
        x = random.nextInt(10) + 1;
        y = random.nextInt(1000) + 1;
        net = new RectNetFixed(x, y, false);
        assertNotNull(net);
        assertEquals(net.getX(), x);
        assertEquals(net.getY(), y);
        net = null;
        x = random.nextInt(10) + 1;
        y = random.nextInt(1000) + 1;
        net = new RectNetFixed(x, y, true);
        assertNotNull(net);
        assertEquals(net.getX(), x);
        assertEquals(net.getY(), y);
        try {
            net = new RectNetFixed(0, 10, true);
            fail("Should not be able to construct a 0 depth net.");
        } catch (Exception e) {
            // should go here
            assertTrue(true);
        }
        try {
            net = new RectNetFixed(10, 0, true);
            fail("Should not be able to construct a 0 input net.");
        } catch (Exception e) {
            // should go here
            assertTrue(true);
        }
    }

    /**
     * Contains a simple test about setting inputs. Does not confirm outputs.
     */
    @Test
    public void testSetInputs() {
        BigDecimal[] inpts = new BigDecimal[NUMINPUTS];
        for (int i = 0; i < NUMINPUTS; i++) {
            inpts[i] = BigDecimal.valueOf(random.nextDouble());
        }
        net.setInputs(inpts);

        // wrong lengths
        try {
            inpts = new BigDecimal[NUMINPUTS - 1];
            for (int i = 0; i < NUMINPUTS - 1; i++) {
                inpts[i] = BigDecimal.valueOf(random.nextDouble());
            }
            net.setInputs(inpts);
            fail("Net should not accept input array that is too short.");
        } catch (Exception e) {
            // should go here
        }
        // too long currently works...
        try {
            inpts = new BigDecimal[NUMINPUTS + 1];
            for (int i = 0; i < NUMINPUTS + 1; i++) {
                inpts[i] = BigDecimal.valueOf(random.nextDouble());
            }
            net.setInputs(inpts);
        } catch (Exception e) {
            // should *not* go here
            fail("Net should accept and truncate a too-long array of inputs");
        }
    }

    /**
     * Tests the getting and setting of individual neuron weights
     */
    @Test
    public void testSetGetWeights() {
        // legal inputs
        BigDecimal scalar = BigDecimal.valueOf(random.nextDouble());
        for (int leftCol = 0; leftCol < DEPTH - 1; leftCol++) {
            int rightCol = leftCol + 1;
            for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
                for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
                    double weight = scalar.doubleValue()
                            * ((leftCol + rightCol) * rightRow - rightCol);
                    net.setWeight(leftCol, leftRow, rightCol, rightRow, BigDecimal.valueOf(weight));
                }
            }
        }

        for (int leftCol = 0; leftCol < DEPTH - 1; leftCol++) {
            int rightCol = leftCol + 1;
            for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
                for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
                    double desired = scalar.doubleValue()
                            * ((leftCol + rightCol) * rightRow - rightCol);
                    double actual = net.getWeight(leftCol, leftRow, rightCol,
                            rightRow).doubleValue();
                    assertEquals("Weight should have changed", actual, desired,
                            EPSILON);
                }
            }
        }

        // confirm that the first layer never changed
        testFirstLayerWeightsHelper(net);
        // now for the output neuron
        for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
            double weight = scalar.doubleValue() * leftRow + 1;
            net.setOutputNeuronWeight(leftRow, BigDecimal.valueOf(weight));
        }
        for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
            double desired = scalar.doubleValue() * leftRow + 1;
            double actual = net.getOutputNeuronWeight(leftRow).doubleValue();
            assertEquals("Weight should have changed", actual, desired, EPSILON);
        }
        // confirm again that the first layer never changed.
        testFirstLayerWeightsHelper(net);
        // now some illegal indices
        int leftCol;
        int rightCol;
        int leftRow;
        int rightRow;
        // right row too small
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            leftRow = random.nextInt(NUMINPUTS - 1);
            rightRow = -1;
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right row too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            leftRow = random.nextInt(NUMINPUTS - 1);
            rightRow = NUMINPUTS;
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // left row too small
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = -1;
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // left row too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = NUMINPUTS;
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right col too small
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = -1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = random.nextInt(NUMINPUTS - 1);
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right col too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = DEPTH + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = NUMINPUTS;
            net.getWeight(leftCol, leftRow, rightCol, rightRow);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // and set weights
        BigDecimal w = BigDecimal.valueOf(random.nextDouble());
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            leftRow = random.nextInt(NUMINPUTS - 1);
            rightRow = -1;
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right row too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            leftRow = random.nextInt(NUMINPUTS - 1);
            rightRow = NUMINPUTS;
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // left row too small
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = -1;
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // left row too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = leftCol + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = NUMINPUTS;
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right col too small
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = -1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = random.nextInt(NUMINPUTS - 1);
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get negative indexed weight");
        } catch (Exception e) {
            // should go here
        }
        // right col too big
        try {
            leftCol = random.nextInt(DEPTH - 1);
            rightCol = DEPTH + 1;
            rightRow = random.nextInt(NUMINPUTS - 1);
            leftRow = NUMINPUTS;
            net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
            fail("Should not get too big indexed weight");
        } catch (Exception e) {
            // should go here
        }

        // and for the output neuron
        // leftRow too small
        try {
            net.getOutputNeuronWeight(-1);
            fail("Should not get output neuron negative index weight");
        } catch (Exception e) {
            // should go here
        }
        // leftRow too big
        try {
            net.getOutputNeuronWeight(NUMINPUTS);
            fail("Should not get output neuron too big index weight");
        } catch (Exception e) {
            // should go here
        }
        // and for the setting
        // leftRow too small
        try {
            net.setOutputNeuronWeight(-1, w);
            fail("Should not set output neuron negative index weight");
        } catch (Exception e) {
            // should go here
        }
        // leftRow too big
        try {
            net.setOutputNeuronWeight(NUMINPUTS, w);
            fail("Should not set output neuron too big index weight");
        } catch (Exception e) {
            // should go here
            assertTrue(true);
        }
        testFirstLayerWeightsHelper(net);
    }

    /**
     * Tests that the weight from the input layer to the first layer of neurons
     * is always 1.0
     */
    @Test
    public void testFirstLayerWeights() {
        assertTrue(testFirstLayerWeightsHelper(net));
    }

    /**
     * Helper method that returns true only when the weights from the input
     * layer to the first layer of neurons contains only 1.0 values
     */
    public boolean testFirstLayerWeightsHelper(RectNetFixed f) {
        for (int i = 0; i < f.getY(); i++) {
            double weight = f.getWeight(0, 0, 0, i).doubleValue();
            assertEquals("Weight from first layer to inputs should be 1.0",
                    1.0, weight, EPSILON);
            if (Math.abs(weight - 1.0) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testTrain() throws InterruptedException {
        // simple silly gate
        BigDecimal[] inpts = new BigDecimal[2];
        inpts[0] = BigDecimal.valueOf(0.2);
        inpts[1] = BigDecimal.valueOf(0.9);
        BigDecimal desired = BigDecimal.valueOf(0.77);
        BigDecimal learningConstant = BigDecimal.valueOf(1.0);
        int iterations = 1;
        net = new RectNetFixed(2, 2);
        // we're just hoping that the training moves in the right direction
        BigDecimal output = net.getOutput();
        BigDecimal distance = desired.subtract(output).abs();
        net.train(inpts, desired, iterations, learningConstant);
        BigDecimal afterOutput = net.getOutput();
        BigDecimal afterDistance = desired.subtract(afterOutput).abs();
        assertTrue(afterDistance.min(distance).equals(afterDistance));
        assertTrue(testFirstLayerWeightsHelper(net));

        // now run a few more times
        output = net.getOutput();
        distance = desired.subtract(output).abs();
        iterations = 5;
        net.train(inpts, desired, iterations, learningConstant);
        afterOutput = net.getOutput();
        afterDistance = desired.subtract(afterOutput).abs();
        assertTrue(afterDistance.min(distance).equals(afterDistance));
        assertTrue(testFirstLayerWeightsHelper(net));

        // here's one that's done by hand
        inpts[0] = BigDecimal.valueOf(0.6);
        inpts[1] = BigDecimal.valueOf(-0.2);
        desired = BigDecimal.valueOf(0.24);
        learningConstant = BigDecimal.valueOf(0.8);
        net = new RectNetFixed(1, 2);
        // set all the weights by hand
        net.setOutputNeuronWeight(0, BigDecimal.valueOf(0.2));
        net.setOutputNeuronWeight(1, BigDecimal.valueOf(-0.2));
        // expect w'0 = 0.2 + 0.8*0.8581*-0.2456
        // expect w'1 = -0.2 + 0.8*0.3543*-0.2456
        net.train(inpts, desired, 1, learningConstant);
        assertEquals("Output weight to top should change",
                net.getOutputNeuronWeight(0).doubleValue(), 0.0314, 0.00005);
        assertEquals("Output weight to bottom should change",
                net.getOutputNeuronWeight(1).doubleValue(), -0.2696, 0.00005);
        assertTrue(testFirstLayerWeightsHelper(net));
        // expect w'0 = 0.0314 + 0.8*0.8581*-0.1549
        // expect w'1 = -0.2696 + 0.8*0.3543*-0.1549
        net.train(inpts, desired, 1, learningConstant);
        assertEquals("Output weight to top should change",
                net.getOutputNeuronWeight(0).doubleValue(), -0.0749, 0.00006);
        assertEquals("Output weight to bottom should change",
                net.getOutputNeuronWeight(1).doubleValue(), -0.3135, 0.00006);
        assertTrue(testFirstLayerWeightsHelper(net));
        // now do the same thing for two rounds
        net = new RectNetFixed(1, 2);
        // set all the weights by hand
        net.setOutputNeuronWeight(0, BigDecimal.valueOf(0.2));
        net.setOutputNeuronWeight(1, BigDecimal.valueOf(-0.2));
        net.train(inpts, desired, 2, learningConstant);
        assertEquals("Output weight to top should change",
                net.getOutputNeuronWeight(0).doubleValue(), -0.0749, 0.00006);
        assertEquals("Output weight to bottom should change",
                net.getOutputNeuronWeight(1).doubleValue(), -0.3135, 0.00006);

        // 2x2 done by hand
        net = new RectNetFixed(2, 2, false);
        // set the weights
        net.setOutputNeuronWeight(0, BigDecimal.valueOf(0.8));
        net.setOutputNeuronWeight(1, BigDecimal.valueOf(-0.6));
        net.setWeight(0, 0, 1, 0, BigDecimal.valueOf(0.2));
        net.setWeight(0, 0, 1, 1, BigDecimal.valueOf(-0.11));
        net.setWeight(0, 1, 1, 0, BigDecimal.valueOf(0.4));
        net.setWeight(0, 1, 1, 1, BigDecimal.valueOf(-0.2));
        learningConstant = BigDecimal.valueOf(0.87);
        iterations = 1;
        desired = BigDecimal.valueOf(-0.14);
        inpts[0] = BigDecimal.valueOf(-0.55);
        inpts[1] = BigDecimal.valueOf(0.16);
        net.setInputs(inpts);

        output = net.getOutput();
        assertEquals("output should start at 0.7238", 0.7238, output.doubleValue(), 0.00005);
        net.train(inpts, desired, iterations, learningConstant);
        assertTrue(testFirstLayerWeightsHelper(net));
        BigDecimal wAB = net.getWeight(0, 0, 1, 0);
        BigDecimal wAD = net.getWeight(0, 0, 1, 1);
        BigDecimal wCB = net.getWeight(0, 1, 1, 0);
        BigDecimal wCD = net.getWeight(0, 1, 1, 1);
        BigDecimal wBo = net.getOutputNeuronWeight(0);
        BigDecimal wDo = net.getOutputNeuronWeight(1);
        output = net.getOutput();

        assertEquals("wAB should change", wAB.doubleValue(), 0.1633, 0.00005);
        assertEquals("wAD should change", wAD.doubleValue(), -0.0787, 0.00005);
        assertEquals("wCB should change", wCB.doubleValue(), 0.2591, 0.00005);
        assertEquals("wCD should change", wCD.doubleValue(), -0.0802, 0.00005);
        assertEquals("wBo should change", wBo.doubleValue(), 0.4854, 0.00005);
        assertEquals("wDo should change", wDo.doubleValue(), -0.7783, 0.00005);

        // because propagated error blows up in sigmoid, we have to do
        // this all in the test case...
        BigDecimal inB = wAB.multiply(sigmoid(inpts[0])).add(wCB.multiply(sigmoid(inpts[1])));
        BigDecimal inD = wAD.multiply(sigmoid(inpts[0])).add(wCD.multiply(sigmoid(inpts[1])));
        BigDecimal outB = sigmoid(inB);
        BigDecimal outD = sigmoid(inD);
        BigDecimal inO = wBo.multiply(outB).add(wDo.multiply(outD));
        BigDecimal expectedOutput = sigmoid(inO);
        assertEquals("output should change", output.doubleValue(), expectedOutput.doubleValue(), 0.00005);
    }

    @Test
    public void testTrainFile() {
        // try a broken file - should fail
        long defaultTime = 1000 * 60;
        try {
            RectNetFixed.trainFile(prefix + "broken_data.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            fail("Should not have succeeded on a broken file");
        } catch (Exception e) {
            // should go here
        }
        // try a broken file - should fail
        try {
            RectNetFixed.trainFile(prefix + "broken_header.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            fail("Should not have succeeded on a broken file");
        } catch (Exception e) {
            // should go here
        }
        // try a broken file - should fail
        try {
            RectNetFixed.trainFile(prefix + "broken_numinputs.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            fail("Should not have succeeded on a broken file");
        } catch (Exception e) {
            // should go here
        }
        // try a broken file - should fail
        try {
            RectNetFixed.trainFile(prefix + "broken_titles.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            fail("Should not have succeeded on a broken file");
        } catch (Exception e) {
            // should go here
        }
        // try a broken file - should fail
        try {
            RectNetFixed.trainFile(prefix + "broken_trainline.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            fail("Should not have succeeded on a broken file");
        } catch (Exception e) {
            // should go here
        }
        // now a working file
        try {
            net = RectNetFixed.trainFile(prefix + "OR_clean.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            assertNotNull(net);
            assertEquals("X should be 3", 3, net.getX());
            assertEquals("Y should be 2", 2, net.getY());

            net.setInputs(new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
            BigDecimal output = net.getOutput();
            double diff = Math.pow(0 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE });
            output = net.getOutput();
            diff += Math.pow(1 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ZERO });
            output = net.getOutput();
            diff += Math.pow(1 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ONE });
            output = net.getOutput();
            diff += Math.pow(1 - output.doubleValue(), 2);

            // Should have run to completion
            assertTrue(diff / 4.0 < 0.1);
        } catch (Exception e) {
            // should not go here
            fail("Should not have exploded on a valid file");
        }
        // AND_clean
        try {
            net = RectNetFixed.trainFile(prefix + "AND_clean.augtrain", false, "test", false, defaultTime, ScaleFunctionType.LINEAR);
            assertNotNull(net);
            assertEquals("X should be 4", 4, net.getX());
            assertEquals("Y should be 2", 2, net.getY());

            net.setInputs(new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
            BigDecimal output = net.getOutput();
            double diff = Math.pow(0 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE });
            output = net.getOutput();
            diff += Math.pow(0 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ZERO });
            output = net.getOutput();
            diff += Math.pow(0 - output.doubleValue(), 2);

            net.setInputs(new BigDecimal[] { BigDecimal.ONE, BigDecimal.ONE });
            output = net.getOutput();
            diff += Math.pow(1 - output.doubleValue(), 2);

            // Should have run to completion
            assertTrue(diff / 4 < 0.1);
        } catch (Exception e) {
            // should not go here
            fail("Should not have exploded on a valid file");
        }
    }

    @Test
    public void testSaveNet() {
        // 2x2 done by hand
        net = new RectNetFixed(2, 2, false);
        // set the weights
        net.setOutputNeuronWeight(0, BigDecimal.valueOf(0.8));
        net.setOutputNeuronWeight(1, BigDecimal.valueOf(-0.6));
        net.setWeight(0, 0, 1, 0, BigDecimal.valueOf(0.2));
        net.setWeight(0, 0, 1, 1, BigDecimal.valueOf(-0.11));
        net.setWeight(0, 1, 1, 0, BigDecimal.valueOf(0.4));
        net.setWeight(0, 1, 1, 1, BigDecimal.valueOf(-0.2));
        testFirstLayerWeightsHelper(net);
        try {
            RectNetFixed.saveNet(prefix + "2by2.augsave", net);
            net = null;
            net = RectNetFixed.loadNet(prefix + "2by2.augsave");
            assertNotNull(net);
            assertEquals("X should be 2",2,net.getX());
            assertEquals("Y should be 2",2,net.getY());
            testFirstLayerWeightsHelper(net);
            BigDecimal Bo = net.getOutputNeuronWeight(0);
            assertEquals("weights should be correct",Bo.doubleValue(),0.8,EPSILON);
            BigDecimal Do = net.getOutputNeuronWeight(1);
            assertEquals("weights should be correct",Do.doubleValue(),-0.6,EPSILON);
            BigDecimal AB = net.getWeight(0,0,1,0);
            assertEquals("weights should be correct",AB.doubleValue(),0.2,EPSILON);
            BigDecimal CB = net.getWeight(0,1,1,0);
            assertEquals("weights should be correct",CB.doubleValue(),0.4,EPSILON);
            BigDecimal AD = net.getWeight(0,0,1,1);
            assertEquals("weights should be correct",AD.doubleValue(),-0.11,EPSILON);
            BigDecimal CD = net.getWeight(0,1,1,1);
            assertEquals("weights should be correct",CD.doubleValue(),-0.2,EPSILON);
        } catch (Exception e) {
            fail("Net should have saved");
        }
    }

    /**
     * Performs the sigmoid function on an input y = 1 / (1 + exp(-alpha*x))
     * Used internally in getOutput method. Alpha is set to 3 currently.
     *
     * @param input
     *            X
     * @return sigmoid(x)
     */
    private double sigmoid(double input) {
        return 1.0 / (1.0 + Math.exp(-3.0 * input));
    }

    private BigDecimal sigmoid(BigDecimal input) {
        return BigDecimal.valueOf(sigmoid(input.doubleValue()));
    }
}
