package alfred;

import java.math.BigDecimal;

import org.apache.commons.lang3.Validate;

public class FixedNeuron implements Input {
	private BigDecimal[] weights;
	private Input[] inputs;
	private String name;
	private int numInputsFilled;
	private int numInputs;
	private BigDecimal lastOutput;
	private int lastCode;

	/**
	 * Instantiates a new neuron with empty lists of inputs, weights, and names.
	 * 
	 * @param numInputs
	 *            number of inputs that this neuron will have
	 */
	public FixedNeuron(int numInputs) {
		this.weights = new BigDecimal[numInputs];
		this.inputs = new Input[numInputs];
		this.name = "";
		this.numInputsFilled = 0;
		this.numInputs = numInputs;
	}

	/**
	 * Sets the name of this neuron.
	 * 
	 * @param n
	 *            name to give this neuron.
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * returns the name of this neuron.
	 * 
	 * @return the name of this neuron.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Add a neuron to the inputs list with a given weight. Assumes the neuron
	 * is not already in the inputs list.
	 * 
	 * @param n
	 *            Neuron to add
	 * @param w
	 *            Weight to set
	 */
	public void addInput(Input n, BigDecimal w) {
		Validate.isTrue(numInputsFilled < this.numInputs);
		Validate.isTrue(this.numInputsFilled >= 0);
		if (numInputsFilled >= this.numInputs) {
			System.err.println("Too many inputs added to neuron");
			throw new IllegalStateException("Too many inputs added to neuron");
		}
		this.inputs[numInputsFilled] = n;
		this.weights[numInputsFilled] = w;
		numInputsFilled++;
	}

	/**
	 * Changes the weight from this neuron to another, given the index of that
	 * neuron.
	 * 
	 * @param index
	 *            index of input to change weight to
	 * @param w
	 *            Amount to change weight by.
	 */
	public void changeWeight(int index, BigDecimal w) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < this.numInputs);
		if (index < 0 || index >= this.numInputs) {
			System.err.println("Index out of accepted range.");
			throw new IllegalArgumentException("Index out of range");
		}
		this.weights[index] = this.weights[index].add(w);
	}

	/**
	 * Sets the weight from this neuron to another, given the index of that
	 * neuron and the new weight to set.
	 * 
	 * @param index
	 *            index of the input to change weight to
	 * @param w
	 *            new weight value
	 */
	public void setWeight(int index, BigDecimal w) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < this.numInputs);
		if (index < 0 || index >= this.numInputs) {
			System.err.println("Index out of accepted range.");
			throw new IllegalArgumentException("Index out of range");
		}
		this.weights[index] = w;
	}

	/**
	 * Performs the sigmoid function on an input y = 1 / (1 + exp(-alpha*x))
	 * Used internally in getOutput method. Alpha is set to 3 currently.
	 * 
	 * @param input
	 *            X
	 * @return sigmoid(x)
	 */
	private BigDecimal sigmoid(BigDecimal input) {
		//	1.0 / (1.0 + Math.exp(-3.0 * input));
		return BigDecimal.ONE.divide(
				BigDecimal.ONE.add(
						BigDecimals.exp(BigDecimal.valueOf(-3).multiply(input))));
	}

	/**
	 * Gets the output of this neuron, which implicitly calculates the outputs
	 * of all the neurons below, unless the given code is the same as the prior
	 * code (indicating that a complete recompute is not necessary).
	 * 
	 * @param code
	 *            an integer code denoting whether a new computation is
	 *            necessary.
	 * @return the output from this neuron.
	 */
	public BigDecimal getOutput(int code) {
		if (code == lastCode) {
			return lastOutput;
		}
		lastCode = code;
		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < this.numInputs; i++) {
			sum = sum.add(this.weights[i].multiply(this.inputs[i].getOutput(code)));
		}
		sum = sigmoid(sum);
		this.lastOutput = sum;
		return sum;
	}

	/**
	 * Gets the output of this neuron, requiring recursion.
	 * 
	 * @return the output from this neuron
	 */
	public BigDecimal getOutput() {
		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < this.numInputs; i++) {
			sum = sum.add(this.weights[i].multiply(this.inputs[i].getOutput()));
		}
		sum = sigmoid(sum);
		this.lastOutput = sum;
		return sum;
	}

	/**
	 * Returns the last output that this neuron computed.
	 * 
	 * @return the last output that this neuron computed.
	 */
	public BigDecimal getLastOutput() {
		return this.lastOutput;
	}

	/**
	 * Returns the output of this neuron when given an array of inputs.
	 * Basically performs the dot product between ins and weights, then outputs
	 * the sigmoid of that.
	 * 
	 * @param ins
	 *            the input array of prior row's outputs.
	 * @return the output of this neuron
	 */
	public BigDecimal getOutput(BigDecimal[] ins) {
		Validate.isTrue(ins.length == this.numInputs);
		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < this.numInputs; i++) {
			sum = sum.add(this.weights[i].multiply(ins[i]));
		}
		sum = sigmoid(sum);
		this.lastOutput = sum;
		return sum;
	}

	/**
	 * Returns the weight from this neuron to a different neuron given the index
	 * to look in.
	 * 
	 * @param index
	 *            index of Neuron to return weight to.
	 * @return weight to the given neuron
	 */
	public BigDecimal getWeight(int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < this.numInputs);
		if (index < 0 || index >= this.numInputs) {
			System.err.println("Index out of accepted range.");
			throw new IllegalArgumentException("Index out of accepted range.");
		}
		return this.weights[index];
	}

}
