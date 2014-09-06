package alfred;

public class TrainingSummary {

    private final TrainingStopReason stopReason;
    private final int secondsElapsed;
    private final int roundsTrained;

    public TrainingSummary(TrainingStopReason stopReason,
                           int secondsElapsed,
                           int roundsTrained) {
        this.stopReason = stopReason;
        this.secondsElapsed = secondsElapsed;
        this.roundsTrained = roundsTrained;
    }

    public TrainingStopReason getStopReason() {
        return stopReason;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public int getRoundsTrained() {
        return roundsTrained;
    }

}
