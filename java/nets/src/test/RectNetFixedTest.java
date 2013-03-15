package test;

import static org.junit.Assert.*;
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
	private static boolean VERBOSE = false;

	@Before
	public void setUp() throws Exception {
		net = new RectNetFixed(DEPTH, NUMINPUTS);
	}

	@After
	public void tearDown() throws Exception {
		net = null;
	}

	@Test
	public void testGetOutput() {
		fail("Not yet implemented");
	}

	/**
	 * Contains constructor tests.
	 */
	@Test
	public void testRectNetFixed() {
		net = null;
		net = new RectNetFixed();
		assertNotNull(net);
		net = null;

		net = new RectNetFixed(random.nextInt(10) + 1, random.nextInt(1000) + 1);
		assertNotNull(net);
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
		net = new RectNetFixed(random.nextInt(10) + 1,
				random.nextInt(1000) + 1, false);
		assertNotNull(net);
		net = null;
		net = new RectNetFixed(random.nextInt(10) + 1,
				random.nextInt(1000) + 1, true);
		assertNotNull(net);
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
			for (int rightCol = 1; rightCol < DEPTH; rightCol++) {
				for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
					for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
						double weight = scalar
								* ((leftCol + rightCol) * rightRow - rightCol);
						net.setWeight(leftCol, leftRow, rightCol, rightRow,
								weight);
					}
				}
			}
		}
		for (int leftCol = 0; leftCol < DEPTH - 1; leftCol++) {
			for (int rightCol = 1; rightCol < DEPTH; rightCol++) {
				for (int leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
					for (int rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
						double desired = scalar
								* ((leftCol + rightCol) * rightRow - rightCol);
						double actual = net.getWeight(leftCol, leftRow,
								rightCol, rightRow);
						assertEquals("Weight should have changed", actual,
								desired, EPSILON);
					}
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
		// left col too small
		try {
			leftCol = -1;
			rightCol = leftCol + 1;
			rightRow = random.nextInt(NUMINPUTS - 1);
			leftRow = random.nextInt(NUMINPUTS - 1);
			net.getWeight(leftCol, leftRow, rightCol, rightRow);
			fail("Should not get negative indexed weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// left col too big
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
		// left col too small
		try {
			leftCol = -1;
			rightCol = leftCol + 1;
			rightRow = random.nextInt(NUMINPUTS - 1);
			leftRow = random.nextInt(NUMINPUTS - 1);
			net.setWeight(leftCol, leftRow, rightCol, rightRow, w);
			fail("Should not get negative indexed weight");
		} catch (Exception e) {
			// should go here
			assertTrue(true);
		}
		// left col too big
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
		for (int i = 0; i < NUMINPUTS; i++) {
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
		fail("Not yet implemented");
	}

	@Test
	public void testTrainFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveNet() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadNet() {
		fail("Not yet implemented");
	}

	@Test
	public void testTestNet() {
		fail("Not yet implemented");
	}

	@Test
	public void testPredictTomorrow() {
		fail("Not yet implemented");
	}
}
