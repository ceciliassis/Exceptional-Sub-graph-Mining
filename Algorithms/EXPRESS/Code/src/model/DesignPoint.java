package model;

public class DesignPoint {
	public static final double THRESHOLD_DEFAULT_VALUE = 0.005;
	public static final int MIN_SIZE_SUBGRAPH_DEFAULT_VALUE = 1;
	public static final GenerationType GENERATION_TYPE_DEFAULT_VALUE = GenerationType.WeighedGeneration;
	public static final boolean ACTIVATE_SMINUS_DEFAULT_VALUE = false;
	public static final boolean REMOVE_REPETITION_DEFAULT_VALUE = false;
	public static final long EXECUTION_TIME_IN_MS_DEFAULT_VALUE = 200;

	private double threshold = THRESHOLD_DEFAULT_VALUE;
	private int minSizeSubgraph = MIN_SIZE_SUBGRAPH_DEFAULT_VALUE;
	private GenerationType generationType = GENERATION_TYPE_DEFAULT_VALUE;
	private boolean activateSMinus = ACTIVATE_SMINUS_DEFAULT_VALUE;
	private boolean removeRepetition = REMOVE_REPETITION_DEFAULT_VALUE;
	private long executionTimeInMS = EXECUTION_TIME_IN_MS_DEFAULT_VALUE;

	public DesignPoint() {

	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getMinSizeSubgraph() {
		return minSizeSubgraph;
	}

	public void setMinSizeSubgraph(int minSizeSubgraph) {
		this.minSizeSubgraph = minSizeSubgraph;
	}

	public GenerationType getGenerationType() {
		return generationType;
	}

	public void setGenerationType(GenerationType generationType) {
		this.generationType = generationType;
	}

	public boolean isActivateSMinus() {
		return activateSMinus;
	}

	public void setActivateSMinus(boolean activateSMinus) {
		this.activateSMinus = activateSMinus;
	}

	public boolean isRemoveRepetition() {
		return removeRepetition;
	}

	public void setRemoveRepetition(boolean removeRepetition) {
		this.removeRepetition = removeRepetition;
	}

	public long getExecutionTimeInMS() {
		return executionTimeInMS;
	}

	public void setExecutionTimeInMS(long executionTimeInMS) {
		this.executionTimeInMS = executionTimeInMS;
	}

}
