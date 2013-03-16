package alfred;

/**
 * Represents any type of object that returns an output. Used for Neuron and
 * Input.
 * 
 * @author saf
 * 
 */
public interface Inp {
	/**
	 * Returns the output of this Inp.
	 * 
	 * @param code
	 *            Used in implementations for caching prior outputs.
	 * @return Output of this Inp
	 */
	public double getOutput(int code);

	/**
	 * Returns the output of this Inp. Requires recursion.
	 * 
	 * @return output of this Inp
	 */
	public double getOutput();
}
