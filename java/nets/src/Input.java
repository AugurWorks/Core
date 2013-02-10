/**
 * Represents and input to the Net. Has a constant output that can be set.
 * 
 * @author saf
 * 
 */
public class Input implements Inp {
	// The constant output of this Input.
	private double value;

	/**
	 * Instantiates an Input with default value of 0.
	 */
	public Input() {
		this.value = 0;
	}

	/**
	 * Instantiates and input with the given value.
	 * 
	 * @param v
	 *            initial value of this Input.
	 */
	public Input(double v) {
		this.value = v;
	}

	/**
	 * Returns the value of this Input.
	 * 
	 * @return the value of this Input.
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Returns the value of this Input
	 * 
	 * @param code
	 *            unused.
	 */
	public double getOutput(int code) {
		return this.value;
	}

	/**
	 * Sets the value of this Input.
	 * 
	 * @param v
	 *            value to set this Input to.
	 */
	public void setValue(double v) {
		this.value = v;
	}
}
