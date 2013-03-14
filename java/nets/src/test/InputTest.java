package test;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alfred.Input;

public class InputTest {
	Input inp;
	Random random = new Random();
	static double EPSILON = 0.000001;

	@Before
	public void setUp() throws Exception {
		inp = new Input();
	}

	@After
	public void tearDown() throws Exception {
		inp = null;
	}

	@Test
	public void testInput() {
		inp = null;
		inp = new Input();
		assertNotNull(inp);
	}

	@Test
	public void testInputDouble() {
		inp = null;
		inp = new Input(random.nextDouble());
		assertNotNull(inp);
	}

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
