package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alfred.FixedNeuron;
import alfred.Input;

/**
 * A testing suite for the FixedNeuron class.
 * 
 * @author saf
 * 
 */
public class FixedNeuronTest {
	FixedNeuron f;
	static int SIZE = 10;
	Random random = new Random();
	double epsilon = 0.000001;

	/**
	 * Provides a random legal index based on SIZE
	 */
	private int randomLegalIndex() {
		return random.nextInt(SIZE - 1);
	}

	/**
	 * Sets up the test fixture. (Called before every test case method.)
	 */
	@Before
	public void setUp() {
		f = new FixedNeuron(SIZE);
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */
	@After
	public void tearDown() {
		f = null;
	}

	/**
	 * Simple constructor test.
	 */
	@Test
	public void testFixedNeuron() {
		assertNotNull(f);
	}

	/**
	 * Test that name of FixedNeuron is successfully changed.
	 */
	@Test
	public void testSetGetName() {
		assertEquals("Name should be empty after constructor.", f.getName(), "");

		String randomName = Long.toHexString(Double.doubleToLongBits(Math
				.random()));
		f.setName(randomName);
		assertEquals("Name should change.", f.getName(), randomName);

		String nullName = null;
		f.setName(nullName);
		assertNull(f.getName(), nullName);

	}

	/**
	 * Tests the filling of inputs. This is just a sanity check that there's no
	 * exceptions thrown; in FixedNeurons, complaints don't occur when you try
	 * to add too many inputs.
	 */
	@Test
	public void testAddInput() {
		// adding Input objects
		for (int i = 0; i < SIZE + 1; i++) {
			f.addInput(new Input(), random.nextDouble());
		}
		// adding FixedNeuron objects
		f = new FixedNeuron(SIZE);
		for (int i = 0; i < SIZE + 1; i++) {
			f.addInput(new FixedNeuron(SIZE), random.nextDouble());
		}
	}

	@Test
	public void testChangeWeight() {
		// initial
		double w = f.getWeight(randomLegalIndex());
		assertEquals("Initial weight should be zero", w, 0, epsilon);

		// simple change weight and confirm
		double newWeight = random.nextDouble();
		int idx = randomLegalIndex();
		f.changeWeight(idx, newWeight);
		assertEquals("Weight should have changed", f.getWeight(idx), newWeight,
				epsilon);

		// now add inputs and try again
		newWeight = random.nextDouble();
		idx = randomLegalIndex();
		for (int i = 0; i < SIZE; i++) {
			f.addInput(new FixedNeuron(SIZE), random.nextDouble());
		}
		f.changeWeight(randomLegalIndex(), random.nextDouble());
		assertEquals("Weight should have changed", f.getWeight(idx), newWeight,
				epsilon);

		// now try illegal indices
		try {
			// too small
			f.changeWeight(-1, random.nextDouble());
			fail("Should not be able to change weight from -1");
		} catch (Exception e) {
			assertTrue(true);
		}
		try {
			// too big
			f.changeWeight(SIZE + 1, random.nextDouble());
			fail("Should not be able to change weight from index outside range");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetOutput() {
		int code = 0;
		// for initial settings, 0 should be lastCode, lastOutput should be 0.
		assertEquals("Initial output is zero",f.getOutput(code),)
	}

	@Test
	public void testGetLastOutput() {
		// initial set to 0.
		assertEquals("Initial lastOutput should be zero", f.getLastOutput(), 0,
				epsilon);
	}
}
