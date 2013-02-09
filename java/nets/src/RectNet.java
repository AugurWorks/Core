/**
 * Simple rectangular neural network.
 * 
 * @author saf
 * 
 */
public class RectNet implements Net {

	private Input[] inputs;
	// Every neuron with the same i is in the
	// same "layer"
	private Neuron[][] neurons;
	private int x;
	private int y;
	// There's only one final output neuron
	// since this is built to make booleans.
	private Neuron output;
	private boolean verbose = false;

	/**
	 * Constructs a new rectnet with 10 inputs and 5 layers of network.
	 */
	public RectNet() {
		this.x = 5;
		this.y = 10;
		init();
	}

	/**
	 * Constructs a new rectnet with given depth and number of inputs.
	 * 
	 * @param depth
	 *            number of layers in the network
	 * @param numInputs
	 *            number of inputs to the network
	 */
	public RectNet(int depth, int numInputs) {
		this.x = depth;
		this.y = numInputs;
		init();
	}
	public RectNet(int depth, int numInputs, boolean verb) {
		this.x = depth;
		this.y = numInputs;
		this.verbose = verb;
		init();
	}

	/**
	 * 
	 * @param leftCol
	 * @param leftRow
	 * @param rightCol
	 * @param rightRow
	 * @return
	 */
	private double getWeight(int leftCol, int leftRow, int rightCol,
			int rightRow) {
		return this.neurons[rightCol][rightRow]
				.getWeight(this.neurons[leftCol][leftRow]);
	}

	/**
	 * 
	 * @param delta
	 * @param leftCol
	 * @param leftRow
	 * @param rightCol
	 * @param rightRow
	 */
	private void changeWeight(double delta, int leftCol, int leftRow,
			int rightCol, int rightRow) {
		this.neurons[rightCol][rightRow].changeWeight(
				this.neurons[leftCol][leftRow], delta);
	}

	/**
	 * Initializes the RectNet by: 1) creating neurons and inputs as necessary
	 * 2) connecting neurons to the inputs 3) connecting neurons to each other
	 * 4) connecting neurons to the output
	 * 
	 * Initial weights are specified by initNum
	 */
	private void init() {
		// Initialize arrays to blank neurons and inputs.
		this.inputs = new Input[y];
		this.neurons = new Neuron[x][y];
		this.output = new Neuron();
		output.setName("output");
		for (int j = 0; j < this.y; j++) {
			this.inputs[j] = new Input();
			for (int i = 0; i < this.x; i++) {
				this.neurons[i][j] = new Neuron();
				this.neurons[i][j].setName("(" + i + "," + j + ")");
			}
		}
		// Make connections between neurons and inputs.
		for (int j = 0; j < this.y; j++) {
			this.neurons[0][j].addInput(this.inputs[j], initNum());
		}
		// Make connections between neurons and neurons.
		for (int leftCol = 0; leftCol < this.x - 1; leftCol++) {
			int rightCol = leftCol + 1;
			for (int leftRow = 0; leftRow < this.y; leftRow++) {
				for (int rightRow = 0; rightRow < this.y; rightRow++) {
					this.neurons[rightCol][rightRow].addInput(
							this.neurons[leftCol][leftRow], initNum());
				}
			}
		}
		// Make connections between output and neurons.
		for (int j = 0; j < this.y; j++) {
			this.output.addInput(this.neurons[this.x - 1][j], initNum());
		}
	}

	/**
	 * Allows network weight initialization to be changed in one location.
	 * 
	 * @return a double that can be used to initialize weights between network
	 *         connections.
	 */
	private double initNum() {
		return Math.random() - 0.5;
	}

	/**
	 * Sets the inputs of this network to the values given. Length of inpts must
	 * be equal to the "height" of the network.
	 * 
	 * @param inpts
	 *            array of double to set as network inputs.
	 */
	public void setInputs(double[] inpts) {
		assert (inpts.length == this.y);
		for (int j = 0; j < this.y; j++) {
			this.inputs[j].setValue(inpts[j]);
		}
	}

	/**
	 * Returns the output value from this network run.
	 */
	@Override
	public double getOutput() {
		double code = Math.random();
		return this.output.getOutput(code);
	}

	/**
	 * Trains the network on a given input with a given output the number of
	 * times specified by iterations. Trains via a backpropagation algorithm.
	 * 
	 * @param inpts
	 *            input values for the network.
	 * @param desired
	 *            what the result of the network should be.
	 * @param learningConstant
	 *            the "rate" at which the network learns
	 * @param iterations
	 *            number of times to train the network.
	 */
	public void train(double[] inpts, double desired, int iterations,
			double learningConstant) {
		assert (iterations > 0);
		for (int lcv = 0; lcv < iterations; lcv++) {
			// Set the inputs
			this.setInputs(inpts);
			// Compute the last node error
			double deltaF = this.outputError(desired);
			if (verbose) {
				System.out.println("DeltaF: " + deltaF);
			}
			// For each interior node, compute the weighted error
			// deltas are of the form
			// delta[col][row]
			double[][] deltas = new double[this.x + 1][this.y];
			// spoof the rightmost deltas
			for (int j = 0; j < y; j++) {
				deltas[this.x][j] = deltaF;
			}
			int leftCol = 0;
			int leftRow = 0;
			int rightCol = 0;
			int rightRow = 0;
			for (leftCol = this.x - 1; leftCol >= 0; leftCol--) {
				rightCol = leftCol + 1;
				for (leftRow = 0; leftRow < this.y; leftRow++) {
					double lastOutput = this.neurons[leftCol][leftRow]
							.getLastOutput();
					double delta = lastOutput * (1 - lastOutput);
					double summedRightWeightDelta = 0;
					for (rightRow = 0; rightRow < this.y; rightRow++) {
						if (rightCol == this.x) {
							summedRightWeightDelta += this.output
									.getWeight(this.neurons[leftCol][leftRow])
									* deltaF;
						} else {
							// summing w * delta
							summedRightWeightDelta += getWeight(leftCol,
									leftRow, rightCol, rightRow)
									* deltas[rightCol][rightRow];
						}
					}
					deltas[leftCol][leftRow] = delta * summedRightWeightDelta;
					if (verbose) {
						System.out.println("leftCol: " + leftCol
								+ ", leftRow: " + leftRow + ", lo*(1-lo): "
								+ delta);
						System.out.println("leftCol: " + leftCol
								+ ", leftRow: " + leftRow + ", srwd: "
								+ summedRightWeightDelta);
						System.out.println("leftCol: " + leftCol
								+ ", leftRow: " + leftRow + ", delta: "
								+ deltas[leftCol][leftRow]);
					}
				}
			}
			// now that we have the deltas, we can change the weights
			// again, we special case the last neuron
			for (int j = 0; j < this.y; j++) {
				// w' = w + r*i*delta
				// r is the learning constant
				// i is the output from the leftward neuron
				double dw = learningConstant
						* this.neurons[this.x - 1][j].getLastOutput() * deltaF;
				this.output.changeWeight(this.neurons[this.x - 1][j], dw);
			}
			// now we do the same for the internal nodes
			for (leftCol = this.x - 2; leftCol >= 0; leftCol--) {
				rightCol = leftCol + 1;
				for (leftRow = 0; leftRow < this.y; leftRow++) {
					for (rightRow = 0; rightRow < this.y; rightRow++) {
						// w' = w + r*i*delta
						// r is the learning constant
						// i is the output from the leftward neuron
						double dw = learningConstant
								* this.neurons[leftCol][leftRow]
										.getLastOutput()
								* deltas[rightCol][rightRow];
						this.neurons[rightCol][rightRow].changeWeight(
								this.neurons[leftCol][leftRow], dw);
						if (verbose) {
							System.out.println(leftCol + "," + leftRow + "->"
									+ rightCol + "," + rightRow);
							System.out.println(this.neurons[rightCol][rightRow]
									.getWeight(this.neurons[leftCol][leftRow]));
						}
					}
				}
			}
		}
	}

	/**
	 * Computes the error in the network, assuming that the proper inputs have
	 * been set before this method is called.
	 * 
	 * @param desired
	 *            the desired output.
	 * @return error.
	 */
	private double outputError(double desired) {
		this.getOutput();
		return this.output.getLastOutput() * (1 - this.output.getLastOutput())
				* (desired - this.output.getLastOutput());
	}

	public static void main(String[] args) {
		RectNet r = new RectNet(2, 2, false);
		double[] or00 = { 0, 0 };
		double[] or01 = { 0, 1 };
		double[] or10 = { 1, 0 };
		double[] or11 = { 1, 1 };
		int outerRounds = 10;
		int innerRounds = 10000;
		double lc = 0.2;
		for (int i = 0; i < outerRounds; i++) {
			r.train(or00, 0.2, innerRounds, lc);
			r.train(or01, 0.2, innerRounds, lc);
			r.train(or10, 0.2, innerRounds, lc);
			r.train(or11, 0.8, innerRounds, lc);
		}
		// test
		System.out.println("-------------------------");
		System.out.println("Test Results: ");
		r.setInputs(or00);
		System.out.println(r.getOutput());
		r.setInputs(or01);
		System.out.println(r.getOutput());
		r.setInputs(or10);
		System.out.println(r.getOutput());
		r.setInputs(or11);
		System.out.println(r.getOutput());
	}
}
