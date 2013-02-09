import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Simple rectangular neural network.
 * 
 * @author saf
 * 
 */
public class RectNet extends Net {

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

	/**
	 * Constructs a new RectNet with given depth and number of inputs. Sets the
	 * verbose boolean as given.
	 * 
	 * @param depth
	 * @param numInputs
	 * @param verb
	 *            true when RectNet displays debug output.
	 */
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

	public static void trainFile(String fileName, boolean verbose) {
		boolean valid = Net.validateAUGt(fileName);
		if (!valid) {
			System.err.println("File not valid format.");
			System.exit(1);
		}
		// Now we need to pull information out of the augtrain file.
		Charset charset = Charset.forName("US-ASCII");
		Path file = Paths.get(fileName);
		String line = null;
		int lineNumber = 1;
		String[] lineSplit;
		int side = 0;
		int depth = 0;
		int rowIter = 0;
		int fileIter = 0;
		double learningConstant = 0;
		int minTrainingRounds = 0;
		double cutoff = 0;
		ArrayList<double[]> inputSets = new ArrayList<double[]>();
		ArrayList<Double> targets = new ArrayList<Double>();
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			while ((line = reader.readLine()) != null) {
				try {
					lineSplit = line.split(" ");
					switch (lineNumber) {
					case 1:
						String[] size = lineSplit[1].split(",");
						side = Integer.valueOf(size[0]);
						depth = Integer.valueOf(size[1]);
						break;
					case 2:
						size = lineSplit[1].split(",");
						rowIter = Integer.valueOf(size[0]);
						fileIter = Integer.valueOf(size[1]);
						learningConstant = Double.valueOf(size[2]);
						minTrainingRounds = Integer.valueOf(size[3]);
						cutoff = Double.valueOf(size[4]);
						break;
					case 3:
						// Titles
						break;
					default:
						// expected
						double target = Double.valueOf(lineSplit[0]);
						targets.add(target);
						// inputs
						double[] input = new double[side];
						size = lineSplit[1].split(",");
						for (int i = 0; i < side; i++) {
							input[i] = Double.valueOf(size[i]);
						}
						inputSets.add(input);
						break;
					}
					lineNumber++;
				} catch (Exception e) {
					System.err
							.println("Training failed at line: " + lineNumber);
				}
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		if (verbose) {
			System.out.println("-------------------------");
			System.out.println("File path: " + fileName);
			System.out.println("Number Inputs: " + side);
			System.out.println("Net depth: " + depth);
			System.out.println("Number training sets: " + targets.size());
			System.out.println("Row iterations: " + rowIter);
			System.out.println("File iterations: " + fileIter);
			System.out.println("Learning constant: " + learningConstant);
			System.out.println("Minimum training rounds: " + minTrainingRounds);
			System.out.println("Performance cutoff: " + cutoff);
			System.out.println("-------------------------");
		}
		// Actually do the training part
		RectNet r = new RectNet(side, depth);
		double maxScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < fileIter; i++) {
			for (int lcv = 0; lcv < inputSets.size(); lcv++) {
				r.train(inputSets.get(lcv), targets.get(lcv), rowIter,
						learningConstant);
			}
			double score = 0;
			for (int lcv = 0; lcv < inputSets.size(); lcv++) {
				r.setInputs(inputSets.get(lcv));
				score += Math.pow((targets.get(lcv) - r.getOutput()), 2);
			}
			score *= -1.0;
			if (score > -1.0*cutoff) {
				System.out.println("Score of " + -1 * score
						+ " achieved after " + i + " training rounds.");
				break;
			}

			if (score > maxScore) {
				maxScore = score;
			} else if (i < minTrainingRounds) {
				continue;
			} else {
				System.out.println("Local max hit.");
				System.out.println("Rounds trained: " + i);
				System.out.println("Final score: " + -1 * score);
				break;
			}
		}
		// Results
		System.out.println("-------------------------");
		System.out.println("Test Results: ");
		for (int lcv = 0; lcv < inputSets.size(); lcv++) {
			r.setInputs(inputSets.get(lcv));
			System.out.println("Input " + lcv);
			System.out.println("\tTarget: " + targets.get(lcv));
			System.out.println("\tActual: " + r.getOutput());
		}
	}

	public static void main(String[] args) {
		String defaultFile = "C:\\Users\\saf\\workspace\\AugurWorks\\Core\\java\\nets\\test_files\\OR_clean.augtrain";
		RectNet.trainFile(defaultFile, true);
		System.exit(0);
	}
}
