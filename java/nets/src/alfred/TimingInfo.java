package alfred;

public class TimingInfo {

    private final long startTime;
    private final long maxAllowedDuration;

    public TimingInfo(long startTime, long maxAllowedDuration) {
        this.startTime = startTime;
        this.maxAllowedDuration = maxAllowedDuration;
    }

    public static TimingInfo withDuration(long maxAllowedDuration) {
        return new TimingInfo(System.currentTimeMillis(), maxAllowedDuration);
    }

    public boolean hasTimeExpired() {
        return System.currentTimeMillis() > (startTime + maxAllowedDuration);
    }

    public long getStartTime() {
        return startTime;
    }

}
