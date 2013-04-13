package alfred;

public class WeightDelta {
	private double[] outputDeltas;
	// Indexed by innerDeltas[rightCol][rightRow][leftRow]
	private double[][][] innerDeltas;

	public WeightDelta(double[] od, double[][][] id) {
		this.outputDeltas = od;
		this.innerDeltas = id;
	}

	public WeightDelta(int depth, int numInputs) {
		this.outputDeltas = new double[numInputs];
		this.innerDeltas = new double[depth][numInputs][numInputs];
	}

	public double[] getOutputDeltas() {
		return this.outputDeltas;
	}

	public double[][][] getInnerDeltas() {
		return this.innerDeltas;
	}

	public void changeOutputDelta(double dw, int loc) {
		this.outputDeltas[loc] += dw;
	}

	public void changeInnerDelta(double dw, int rc, int rr, int lr) {
		this.innerDeltas[rc][rr][lr] += dw;
	}
	
	public double getOutputDelta(int loc) {
		return this.outputDeltas[loc];
	}

	public double getInnerDelta(int rc, int rr, int lr) {
		return this.innerDeltas[rc][rr][lr];
	}
}
