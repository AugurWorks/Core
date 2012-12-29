public class CubeNet {
	private Input[][] inputs;
	private Neuron[][][] neurons;
	private int x;
	private int y;
	private int z;

	public CubeNet() {
		this.inputs = new Input[10][10];
		this.neurons = new Neuron[10][10][5];
		this.x = 10;
		this.y = 10;
		this.z = 5;
		init();
	}

	public CubeNet(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.inputs = new Input[x][y];
		this.neurons = new Neuron[x][y][z];
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
		// Set everything to null
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				this.inputs[i][j] = new Input();
				for (int k = 0; k < z; k++) {
					this.neurons[i][j][k] = new Neuron();
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

	public void train(double r, double samples[][][],
			double[][][] desiredOutput, double perfLimit) {
		assert (samples.length == x);
		assert (samples[0].length == y);
		assert (samples[0][0].length == desiredOutput.length);
		int numSamples = samples[0][0].length;
		// 1) pick a rate parameter r
		// 2) until perf is satisfactory
		while (true) {
			double[][] actual;
			double[][] desired = new double[x][y];
			double[][] differenceSquared = new double[x][y];
			double[][][] weightDeltas = new double[x][y][z];
			// for each sample input
			for (int sampleNumber = 0; sampleNumber < numSamples; sampleNumber++) {
				// System.out.println("Sample number: " + sampleNumber);
				double[][] samp = new double[x][y];
				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						samp[i][j] = samples[i][j][sampleNumber];
					}
				}
				// samp is now the sample input array
				// 3) compute the resulting output
				setInputs(samp);
				actual = getOutput();
				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						desired[i][j] = desiredOutput[i][j][sampleNumber];
					}
				}
				// System.out.println("Actual: " + actual + ", Desired: " +
				// desired);
				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						differenceSquared[i][j] += (desiredOutput[i][j][sampleNumber] - actual[i][j])
								* (desiredOutput[i][j][sampleNumber] - actual[i][j]);
					}
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
									System.out.println("b: ");
									System.out.print(b[i][j][k] + " ");
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
							System.out.println("dw: ");
							System.out.print(dw[i][j][k] + " ");
						}
					}
				}
				// put the dw into the weight deltas
				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						for (int k = 0; k < z; k++) {
							weightDeltas[i][j][k] += dw[i][j][k];
							System.out.println("wd: ");
							System.out.print(weightDeltas[i][j][k]);
						}
					}
				}
				System.out.println("Actual[0][0]: " + actual[0][0]
						+ ", Expected[0][0]: " + desired[0][0]);
			}
			// Check the break condition
			double ds = 0;
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					ds += differenceSquared[i][j];
				}
			}
			ds *= -1;
			System.out.println(ds);
			if (ds > perfLimit) {
				break;
			}
			// 7) add up the weight changes for all the sample inputs
			// and change the weights
			// new weight deltas are in weightDeltas
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					for (int k = 1; k < z - 1; k++) {
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

	public static void main(String[] args) {

		// Let's instantiate a cubenet.
		CubeNet c = new CubeNet(2, 2, 3);

		// now let's make some training data for AND
		double rate = 1;
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

		// and some test data
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
		System.out.println("Expected: " + e + ", Actual: " + c.getOutput());
	}
}
