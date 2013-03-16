package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alfred.RectNetFixed;

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
		prefix = prefix + "\\nets\\src\\test\\test_train_files\\";
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
		double[] inpts = new double[height];
		for (int i = 0; i < height; i++) {
			inpts[i] = 1.0;
		}
		net.setInputs(inpts);
		testFirstLayerWeightsHelper(net);
		// i worked this out by hand ... :(
		double weight = 0.1;
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
		double output = net.getOutput();
		// use a small epsilon because i only used a few digits in matlab
		assertEquals("output should be 0.6529", output, 0.6529, 0.00005);

		// square example
		width = 2;
		height = 2;
		net = new RectNetFixed(width, height);
		inpts = new double[height];
		inpts[0] = 0.2;
		inpts[1] = 0.8;
		net.setInputs(inpts);
		testFirstLayerWeightsHelper(net);
		// i worked this out by hand ... :(
		weight = 0.2;
		for (int leftCol = 0; leftCol < width - 1; leftCol++) {
			int rightCol = leftCol + 1;
			for (int leftRow = 0; leftRow < height; leftRow++) {
				for (int rightRow = 0; rightRow < height; rightRow++) {
					net.setWeight(leftCol, leftRow, rightCol, rightRow, weight);
				}
			}
		}
		for (int leftRow = 0; leftRow < height; leftRow++) {
			net.setOutputNeuronWeight(leftRow, 0.4);
		}
		testFirstLayerWeightsHelper(net);
		// answer should be 0.8487:
		System.out.println("\n\n\n");
		output = net.getOutput();
		// use a small epsilon because i only used a few digits in matlab
		assertEquals("output should be 0.8487", output, 0.8487, 0.00005);
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
		double[] inpts = new double[NUMINPUTS];
		for (int i = 0; i < NUMINPUTS; i++) {
			inpts[i] = random.nextDouble();
		}
		net.setInputs(inpts);
		assertTrue(true);

		// wrong lengths
		try {
			inpts = new double[NUMINPUTS - 1];
			for (int i = 0; i < NUMINPUTS - 1; i++) {
				inpts[i] = random.nextDouble();
			}
			net.setInputs(inpts);
			fail("Net should not accept input array that is too short.");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// too long currently works...
		try {
			inpts = new double[NUMINPUTS + 1];
			for (int i = 0; i < NUMINPUTS + 1; i++) {
				inpts[i] = random.nextDouble();
			}
			net.setInputs(inpts);
			assertTrue(true);
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
		double scalar = random.nextDouble();
		for (int leftCol = 0; leftCol < DEPTH - 1; leftCol++) {
			int rightCol = leftCol + 1;
			for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
				for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
					double weight = scalar
							* ((leftCol + rightCol) * rightRow - rightCol);
					net.setWeight(leftCol, leftRow, rightCol, rightRow, weight);
				}
			}
		}

		for (int leftCol = 0; leftCol < DEPTH - 1; leftCol++) {
			int rightCol = leftCol + 1;
			for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
				for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
					double desired = scalar
							* ((leftCol + rightCol) * rightRow - rightCol);
					double actual = net.getWeight(leftCol, leftRow, rightCol,
							rightRow);
					assertEquals("Weight should have changed", actual, desired,
							EPSILON);
				}
			}
		}

		// confirm that the first layer never changed
		testFirstLayerWeightsHelper(net);
		// now for the output neuron
		for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
			double weight = scalar * leftRow + 1;
			net.setOutputNeuronWeight(leftRow, weight);
		}
		for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
			double desired = scalar * leftRow + 1;
			double actual = net.getOutputNeuronWeight(leftRow);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
		}
		// and set weights
		double w = random.nextDouble();
		try {
			leftCol = random.nextInt(DEPTH - 1);
			rightCol = leftCol + 1;
			leftRow = random.nextInt(NUMINPUTS - 1);
			rightRow = -1;
			net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
			fail("Should not get negative indexed weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
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
			assertTrue(true);
		}

		// and for the output neuron
		// leftRow too small
		try {
			net.getOutputNeuronWeight(-1);
			fail("Should not get output neuron negative index weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// leftRow too big
		try {
			net.getOutputNeuronWeight(NUMINPUTS);
			fail("Should not get output neuron too big index weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// and for the setting
		// leftRow too small
		try {
			net.setOutputNeuronWeight(-1, w);
			fail("Should not set output neuron negative index weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
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
			double weight = f.getWeight(0, 0, 0, i);
			assertEquals("Weight from first layer to inputs should be 1.0",
					1.0, weight, EPSILON);
			if (Math.abs(weight - 1.0) > EPSILON) {
				return false;
			}
		}
		return true;
	}

	@Test
	public void testTrain() {
		// simple silly gate
		double[] inpts = new double[2];
		inpts[0] = 0.2;
		inpts[1] = 0.9;
		double desired = 0.77;
		double learningConstant = 1.0;
		int iterations = 1;
		net = new RectNetFixed(2, 2);
		// we're just hoping that the training moves in the right direction
		double output = net.getOutput();
		double distance = Math.abs(desired - output);
		net.train(inpts, desired, iterations, learningConstant);
		double afterOutput = net.getOutput();
		double afterDistance = Math.abs(desired - afterOutput);
		assertTrue(afterDistance < distance);
		assertTrue(testFirstLayerWeightsHelper(net));

		// now run a few more times
		output = net.getOutput();
		distance = Math.abs(desired - output);
		iterations = 5;
		net.train(inpts, desired, iterations, learningConstant);
		afterOutput = net.getOutput();
		afterDistance = Math.abs(desired - afterOutput);
		assertTrue(afterDistance < distance);
		assertTrue(testFirstLayerWeightsHelper(net));

		// here's one that's done by hand
		inpts[0] = 0.6;
		inpts[1] = -0.2;
		desired = 0.24;
		learningConstant = 0.8;
		net = new RectNetFixed(1, 2);
		// set all the weights by hand
		net.setOutputNeuronWeight(0, 0.2);
		net.setOutputNeuronWeight(1, -0.2);
		// expect w'0 = 0.2 + 0.8*0.8581*-0.2456
		// expect w'1 = -0.2 + 0.8*0.3543*-0.2456
		net.train(inpts, desired, 1, learningConstant);
		assertEquals("Output weight to top should change",
				net.getOutputNeuronWeight(0), 0.0314, 0.00005);
		assertEquals("Output weight to bottom should change",
				net.getOutputNeuronWeight(1), -0.2696, 0.00005);
		assertTrue(testFirstLayerWeightsHelper(net));
		// expect w'0 = 0.0314 + 0.8*0.8581*-0.1549
		// expect w'1 = -0.2696 + 0.8*0.3543*-0.1549
		net.train(inpts, desired, 1, learningConstant);
		assertEquals("Output weight to top should change",
				net.getOutputNeuronWeight(0), -0.0749, 0.00006);
		assertEquals("Output weight to bottom should change",
				net.getOutputNeuronWeight(1), -0.3135, 0.00006);
		assertTrue(testFirstLayerWeightsHelper(net));
		// now do the same thing for two rounds
		net = new RectNetFixed(1, 2);
		// set all the weights by hand
		net.setOutputNeuronWeight(0, 0.2);
		net.setOutputNeuronWeight(1, -0.2);
		net.train(inpts, desired, 2, learningConstant);
		assertEquals("Output weight to top should change",
				net.getOutputNeuronWeight(0), -0.0749, 0.00006);
		assertEquals("Output weight to bottom should change",
				net.getOutputNeuronWeight(1), -0.3135, 0.00006);

		// 2x2 done by hand
		net = new RectNetFixed(2, 2, false);
		// set the weights
		net.setOutputNeuronWeight(0, 0.8);
		net.setOutputNeuronWeight(1, -0.6);
		net.setWeight(0, 0, 1, 0, 0.2);
		net.setWeight(0, 0, 1, 1, -0.11);
		net.setWeight(0, 1, 1, 0, 0.4);
		net.setWeight(0, 1, 1, 1, -0.2);
		learningConstant = 0.87;
		iterations = 1;
		desired = -0.14;
		inpts[0] = -0.55;
		inpts[1] = 0.16;
		net.setInputs(inpts);

		output = net.getOutput();
		assertEquals("output should start at 0.7238", 0.7238, output, 0.00005);
		net.train(inpts, desired, iterations, learningConstant);
		assertTrue(testFirstLayerWeightsHelper(net));
		double wAB = net.getWeight(0, 0, 1, 0);
		double wAD = net.getWeight(0, 0, 1, 1);
		double wCB = net.getWeight(0, 1, 1, 0);
		double wCD = net.getWeight(0, 1, 1, 1);
		double wBo = net.getOutputNeuronWeight(0);
		double wDo = net.getOutputNeuronWeight(1);
		output = net.getOutput();

		assertEquals("wAB should change", wAB, 0.1633, 0.00005);
		assertEquals("wAD should change", wAD, -0.0787, 0.00005);
		assertEquals("wCB should change", wCB, 0.2591, 0.00005);
		assertEquals("wCD should change", wCD, -0.0802, 0.00005);
		assertEquals("wBo should change", wBo, 0.4854, 0.00005);
		assertEquals("wDo should change", wDo, -0.7783, 0.00005);

		// because propagated error blows up in sigmoid, we have to do
		// this all in the test case...
		double inB = wAB * sigmoid(inpts[0]) + wCB * sigmoid(inpts[1]);
		double inD = wAD * sigmoid(inpts[0]) + wCD * sigmoid(inpts[1]);
		double outB = sigmoid(inB);
		double outD = sigmoid(inD);
		double inO = wBo * outB + wDo * outD;
		double expectedOutput = sigmoid(inO);
		assertEquals("output should change", output, expectedOutput, 0.00005);
	}

	@Test
	public void testTrainFile() {
		// try a broken file - should fail
		try {
			RectNetFixed.trainFile(prefix + "broken_data.augtrain", false);
			fail("Should not have succeeded on a broken file");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// try a broken file - should fail
		try {
			RectNetFixed.trainFile(prefix + "broken_header.augtrain", false);
			fail("Should not have succeeded on a broken file");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// try a broken file - should fail
		try {
			RectNetFixed.trainFile(prefix + "broken_numinputs.augtrain", false);
			fail("Should not have succeeded on a broken file");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// try a broken file - should fail
		try {
			RectNetFixed.trainFile(prefix + "broken_titles.augtrain", false);
			fail("Should not have succeeded on a broken file");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// try a broken file - should fail
		try {
			RectNetFixed.trainFile(prefix + "broken_trainline.augtrain", false);
			fail("Should not have succeeded on a broken file");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// now a working file
		try {
			net = RectNetFixed.trainFile(prefix + "OR_clean.augtrain", false);
			assertNotNull(net);
			assertEquals("X should be 3", 3, net.getX());
			assertEquals("Y should be 2", 2, net.getY());

			net.setInputs(new double[] { 0, 0 });
			double output = net.getOutput();
			double diff = Math.pow(0 - output, 2);

			net.setInputs(new double[] { 0, 1.0 });
			output = net.getOutput();
			diff += Math.pow(1 - output, 2);

			net.setInputs(new double[] { 1.0, 0.0 });
			output = net.getOutput();
			diff += Math.pow(1 - output, 2);

			net.setInputs(new double[] { 1.0, 1.0 });
			output = net.getOutput();
			diff += Math.pow(1 - output, 2);

			// Should have run to completion
			assertTrue(diff / 4.0 < 0.1);
		} catch (Exception e) {
			// should not go here
			fail("Should not have exploded on a valid file");
		}
		// AND_clean
		try {
			net = RectNetFixed.trainFile(prefix + "AND_clean.augtrain", false);
			assertNotNull(net);
			assertEquals("X should be 4", 4, net.getX());
			assertEquals("Y should be 2", 2, net.getY());

			net.setInputs(new double[] { 0, 0 });
			double output = net.getOutput();
			double diff = Math.pow(0 - output, 2);

			net.setInputs(new double[] { 0, 1.0 });
			output = net.getOutput();
			diff += Math.pow(0 - output, 2);

			net.setInputs(new double[] { 1.0, 0.0 });
			output = net.getOutput();
			diff += Math.pow(0 - output, 2);

			net.setInputs(new double[] { 1.0, 1.0 });
			output = net.getOutput();
			diff += Math.pow(1 - output, 2);

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
		net.setOutputNeuronWeight(0, 0.8);
		net.setOutputNeuronWeight(1, -0.6);
		net.setWeight(0, 0, 1, 0, 0.2);
		net.setWeight(0, 0, 1, 1, -0.11);
		net.setWeight(0, 1, 1, 0, 0.4);
		net.setWeight(0, 1, 1, 1, -0.2);
		testFirstLayerWeightsHelper(net);
		try {
			RectNetFixed.saveNet(prefix + "2by2.augsave", net);
			net = null;
			net = RectNetFixed.loadNet(prefix + "2by2.augsave");
			assertNotNull(net);
			assertEquals("X should be 2",2,net.getX());
			assertEquals("Y should be 2",2,net.getY());
			testFirstLayerWeightsHelper(net);
			double Bo = net.getOutputNeuronWeight(0);
			assertEquals("weights should be correct",Bo,0.8,EPSILON);
			double Do = net.getOutputNeuronWeight(1);
			assertEquals("weights should be correct",Do,-0.6,EPSILON);
			double AB = net.getWeight(0,0,1,0);
			assertEquals("weights should be correct",AB,0.2,EPSILON);
			double CB = net.getWeight(0,1,1,0);
			assertEquals("weights should be correct",CB,0.4,EPSILON);
			double AD = net.getWeight(0,0,1,1);
			assertEquals("weights should be correct",AD,-0.11,EPSILON);
			double CD = net.getWeight(0,1,1,1);
			assertEquals("weights should be correct",CD,-0.2,EPSILON);
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
}
