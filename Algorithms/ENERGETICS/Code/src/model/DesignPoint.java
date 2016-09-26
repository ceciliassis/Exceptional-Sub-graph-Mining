package model;

public class DesignPoint {
	public static final double THRESHOLD_DEFAULT_VALUE = 0.005;
	public static final double MIN_COVERING_DEFAULT_VALUE = 0.8;
	public static final int MIN_SIZE_SUBGRAPH_DEFAULT_VALUE = 1;
	public static final boolean ACTIVATE_UB1_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_VARIATE_HYPERZONE_NAIVELY_DEFAULT_VALUE = false;
	public static final boolean ACTIVATE_CHARAC_FAIL_FIRST_PRINCIPLE_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_UB2_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_UB3_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_SIBLING_BASED_UB_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_COVERING_PRUNING_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_SMINUS_DEFAULT_VALUE = false;
	public static final boolean ACTIVATE_SPLUS_DEFAULT_VALUE = true;
	public static final boolean ACTIVATE_UNIVERSAL_CLOSURE_DEFAULT_VALUE = false;
	public static final boolean PRUNE_WITH_CONNECTION_DEFAULT_VALUE = false;

	private double threshold = THRESHOLD_DEFAULT_VALUE;
	private double minCovering = MIN_COVERING_DEFAULT_VALUE;
	private int minSizeSubgraph = MIN_SIZE_SUBGRAPH_DEFAULT_VALUE;
	private boolean activateUB1 = ACTIVATE_UB1_DEFAULT_VALUE;
	private boolean activateVariateHyperzoneNaively = ACTIVATE_VARIATE_HYPERZONE_NAIVELY_DEFAULT_VALUE;
	private boolean activateCharacFailFirstPrinciple = ACTIVATE_CHARAC_FAIL_FIRST_PRINCIPLE_DEFAULT_VALUE;
	private boolean activateUB2 = ACTIVATE_UB2_DEFAULT_VALUE;
	private boolean activateUB3 = ACTIVATE_UB3_DEFAULT_VALUE;
	private boolean activateSiblingBasedUB = ACTIVATE_SIBLING_BASED_UB_DEFAULT_VALUE;
	private boolean activateCoveringPruning = ACTIVATE_COVERING_PRUNING_DEFAULT_VALUE;
	private boolean activateSMinus = ACTIVATE_SMINUS_DEFAULT_VALUE;
	private boolean activateSPlus = ACTIVATE_SPLUS_DEFAULT_VALUE;
	private boolean activateUniversalClosure = ACTIVATE_UNIVERSAL_CLOSURE_DEFAULT_VALUE;
	private boolean pruneWithConnection = PRUNE_WITH_CONNECTION_DEFAULT_VALUE;
	

	public DesignPoint() {

	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getMinCovering() {
		return minCovering;
	}

	public void setMinCovering(double minCovering) {
		this.minCovering = minCovering;
	}

	public int getMinSizeSubgraph() {
		return minSizeSubgraph;
	}

	public void setMinSizeSubgraph(int minSizeSubgraph) {
		this.minSizeSubgraph = minSizeSubgraph;
	}

	public boolean isActivateUB1() {
		return activateUB1;
	}

	public void setActivateUB1(boolean activateUB1) {
		this.activateUB1 = activateUB1;
	}

	public boolean isActivateVariateHyperzoneNaively() {
		return activateVariateHyperzoneNaively;
	}

	public void setActivateVariateHyperzoneNaively(boolean activateVariateHyperzoneNaively) {
		this.activateVariateHyperzoneNaively = activateVariateHyperzoneNaively;
	}

	public boolean isActivateCharacFailFirstPrinciple() {
		return activateCharacFailFirstPrinciple;
	}

	public void setActivateCharacFailFirstPrinciple(boolean activateCharacFailFirstPrinciple) {
		this.activateCharacFailFirstPrinciple = activateCharacFailFirstPrinciple;
	}

	public boolean isActivateUB2() {
		return activateUB2;
	}

	public void setActivateUB2(boolean activateUB2) {
		this.activateUB2 = activateUB2;
	}

	public boolean isActivateUB3() {
		return activateUB3;
	}

	public void setActivateUB3(boolean activateUB3) {
		this.activateUB3 = activateUB3;
	}

	public boolean isActivateSiblingBasedUB() {
		return activateSiblingBasedUB;
	}

	public void setActivateSiblingBasedUB(boolean activateSiblingBasedUB) {
		this.activateSiblingBasedUB = activateSiblingBasedUB;
	}

	public boolean isActivateCoveringPruning() {
		return activateCoveringPruning;
	}

	public void setActivateCoveringPruning(boolean activateCoveringPruning) {
		this.activateCoveringPruning = activateCoveringPruning;
	}

	public boolean isActivateSMinus() {
		return activateSMinus;
	}

	public void setActivateSMinus(boolean activateSMinus) {
		this.activateSMinus = activateSMinus;
	}

	public boolean isActivateSPlus() {
		return activateSPlus;
	}

	public void setActivateSPlus(boolean activateSPlus) {
		this.activateSPlus = activateSPlus;
	}

	public boolean isActivateUniversalClosure() {
		return activateUniversalClosure;
	}

	public void setActivateUniversalClosure(boolean activateUniversalClosure) {
		this.activateUniversalClosure = activateUniversalClosure;
	}

	public boolean isPruneWithConnection() {
		return pruneWithConnection;
	}

	public void setPruneWithConnection(boolean pruneWithConnection) {
		this.pruneWithConnection = pruneWithConnection;
	}

}
