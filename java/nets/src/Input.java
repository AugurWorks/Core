
public class Input implements Inp {
	private double value;

	public Input() {
		this.value = 0;
	}
	
	public Input(double v) {
		this.value = v;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public double getOutput(double code) {
		return this.value;
	}
	
	public void setValue(double v) {
		this.value = v;
	}
}
