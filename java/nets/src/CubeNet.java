import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CubeNet {
	private Input[][] inputs;
	private Neuron[][][] neurons;
	private int x;
	private int y;
	private int z;
	public static boolean verbose = false;

	public CubeNet() {
		this.x = 10;
		this.y = 10;
		this.z = 5;
		init();
	}

	public CubeNet(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		init();
	}

	public void setInputs(double[][] nums) {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				this.inputs[i][j].setValue(nums[i][j]);
			}
		}
	}

	public void setInputs(double[][][] nums, int depth) {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				this.inputs[i][j].setValue(nums[i][j][depth]);
			}
		}
	}

	public static void printInputs(double[][][] nums, int depth) {
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < nums[0].length; j++) {
				System.out.print(nums[i][j][depth] + " ");
			}
			System.out.print("\n");
		}
	}

	public void init() {
		this.inputs = new Input[x][y];
		this.neurons = new Neuron[x][y][z];
		// Set everything to null
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				this.inputs[i][j] = new Input();
				for (int k = 0; k < z; k++) {
					this.neurons[i][j][k] = new Neuron();
					this.neurons[i][j][k].setName("(" + i + "," + j + "," + k
							+ ")");
				}
			}
		}
		// Make all the connections
		for (int k = 0; k < z; k++) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					// if k == 0, connect to inputs
					if (k == 0) {
						this.neurons[i][j][k].addInput(this.inputs[i][j], 1);
					} else {
						// else, connect each neuron in
						// a layer to each neuron in the
						// previous layer.
						for (int ii = 0; ii < x; ii++) {
							for (int jj = 0; jj < y; jj++) {
								this.neurons[i][j][k].addInput(
										this.neurons[ii][jj][k - 1],
										Math.random());
							}
						}
					}
				}
			}
		}
	}

	public double[][] getOutput() {
		double code = Math.random();
		double[][] outs = new double[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				outs[i][j] = this.neurons[i][j][z - 1].getOutput(code);
			}
		}
		return outs;
	}

	public void setWeight(int x, int y, int z, int x1, int y1, int z1,
			double weight) {
		if (x >= this.x || y > this.y || z > this.z || x1 > this.x
				|| y1 > this.y || z1 > this.z || x < 0 || y < 0 || z < 0
				|| x1 < 0 || y1 < 0 || z1 < 0) {
			throw new RuntimeException();
		} else {
			// from xyz to x1y1z1
			this.neurons[x1][y1][z1]
					.changeWeight(this.neurons[x][y][z], weight);
		}
	}

	public double getWeight(int x, int y, int z, int x1, int y1, int z1) {
		if (x >= this.x || y > this.y || z > this.z || x1 > this.x
				|| y1 > this.y || z1 > this.z || x < 0 || y < 0 || z < 0
				|| x1 < 0 || y1 < 0 || z1 < 0) {
			throw new RuntimeException();
		} else {
			// from xyz to x1y1z1
			return this.neurons[x1][y1][z1].getWeight(this.neurons[x][y][z]);
		}
	}

	public void printOutput() {
		double code = Math.random();
		double[][] outs = new double[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				outs[i][j] = this.neurons[i][j][z - 1].getOutput(code);
				System.out.print(outs[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void train(double r, double samples[][][],
			double[][][] desiredOutput, double perfLimit, int innerIter,
			int outerIter) {
		assert (samples.length == x);
		assert (samples[0].length == y);
		assert (samples[0][0].length == desiredOutput.length);
		int numSamples = samples[0][0].length;
		// 1) pick a rate parameter r
		// 2) until perf is satisfactory
		double min = Double.NEGATIVE_INFINITY;
		for (int outCount = 0; outCount < outerIter; outCount++) {
			// for each sample input
			for (int sampleNumber = 0; sampleNumber < numSamples; sampleNumber++) {
				for (int count = 0; count < innerIter; count++) {
					double[][] actual;
					double[][] desired = new double[x][y];
					double[][] differenceSquared = new double[x][y];
					double[][][] weightDeltas = new double[x][y][z];
					// System.out.println("Sample number: " + sampleNumber);
					double[][] samp = new double[x][y];
					/*
					 * System.out.println("x: "+x); System.out.println("y: "+y);
					 */
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							samp[i][j] = samples[i][j][sampleNumber];
						}
					}
					if (verbose) {
						System.out.println("\nSample array:");
						for (int i = 0; i < x; i++) {
							for (int j = 0; j < y; j++) {
								/*
								 * System.out.println("i: "+i);
								 * System.out.println("j: "+j);
								 * System.out.println("sn: "+sampleNumber);
								 * System.out.println("samp: " + samp[i][j]);
								 * System.out.
								 * println("s: "+samples[i][j][sampleNumber]);
								 */
								System.out.print("(" + i + "," + j + "): "
										+ samp[i][j] + "   ");
							}
							System.out.print("\n");
						}
						System.out.print("\n");
					}

					// samp is now the sample input array
					// 3) compute the resulting output
					setInputs(samp);
					actual = getOutput();
					if (verbose) {
						System.out.println("Inputs set. Output[0][0] is: "
								+ actual[0][0]);
					}
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							desired[i][j] = desiredOutput[i][j][sampleNumber];
						}
					}
					if (verbose) {
						System.out.println("\nDesired array:");
						for (int i = 0; i < x; i++) {
							for (int j = 0; j < y; j++) {
								System.out.print("(" + i + "," + j + "): "
										+ desired[i][j] + "   ");
							}
							System.out.print("\n");
						}
						System.out.print("\n");
					}

					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							if (verbose) {
								System.out.print("(" + i + "," + j + "): D:"
										+ desired[i][j] + ", A: "
										+ actual[i][j] + "   ");
							}
							differenceSquared[i][j] += (desired[i][j] - actual[i][j])
									* (desired[i][j] - actual[i][j]);
						}
						if (verbose) {
							System.out.print("\n");
						}
					}

					if (verbose) {
						System.out.println("\nDiff^2 array:");
						for (int i = 0; i < x; i++) {
							for (int j = 0; j < y; j++) {
								System.out.print("(" + i + "," + j + "): "
										+ differenceSquared[i][j] + "   ");
							}
							System.out.print("\n");
						}
						System.out.print("\n");
					}

					double[][][] b = new double[x][y][z];
					// 4) compute B for all nodes in the output layer using
					// Bz = dz - oz
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							b[i][j][z - 1] = desiredOutput[i][j][sampleNumber]
									- actual[i][j];
						}
					}
					// 5) compute B for all other nodes using
					// Bj = sum k ( wj->k ok( 1 - ok ) Bk
					// from right to left:

					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							for (int k = z - 2; k > 0; k--) {
								for (int ii = 0; ii < x; ii++) {
									for (int jj = 0; jj < y; jj++) {
										double wjk = neurons[i][j][k + 1]
												.getWeight(neurons[ii][jj][k]);
										double ok = neurons[ii][jj][k + 1]
												.getLastOutput();
										double bk = b[ii][jj][k + 1];
										b[i][j][k] += wjk * ok * (1 - ok) * bk;
									}
								}
							}
						}
					}

					// 6) compute weight changes for all weights using
					// dWi->j = r oi oj ( 1 - oj ) Bj
					double[][][] dw = new double[x][y][z];
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							for (int k = 0; k < z - 1; k++) {
								double oi = neurons[i][j][k].getLastOutput();
								double oj = neurons[i][j][k + 1]
										.getLastOutput();
								double bj = b[i][j][k + 1];
								dw[i][j][k] = r * oi * oj * (1 - oj) * bj;
								// System.out.print("dw: ");
								// System.out.print(dw[i][j][k] + " ");
							}
						}
					}
					// put the dw into the weight deltas
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							for (int k = 0; k < z; k++) {
								weightDeltas[i][j][k] += dw[i][j][k];
							}
						}
					}
					if (verbose) {
						System.out.println("\nWeight Deltas arrays:");
						for (int k = 0; k < z; k++) {
							System.out.println("K: " + k);
							for (int i = 0; i < x; i++) {
								for (int j = 0; j < y; j++) {
									System.out.print("(" + i + "," + j + "): "
											+ weightDeltas[i][j][k] + "   ");
								}
								System.out.print("\n");
							}
							System.out.print("\n");
						}
					}
					// Check the break condition
					double ds = 0;
					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							ds += differenceSquared[i][j];
						}
					}
					ds *= -1;
					if (ds > min) {
						min = ds;
						// System.out.println(ds);
					}
					// if (ds > perfLimit) {
					// break;
					// }
					// 7) add up the weight changes for all the sample inputs
					// and change the weights
					// new weight deltas are in weightDeltas
					if (verbose) {
						for (int k = 1; k < z; k++) {
							for (int ii = 0; ii < x; ii++) {
								for (int jj = 0; jj < x; jj++) {
									System.out.println("(" + ii + "," + jj
											+ "," + k + ") "
											+ weightDeltas[ii][jj][k - 1]);
								}
							}
						}
					}

					for (int i = 0; i < x; i++) {
						for (int j = 0; j < y; j++) {
							for (int k = 1; k < z; k++) {
								for (int ii = 0; ii < x; ii++) {
									for (int jj = 0; jj < x; jj++) {
										this.neurons[i][j][k].changeWeight(
												this.neurons[ii][jj][k - 1],
												weightDeltas[ii][jj][k - 1]);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		//mainOR(null);
		//System.exit(0);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String text = "";
		boolean looking = true;
		String def = "C:\\Users\\Stephen\\workspace\\AugurWorks\\Core\\java\\nets\\test_files\\test.augtrain";
		try {
			while (looking) {
				System.out
						.println("Please enter an absolute location for a training file, or press ENTER for default (" + def + "):");
				text = in.readLine();
				if (text.equals("")) {
					trainFile(def);
				} else if (!text.endsWith(".augtrain")) {
					System.out.println("Please enter a valid training file...");
				} else {
					looking = false;
					trainFile(text);
				}
			}
		} catch (IOException e) {
			System.err.println("File not found: " + text);
		}
	}

	public static void trainFile(String fileName) {
		boolean valid = validateAUGt(fileName);
		if (verbose) {
			System.out.println("Valid file? " + valid);
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
		ArrayList<Double[][]> inputSets = new ArrayList<Double[][]>();
		ArrayList<Double> targets = new ArrayList<Double>();
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			while ((line = reader.readLine()) != null) {
				try {
					lineSplit = line.split(" ");
					switch (lineNumber) {
					case 1:
						String[] size = lineSplit[1].split(",");
						side = Integer.valueOf(size[0]);
						depth = Integer.valueOf(size[2]);
						break;
					case 2:
						size = lineSplit[1].split(",");
						rowIter = Integer.valueOf(size[0]);
						fileIter = Integer.valueOf(size[1]);
						break;
					case 3:
						break;
					default:
						// expected
						double target = Double.valueOf(lineSplit[0]);
						targets.add(target);
						// inputs
						Double[][] input = new Double[side][side];
						size = lineSplit[1].split(",");
						for (int i = 0; i < side; i++) {
							for (int j = 0; j < side; j++) {
								input[i][j] = Double
										.valueOf(size[i * side + j]);
							}
						}
						inputSets.add(input);
						break;
					}
					lineNumber++;
				} catch (Exception e) {
					if (verbose)
						System.err.println("Training failed at line: "
								+ lineNumber);
					System.exit(1);
				}
			}
		} catch (IOException x) {
			if (verbose)
				System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		// Now put the training data in an [][][]
		double[][][] training = new double[side][side][inputSets.size()];
		for (int k = 0; k < inputSets.size(); k++) {
			for (int i = 0; i < side; i++) {
				for (int j = 0; j < side; j++) {
					training[i][j][k] = inputSets.get(k)[i][j];
				}
			}
		}
		double[][][] targ = new double[side][side][inputSets.size()];
		for (int k = 0; k < inputSets.size(); k++) {
			for (int i = 0; i < side; i++) {
				for (int j = 0; j < side; j++) {
					if (i == 0 && j == 0) {
						targ[i][j][k] = targets.get(k);
					} else {
						targ[i][j][k] = 0.5;
					}
				}
			}
		}
		// Create the net
		CubeNet c = new CubeNet(side, side, depth);
		// set weights as random nonzeros
		// TODO
		// run the training program
		c.train(0.1, training, targ, -1, rowIter, fileIter);
		// test her out
		c.setInputs(training, 0);
		System.out.println(c.getOutput()[0][0]);
		c.setInputs(training, 1);
		System.out.println(c.getOutput()[0][0]);
		c.setInputs(training, 2);
		System.out.println(c.getOutput()[0][0]);
		c.setInputs(training, 3);
		System.out.println(c.getOutput()[0][0]);
		System.exit(0);
	}

	public static boolean validateAUGt(String fileName) {
		Charset charset = Charset.forName("US-ASCII");
		Path file = Paths.get(fileName);
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			int lineNumber = 1;
			String[] lineSplit;
			int n = 0;
			while ((line = reader.readLine()) != null) {
				if (verbose) {
					System.out.println(line);
				}
				try {
					lineSplit = line.split(" ");
					switch (lineNumber) {
					case 1:
						assert (lineSplit[0].equals("grid"));
						String[] size = lineSplit[1].split(",");
						assert (Integer.valueOf(size[0]) > 0);
						assert (Integer.valueOf(size[1]) == Integer
								.valueOf(size[0]));
						n = Integer.valueOf(size[0]);
						assert (Integer.valueOf(size[2]) > 0);
						break;
					case 2:
						assert (lineSplit[0].equals("train"));
						size = lineSplit[1].split(",");
						assert (Integer.valueOf(size[0]) > 0);
						assert (Integer.valueOf(size[1]) > 0);
						break;
					case 3:
						assert (lineSplit[0].equals("TITLES"));
						size = lineSplit[1].split(",");
						assert (size.length == n * n);
						break;
					default:
						assert (Double.valueOf(lineSplit[0]) != null);
						size = lineSplit[1].split(",");
						assert (size.length == n * n);
						break;
					}
					lineNumber++;
				} catch (Exception e) {
					if (verbose)
						System.err.println("Validation failed at line: "
								+ lineNumber);
					return false;
				}
			}
		} catch (IOException x) {
			if (verbose)
				System.err.format("IOException: %s%n", x);
			return false;
		}
		return true;
	}

	public static void mainOR(String[] args) {
		// OR test
		CubeNet c = new CubeNet(2, 2, 3);

		for (int k = 0; k < 3; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					// if k == 0, connect to inputs
					if (k == 0) {
						c.neurons[i][j][k].addInput(c.inputs[i][j], 1);
					} else {
						// else, connect each neuron in
						// a layer to each neuron in the
						// previous layer.
						for (int ii = 0; ii < 2; ii++) {
							for (int jj = 0; jj < 2; jj++) {
								c.neurons[i][j][k].addInput(
										c.neurons[ii][jj][k - 1], 0);
							}
						}
					}
				}
			}
		}

		// set initial weights as nonzero for connections
		c.setWeight(0, 0, 0, 0, 0, 1, 0.2);
		c.setWeight(0, 0, 0, 1, 1, 1, 0.4);
		c.setWeight(1, 1, 0, 0, 0, 1, -0.3);
		c.setWeight(1, 1, 0, 1, 1, 1, 0.8);
		c.setWeight(0, 0, 1, 0, 0, 2, -0.4);
		c.setWeight(1, 1, 1, 0, 0, 2, 0.6);

		// 00
		double[][][] samples00 = new double[2][2][1];
		samples00[0][0][0] = 0;
		samples00[0][1][0] = 0;
		samples00[1][0][0] = 0;
		samples00[1][1][0] = 0;

		double[][][] des00 = new double[2][2][1];
		des00[0][0][0] = 0;
		des00[0][1][0] = 0.5;
		des00[1][0][0] = 0.5;
		des00[1][1][0] = 0.5;

		// 01
		double[][][] samples01 = new double[2][2][1];
		samples01[0][0][0] = 0;
		samples01[0][1][0] = 0;
		samples01[1][0][0] = 0;
		samples01[1][1][0] = 1;

		double[][][] des01 = new double[2][2][1];
		des01[0][0][0] = 1;
		des01[0][1][0] = 0.5;
		des01[1][0][0] = 0.5;
		des01[1][1][0] = 0.5;

		// 10
		double[][][] samples10 = new double[2][2][1];
		samples10[0][0][0] = 1;
		samples10[0][1][0] = 0;
		samples10[1][0][0] = 0;
		samples10[1][1][0] = 0;

		double[][][] des10 = new double[2][2][1];
		des10[0][0][0] = 1;
		des10[0][1][0] = 0.5;
		des10[1][0][0] = 0.5;
		des10[1][1][0] = 0.5;

		// 11
		double[][][] samples11 = new double[2][2][1];
		samples11[0][0][0] = 1;
		samples11[0][1][0] = 0;
		samples11[1][0][0] = 0;
		samples11[1][1][0] = 1;

		double[][][] des11 = new double[2][2][1];
		des11[0][0][0] = 1;
		des11[0][1][0] = 0.5;
		des11[1][0][0] = 0.5;
		des11[1][1][0] = 0.5;

		// Before training
		double[][] inp00 = new double[2][2];
		inp00[0][0] = 0;
		inp00[0][1] = 0;
		inp00[1][0] = 0;
		inp00[1][1] = 0;

		double[][] inp01 = new double[2][2];
		inp01[0][0] = 0;
		inp01[0][1] = 0;
		inp01[1][0] = 0;
		inp01[1][1] = 1;

		double[][] inp10 = new double[2][2];
		inp10[0][0] = 1;
		inp10[0][1] = 0;
		inp10[1][0] = 0;
		inp10[1][1] = 0;

		double[][] inp11 = new double[2][2];
		inp11[0][0] = 1;
		inp11[0][1] = 0;
		inp11[1][0] = 0;
		inp11[1][1] = 1;

		double output;
		System.out.println("Before training\n");

		c.setInputs(inp00);
		output = c.getOutput()[0][0];
		System.out.println("00: " + output + ", diff: "
				+ (output - des00[0][0][0]));

		c.setInputs(inp01);
		output = c.getOutput()[0][0];
		System.out.println("01: " + output + ", diff: "
				+ (output - des01[0][0][0]));

		c.setInputs(inp10);
		output = c.getOutput()[0][0];
		System.out.println("10: " + output + ", diff: "
				+ (output - des10[0][0][0]));

		c.setInputs(inp11);
		output = c.getOutput()[0][0];
		System.out.println("11: " + output + ", diff: "
				+ (output - des11[0][0][0]));

		double[][][] inputs = new double[4][4][4];
		double[][][] desireds = new double[4][4][4];
		for (int k = 0; k < 4; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					switch (k) {
					case 0:
						inputs[i][j][k] = samples00[i][j][0];
						desireds[i][j][k] = des00[i][j][0];
						break;
					case 1:
						inputs[i][j][k] = samples01[i][j][0];
						desireds[i][j][k] = des01[i][j][0];
						break;
					case 2:
						inputs[i][j][k] = samples10[i][j][0];
						desireds[i][j][k] = des10[i][j][0];
						break;
					case 3:
						inputs[i][j][k] = samples11[i][j][0];
						desireds[i][j][k] = des11[i][j][0];
						break;
					}
				}
			}
		}

		// Training
		int trainingRounds = 100;
		int intermediateRounds = 100;
		c.train(0.1, inputs, desireds, -1, intermediateRounds, trainingRounds);

		System.out.println("\nAfter training\n");

		c.setInputs(inp00);
		output = c.getOutput()[0][0];
		System.out.println("00: " + output + ", diff: "
				+ (output - des00[0][0][0]));

		c.setInputs(inp01);
		output = c.getOutput()[0][0];
		System.out.println("01: " + output + ", diff: "
				+ (output - des01[0][0][0]));

		c.setInputs(inp10);
		output = c.getOutput()[0][0];
		System.out.println("10: " + output + ", diff: "
				+ (output - des10[0][0][0]));

		c.setInputs(inp11);
		output = c.getOutput()[0][0];
		System.out.println("11: " + output + ", diff: "
				+ (output - des11[0][0][0]));
	}

	public static void main2(String[] args) {
		CubeNet c = new CubeNet(2, 2, 3);
		// since the cubenet is 2 x 2 x 3
		// the network we are interested in is setup like this:
		// INPUT A ----- A
		// \ / \
		// / \ C --- OUTPUT
		// / \ /
		// INPUT B ----- B
		// names go from left to right
		//
		// initial weights are:
		// AA: 0.1
		// AB: 0.4
		// BA: 0.8
		// BB: 0.6
		// AC: 0.3
		// BC: 0.9
		//
		// initial conditions are:
		// A = 0.35
		// B = 0.9
		//
		// output should be 0.69
		//
		// We are starting with a cube, therefore:
		// Input A == 000
		// Input B == 110
		// Neuron A == 001
		// Neuron B == 111
		// Neuron C == 002
		//
		// All other neurons are zeroed out
		double[][] d = { { 0.35, 0 }, { 0, 0.9 } };
		c.setInputs(d);

		for (int x = 0; x < 1; x++) {
			for (int y = 0; y < 1; y++) {
				for (int z = 0; z < 2; z++) {
					c.setWeight(x, y, z, x + 1, y + 1, z + 1, 0);
				}
			}
		}

		// AA: 0.1
		c.setWeight(0, 0, 0, 0, 0, 1, 0.2);
		// AB: 0.4
		c.setWeight(0, 0, 0, 1, 1, 1, 0.4);
		// BA: 0.8
		c.setWeight(1, 1, 0, 0, 0, 1, 0.2);
		// BB: 0.6
		c.setWeight(1, 1, 0, 1, 1, 1, 0.8);
		// AC: 0.3
		c.setWeight(0, 0, 1, 0, 0, 2, 0.4);
		// BC: 0.9
		c.setWeight(1, 1, 1, 0, 0, 2, 0.6);

		System.out.println("Original weights: ");
		System.out.println("AA: " + c.getWeight(0, 0, 0, 0, 0, 1));
		System.out.println("AB: " + c.getWeight(0, 0, 0, 1, 1, 1));
		System.out.println("BA: " + c.getWeight(1, 1, 0, 0, 0, 1));
		System.out.println("BB: " + c.getWeight(1, 1, 0, 1, 1, 1));
		System.out.println("AC: " + c.getWeight(0, 0, 1, 0, 0, 2));
		System.out.println("BC: " + c.getWeight(1, 1, 1, 0, 0, 2));

		double[][][] samples = new double[2][2][1];
		samples[0][0][0] = 0.35;
		samples[0][1][0] = 0;
		samples[1][0][0] = 0;
		samples[1][1][0] = 0.9;

		double[][][] desO = new double[2][2][1];
		desO[0][0][0] = 0.6;
		desO[0][1][0] = 0.5;
		desO[1][0][0] = 0.5;
		desO[1][1][0] = 0.5;

		double output = c.getOutput()[0][0];
		System.out.println("\nOriginal " + "Output: " + output + ", diff: "
				+ (output - desO[0][0][0]));

		double mindiff = Double.MAX_VALUE;

		int trainingRounds = 1000;
		for (int i = 0; i < trainingRounds; i++) {
			// c.train(0.1, samples, desO, -1);
			output = c.getOutput()[0][0];
			double diff = (output - desO[0][0][0]);
			if (diff < mindiff) {
				mindiff = diff;
			} else {
				System.out.println(i);
				break;
			}
			// System.out.println("Round " + i + ", Output: " + output
			// + ", diff: " + (output - desO[0][0][0]));
		}
		System.out.println("\nWeights after trainings: ");
		System.out.println("AA: " + c.getWeight(0, 0, 0, 0, 0, 1));
		System.out.println("AB: " + c.getWeight(0, 0, 0, 1, 1, 1));
		System.out.println("BA: " + c.getWeight(1, 1, 0, 0, 0, 1));
		System.out.println("BB: " + c.getWeight(1, 1, 0, 1, 1, 1));
		System.out.println("AC: " + c.getWeight(0, 0, 1, 0, 0, 2));
		System.out.println("BC: " + c.getWeight(1, 1, 1, 0, 0, 2));

		output = c.getOutput()[0][0];
		System.out.println("\nFinal Output: " + output + ", diff: "
				+ (output - desO[0][0][0]));
	}

	public static void main1(String[] args) {

		// Let's instantiate a cubenet.
		CubeNet c = new CubeNet(2, 2, 12);

		// now let's make some training data for AND
		double rate = 0.5;
		double perfLimit = -1;
		double[][][] trainingData = new double[2][2][12];
		// 0,1 for k=0..2
		for (int k = 0; k < 3; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if ((i + j) % 2 == 0) {
						trainingData[i][j][k] = 0 + Math.random() * 0.2 - 0.1;
					} else {
						trainingData[i][j][k] = 1 + Math.random() * 0.2 - 0.1;
					}
				}
			}
		}
		// 0,0 for k=3..5
		for (int k = 3; k < 6; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if ((i + j) % 2 == 0) {
						trainingData[i][j][k] = 0 + Math.random() * 0.2 - 0.1;
					} else {
						trainingData[i][j][k] = 0 + Math.random() * 0.2 - 0.1;
					}
				}
			}
		}
		// 1,0 for k=6..8
		for (int k = 6; k < 9; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if ((i + j) % 2 == 0) {
						trainingData[i][j][k] = 1 + Math.random() * 0.2 - 0.1;
					} else {
						trainingData[i][j][k] = 0 + Math.random() * 0.2 - 0.1;
					}
				}
			}
		}
		// 1,1 for k=9..11
		for (int k = 9; k < 12; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if ((i + j) % 2 == 0) {
						trainingData[i][j][k] = 1 + Math.random() * 0.2 - 0.1;
					} else {
						trainingData[i][j][k] = 1 + Math.random() * 0.2 - 0.1;
					}
				}
			}
		}

		double[][][] expected = new double[2][2][12];
		for (int k = 0; k < 9; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					expected[i][j][k] = 0;
				}
			}
		}
		for (int k = 9; k < 12; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					expected[i][j][k] = 1;
				}
			}
		}

		// c.train(rate, trainingData, expected, perfLimit);
		System.out.println("Trained:");

		// and some test data
		// 1,1
		double[][] test = new double[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i + j) % 2 == 0) {
					test[i][j] = 1 + Math.random() * 0.2 - 0.1;
				} else {
					test[i][j] = 1 + Math.random() * 0.2 - 0.1;
				}
			}
		}
		double e = 1;

		c.setInputs(test);
		System.out.println("Expected: " + e + ", Actual: ");
		c.printOutput();

		// and some test data
		// 0,1
		test = new double[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i + j) % 2 == 0) {
					test[i][j] = 0 + Math.random() * 0.2 - 0.1;
				} else {
					test[i][j] = 1 + Math.random() * 0.2 - 0.1;
				}
			}
		}
		e = 0;

		c.setInputs(test);
		System.out.println("Expected: " + e + ", Actual: ");
		c.printOutput();

		// and some test data
		// 1,0
		test = new double[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i + j) % 2 == 0) {
					test[i][j] = 1 + Math.random() * 0.2 - 0.1;
				} else {
					test[i][j] = 0 + Math.random() * 0.2 - 0.1;
				}
			}
		}
		e = 0;

		c.setInputs(test);
		System.out.println("Expected: " + e + ", Actual: ");
		c.printOutput();

		// and some test data
		// 0,0
		test = new double[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i + j) % 2 == 0) {
					test[i][j] = 0 + Math.random() * 0.2 - 0.1;
				} else {
					test[i][j] = 0 + Math.random() * 0.2 - 0.1;
				}
			}
		}
		e = 0;

		c.setInputs(test);
		System.out.println("Expected: " + e + ", Actual: ");
		c.printOutput();

	}
}
