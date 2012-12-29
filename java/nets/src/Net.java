import java.util.ArrayList;

public class Net {
	private ArrayList<Neuron> neurons;
	private Neuron outputNeuron;

	public Net() {
		this.neurons = new ArrayList<Neuron>();
		this.outputNeuron = null;
	}

	public Net(Neuron n) {
		this.neurons = new ArrayList<Neuron>();
		this.outputNeuron = n;
	}

	public double getOutput() {
		return 0;
	}
}
