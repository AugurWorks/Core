package alfred;

import java.math.BigDecimal;

public class NetTrainSpecification {

	private final NetDataSpecification netData;
	private final BigDecimal cutoff;
	private final int depth;
	private final int side;
	private final int numberRowIterations;
	private final int numberFileIterations;
	private final BigDecimal learningConstant;
	private final int minTrainingRounds;
	private final BigDecimal performanceCutoff;

	public NetTrainSpecification(NetDataSpecification netData, BigDecimal cutoff, int depth, int side,
			int numberRowIterations, int numberFileIterations,
			BigDecimal learningConstant, int minTrainingRounds,
			BigDecimal performanceCutoff) {
		this.netData = netData;
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
		private NetDataSpecification.Builder dataBuilder = new NetDataSpecification.Builder();
		private BigDecimal cutoff;
		private int depth;
		private int numberRowIterations;
		private int numberFileIterations;
		private BigDecimal learningConstant;
		private int minTrainingRounds;
		private BigDecimal performanceCutoff;
		private int side;
		
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
		
		public Builder addInputAndTarget(BigDecimal[] inputs, BigDecimal target, String date) {
			this.dataBuilder.addDataRow(date, target, inputs);
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
			return new NetTrainSpecification(dataBuilder.build(), cutoff, depth, side, numberRowIterations, 
					numberFileIterations, learningConstant, minTrainingRounds, performanceCutoff);
		}
	}

	public NetDataSpecification getNetData() {
		return netData;
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
