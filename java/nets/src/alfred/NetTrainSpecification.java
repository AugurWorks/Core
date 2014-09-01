package alfred;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

public class NetTrainSpecification {

	private final List<BigDecimal> targets;
	private final List<BigDecimal[]> inputSets;
	private final BigDecimal cutoff;
	private final int depth;
	private final int side;
	private final int numberRowIterations;
	private final int numberFileIterations;
	private final BigDecimal learningConstant;
	private final int minTrainingRounds;
	private final BigDecimal performanceCutoff;

	public NetTrainSpecification(List<BigDecimal> targets,
			List<BigDecimal[]> inputSets, BigDecimal cutoff, int depth, int side,
			int numberRowIterations, int numberFileIterations,
			BigDecimal learningConstant, int minTrainingRounds,
			BigDecimal performanceCutoff) {
		this.targets = targets;
		this.inputSets = inputSets;
		this.cutoff = cutoff;
		this.depth = depth;
		this.side = side;
		this.numberRowIterations = numberRowIterations;
		this.numberFileIterations = numberFileIterations;
		this.learningConstant = learningConstant;
		this.minTrainingRounds = minTrainingRounds;
		this.performanceCutoff = performanceCutoff;
	}
	
	public static class Builder {
		private List<BigDecimal> targets = Lists.newArrayList();
		private List<BigDecimal[]> inputSets = Lists.newArrayList();
		private BigDecimal cutoff;
		private int depth;
		private int numberRowIterations;
		private int numberFileIterations;
		private BigDecimal learningConstant;
		private int minTrainingRounds;
		private BigDecimal performanceCutoff;
		private int side;
		
		public Builder targets(List<BigDecimal> targets) {
			this.targets = targets;
			return this;
		}
		
		public Builder inputSets(List<BigDecimal[]> inputSets) {
			this.inputSets = inputSets;
			return this;
		}
		
		public Builder cutoff(BigDecimal cutoff) {
			this.cutoff = cutoff;
			return this;
		}
		
		public Builder rowIterations(int numberRowIterations) {
			this.numberRowIterations = numberRowIterations;
			return this;
		}
		
		public Builder fileIterations(int numberFileIterations) {
			this.numberFileIterations = numberFileIterations;
			return this;
		}
		
		public Builder learningConstant(BigDecimal learningConstant) {
			this.learningConstant = learningConstant;
			return this;
		}
		
		public Builder minTrainingRounds(int minTrainingRounds) {
			this.minTrainingRounds = minTrainingRounds;
			return this;
		}
		
		public Builder performanceCutoff(BigDecimal performanceCutoff) {
			this.performanceCutoff = performanceCutoff;
			return this;
		}
		
		public Builder addInputAndTarget(BigDecimal[] inputs, BigDecimal target) {
			this.inputSets.add(inputs);
			this.targets.add(target);
			return this;
		}
		
		public Builder depth(int depth) {
			this.depth = depth;
			return this;
		}
		
		public Builder side(int side) {
			this.side = side;
			return this;
		}
		
		public int getSide() {
			return side;
		}
		
		public NetTrainSpecification build() {
			Validate.isTrue(inputSets.size() == targets.size());
			return new NetTrainSpecification(targets, inputSets, cutoff, depth, side, numberRowIterations, 
					numberFileIterations, learningConstant, minTrainingRounds, performanceCutoff);
		}
	}

	public List<BigDecimal> getTargets() {
		return targets;
	}

	public List<BigDecimal[]> getInputSets() {
		return inputSets;
	}

	public BigDecimal getCutoff() {
		return cutoff;
	}

	public int getDepth() {
		return depth;
	}

	public int getSide() {
		return side;
	}

	public int getNumberRowIterations() {
		return numberRowIterations;
	}

	public int getNumberFileIterations() {
		return numberFileIterations;
	}

	public BigDecimal getLearningConstant() {
		return learningConstant;
	}

	public int getMinTrainingRounds() {
		return minTrainingRounds;
	}

	public BigDecimal getPerformanceCutoff() {
		return performanceCutoff;
	}

}
