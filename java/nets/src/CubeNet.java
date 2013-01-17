public class CubeNet {
	private Input[][] inputs;
	private Neuron[][][] neurons;
	private int x;
	private int y;
	private int z;

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
										this.neurons[ii][jj][k - 1], 0);
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
			double[][][] desiredOutput, double perfLimit) {
		boolean verbose = false;
		assert (samples.length == x);
		assert (samples[0].length == y);
		assert (samples[0][0].length == desiredOutput.length);
		int numSamples = samples[0][0].length;
		// 1) pick a rate parameter r
		// 2) until perf is satisfactory
		int kill = 0;
		int lcv = 0;
		double min = Double.NEGATIVE_INFINITY;
		while (true) {
			kill++;
			if (kill > 1000) {
				kill = 0;
				lcv++;
				init();
			}
			double[][] actual;
			double[][] desired = new double[x][y];
			double[][] differenceSquared = new double[x][y];
			double[][][] weightDeltas = new double[x][y][z];
			// for each sample input
			for (int sampleNumber = 0; sampleNumber < numSamples; sampleNumber++) {
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
									+ desired[i][j] + ", A: " + actual[i][j]
									+ "   ");
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
							double oj = neurons[i][j][k + 1].getLastOutput();
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
							System.out.println("(" + ii + "," + jj + "," + k
									+ ") " + weightDeltas[ii][jj][k - 1]);
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
			break;
		}
	}

	public static void main(String[] args) {
		CubeNet c = new CubeNet(2, 2, 3);
		// since the cubenet is 2 x 2 x 3
		// the network we are interested in is setup like this:
		// INPUT A ----- A
		// 		   \ / \
		// 		   / \ C --- OUTPUT
		// 		  / \ /
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
			c.train(0.1, samples, desO, -1);
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

		c.train(rate, trainingData, expected, perfLimit);
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
