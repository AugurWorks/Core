package alfred;

public class ScaleFunction {
    private final double min;
    private final double max;
    private final double desiredMin;
    private final double desiredMax;

    public ScaleFunction(double min, double max, double desiredMin, double desiredMax) {
        this.min = min;
        this.max = max;
        this.desiredMax = desiredMax;
        this.desiredMin = desiredMin;
    }

    public double normalize(double value) {
        return (value - min) / (max - min) * (desiredMax - desiredMin) + desiredMin;
    }

    public double denormalize(double value) {
        return (value - desiredMin) / (desiredMax - desiredMin) * (max - min) + min;
    }
}
