package alfred;
public class FixedNeuron implements Inp {
	private double[] weights;
	private Inp[] inputs;
	private String name;
	private int numInputsFilled;
	private int numInputs;
	private double lastOutput;
	private int lastCode;

	/**
	 * Instantiates a new neuron with empty lists of inputs, weights, and names.
	 * 
	 * @param numInputs
	 *            number of inputs that this neuron will have
	 */
	public FixedNeuron(int numInputs) {
		this.weights = new double[numInputs];
		this.inputs = new Inp[numInputs];
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
	public void addInput(Inp n, double w) {
		if (numInputsFilled >= this.numInputs) {
			System.err.println("Too many inputs added to neuron");
			return;
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
	public void changeWeight(int index, double w) {
		if (index < 0 || index >= this.numInputs) {
			System.err.println("Index out of accepted range.");
			return;
		}
		this.weights[index] = this.weights[index] + w;
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
	public double getOutput(int code) {
		if (code == lastCode) {
			return lastOutput;
		}
		lastCode = code;
		double sum = 0;
		for (int i = 0; i < this.numInputs; i++) {
			sum += this.weights[i] * this.inputs[i].getOutput(code);
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
	public double getLastOutput() {
		return this.lastOutput;
	}

	/**
	 * Returns the weight from this neuron to a different neuron given the index
	 * to look in.
	 * 
	 * @param index
	 *            index of Neuron to return weight to.
	 * @return weight to the given neuron
	 */
	public double getWeight(int index) {
		if (index < 0 || index >= this.numInputs) {
			System.err.println("Index out of accepted range.");
			throw new RuntimeException("Index out of accepted range.");
		}
		return this.weights[index];
	}

}
