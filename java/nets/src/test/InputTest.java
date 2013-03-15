package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alfred.Input;

public class InputTest {
	private Input inp;
	private Random random = new Random();
	private static double EPSILON = 0.000001;

	/**
	 * Runs before each test method. Constructs a new Input.
	 */
	@Before
	public void setUp() {
		inp = new Input();
	}

	/**
	 * Runs after each test method.
	 */
	@After
	public void tearDown() {
		inp = null;
	}

	/**
	 * Tests the constructors.
	 */
	@Test
	public void testInput() {
		inp = null;
		inp = new Input();
		assertNotNull(inp);
		assertEquals("value should initialize to 0", inp.getValue(), 0, EPSILON);

		inp = null;
		double w = random.nextDouble();
		inp = new Input(w);
		assertNotNull(inp);
		assertEquals("value should initialize to " + w, inp.getValue(), w,
				EPSILON);
	}

	/**
	 * Tests getOutput and getValue.
	 */
	@Test
	public void testGetValue() {
		assertEquals("Default value should be 0", inp.getValue(), 0, EPSILON);
		assertEquals("Default output should be 0", inp.getOutput(0), 0, EPSILON);

		double expected = random.nextDouble();
		inp.setValue(expected);

		assertEquals("value should have changed", inp.getValue(), expected,
				EPSILON);
		assertEquals("output should have changed", inp.getOutput(0), expected,
				EPSILON);
	}
}
