package alfred;
import java.util.ArrayList;

@Deprecated
public class Neuron implements Inp {
	private ArrayList<Double> weights;
	private ArrayList<Inp> inputs;
	private double offset;
	// Cached values of last output and code.
	private double lastOutput;
	private int lastCode;
	private String name;

	/**
	 * Instantiates a new neuron with empty lists of inputs, weights, and names.
	 */
	public Neuron() {
		this.weights = new ArrayList<Double>();
		this.inputs = new ArrayList<Inp>();
		this.offset = 0;
		this.name = "";
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
	 * Add a neuron to the inputs list with a given weight. If the neuron is
	 * already in the inputs list, set the weight to the given value.
	 * 
	 * @param n
	 *            Neuron to add
	 * @param w
	 *            Weight to set
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

	/**
	 * Changes the weight from this neuron to another, adding that neuron if
	 * necessary. The input w is a weight delta that is added to the original
	 * weight.
	 * 
	 * @param n
	 *            Input to change weight from this neuron to.
	 * @param w
	 *            Amount to change weight by.
	 */
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
		for (int i = 0; i < inputs.size(); i++) {
			sum += this.weights.get(i) * this.inputs.get(i).getOutput(code);
		}
		sum += offset;
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
	 * Returns the weight from this neuron to a different neuron, and throws an
	 * exception if that neuron is not connected to this.
	 * 
	 * @param n
	 *            Neuron to return weight to.
	 * @return weight to the given neuron
	 * @throws RuntimeException
	 *             when given neuron n is not connected to this.
	 */
	public double getWeight(Neuron n) {
		if (!inputs.contains(n)) {
			throw new RuntimeException("does not contain this neuron");
		} else {
			int loc = inputs.indexOf(n);
			return weights.get(loc).doubleValue();
		}
	}

	@Override
	public double getOutput() {
		// TODO Auto-generated method stub
		return 0;
	}
}
