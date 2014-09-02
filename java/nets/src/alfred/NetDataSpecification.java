package alfred;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

public class NetDataSpecification {

    private final List<String> dates;
    private final List<BigDecimal> targets;
    private final List<BigDecimal[]> inputSets;
    private final ScaleFunction scaleFunction;

    public NetDataSpecification(List<String> dates, List<BigDecimal> targets,
            List<BigDecimal[]> inputSets, ScaleFunction scaleFunction) {
        this.dates = dates;
        this.targets = targets;
        this.inputSets = inputSets;
        this.scaleFunction = scaleFunction;
    }

    public static class Builder {

        private List<String> dates = Lists.newArrayList();
        private List<BigDecimal> targets = Lists.newArrayList();
        private List<BigDecimal[]> inputSets = Lists.newArrayList();
        private double desiredMin = 0.1;
        private double desiredMax = 0.9;

        public Builder addDataRow(String date, BigDecimal target, BigDecimal[] inputs) {
            dates.add(date);
            targets.add(target);
            inputSets.add(inputs);
            return this;
        }

        public Builder desiredMin(double desiredMin) {
            this.desiredMin = desiredMin;
            return this;
        }

        public Builder desiredMax(double desiredMax) {
            this.desiredMax = desiredMax;
            return this;
        }

        private double getMinTarget() {
            double minSeen = Double.POSITIVE_INFINITY;
            for (BigDecimal target : targets) {
                if (target.doubleValue() < minSeen) {
                    minSeen = target.doubleValue();
                }
            }
            return minSeen;
        }

        private double getMaxTarget() {
            double maxSeen = Double.NEGATIVE_INFINITY;
            for (BigDecimal target : targets) {
                if (target.doubleValue() > maxSeen) {
                    maxSeen = target.doubleValue();
                }
            }
            return maxSeen;
        }

        public void normalizeTargetValues(ScaleFunction scaleFunction) {
            for (int i = 0; i < targets.size(); i++) {
                BigDecimal target = targets.get(i);
                targets.set(i, BigDecimal.valueOf(scaleFunction.normalize(target.doubleValue())));
            }
        }

        /*
         * Normalizes inputs.
         */
        public NetDataSpecification build() {
            ScaleFunction scaleFunc = new ScaleFunction(getMinTarget(),
                                                        getMaxTarget(),
                                                        desiredMin,
                                                        desiredMax);
            normalizeTargetValues(scaleFunc);
            return new NetDataSpecification(dates, targets, inputSets, scaleFunc);
        }

    }

    public List<String> getDates() {
        return dates;
    }

    public List<BigDecimal> getTargets() {
        return targets;
    }

    public List<BigDecimal[]> getInputSets() {
        return inputSets;
    }

    public BigDecimal denormalize(BigDecimal value) {
        return BigDecimal.valueOf(scaleFunction.denormalize(value.doubleValue()));
    }

}
