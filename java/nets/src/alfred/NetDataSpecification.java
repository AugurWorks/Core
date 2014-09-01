package alfred;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

public class NetDataSpecification {
	
	private final List<String> dates;
	private final List<BigDecimal> targets;
	private final List<BigDecimal[]> inputSets;
	
	public NetDataSpecification(List<String> dates, List<BigDecimal> targets,
			List<BigDecimal[]> inputSets) {
		this.dates = dates;
		this.targets = targets;
		this.inputSets = inputSets;
	}
	
	public static class Builder {
		
		private List<String> dates = Lists.newArrayList();
		private List<BigDecimal> targets = Lists.newArrayList();
		private List<BigDecimal[]> inputSets = Lists.newArrayList();
		
		public Builder addDataRow(String date, BigDecimal target, BigDecimal[] inputs) {
			dates.add(date);
			targets.add(target);
			inputSets.add(inputs);
			return this;
		}
		
		public NetDataSpecification build() {
			return new NetDataSpecification(dates, targets, inputSets);
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
	
}
