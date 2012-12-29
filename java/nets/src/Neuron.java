import java.util.ArrayList;

public class Neuron implements Inp {
	private ArrayList<Double> weights;
	private ArrayList<Inp> inputs;
	private double offset;
	private double lastOutput;
	private double lastCode;

	public Neuron() {
		this.weights = new ArrayList<Double>();
		this.inputs = new ArrayList<Inp>();
		this.offset = 0;
	}

	/**
	 * Add a neuron to the inputs list with a given weight.
	 * If the neuron is already in the inputs list, change
	 * the weight.
	 * 
	 * @param n Neuron to add
	 * @param w Weight to add
	 */
	public void addInput(Inp n, double w) {
		if (!inputs.contains(n)) {
			inputs.add(n);
			weights.add(w);
		} else {
			int loc = inputs.indexOf(n);
			weights.set(loc, w);
		}
	}
	
	public void changeWeight(Inp n, double w) {
		if (!inputs.contains(n)) {
			inputs.add(n);
			weights.add(w);
		} else {
			int loc = inputs.indexOf(n);
			weights.set(loc, weights.get(loc) + w);
		}
	}
	
	/**
	 * Performs the sigmoid function on an input
	 * y = 1 / (1 + exp(-x))
	 * Used internally in getOutput method.
	 * 
	 * @param input X
	 * @return sigmoid(x)
	 */
	private double sigmoid(double input) {
		return 1.0 / (1.0 + Math.exp(-1.0*input));
	}
	
	/**
	 * Gets the output of this neuron, which implicitly calculates the outputs of
	 * all the neurons below.
	 * 
	 * @return the output from this neuron.
	 */
	public double getOutput(double code) {
		if (code == lastCode) {
			return lastOutput;
		}
		lastCode = code;
		double sum = 0;
		for (int i = 0; i < inputs.size(); i++) {
			sum += this.weights.get(i) * this.inputs.get(i).getOutput(code);
		}
		sum += offset;
		sum = sigmoid(sum);
		this.lastOutput = sum;
		return sum;
	}
	
	public double getLastOutput() {
		return this.lastOutput;
	}
	
	public double getWeight(Neuron n) {
		if (!inputs.contains(n)) {
			throw new RuntimeException("does not contain this neuron");
		} else {
			int loc = inputs.indexOf(n);
			return weights.get(loc).doubleValue();
		}
	}
}
