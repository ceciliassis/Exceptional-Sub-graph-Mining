package model;

public class DesignPoint {
	private static final int NUMBER_OF_VERTICES_DEFAULT_VALUE = 1000;
	private static final int NUMBER_OF_EDGES_DEFAULT_VALUE = 5000;
	private static final int NUMBER_OF_ATTRIBUTES_DEFAULT_VALUE = 10;
	private static final int NUMBER_OF_PATTERNS_DEFAULT_VALUE = 3;
	private static final int PATTERN_VERTICES_SIZE_DEFAULT_VALUE = 10;
	private static final int PATTERN_ATTRIBUTES_HALF_SIZE_DEFAULT_VALUE = 1;
	private static final double MIN_ATT_VALUE_DEFAULT_VALUE = 50;
	private static final double MAX_ATT_VALUE_DEFAULT_VALUE = 150;
	private static final double PATTERN_CONTRAST_RATE_DEFAULT_VALUE = 0.4;

	private int numberOfVertices = NUMBER_OF_VERTICES_DEFAULT_VALUE;
	private int numberOfEdges = NUMBER_OF_EDGES_DEFAULT_VALUE;
	private int numberOfAttributes = NUMBER_OF_ATTRIBUTES_DEFAULT_VALUE;
	private int numberOfPatterns = NUMBER_OF_PATTERNS_DEFAULT_VALUE;
	private int patternVerticesSize = PATTERN_VERTICES_SIZE_DEFAULT_VALUE;
	private int patternAttributesHalfSize = PATTERN_ATTRIBUTES_HALF_SIZE_DEFAULT_VALUE;
	private double minAttValue = MIN_ATT_VALUE_DEFAULT_VALUE;
	private double maxAttValue = MAX_ATT_VALUE_DEFAULT_VALUE;
	private double patternContrastRate = PATTERN_CONTRAST_RATE_DEFAULT_VALUE;

	public DesignPoint() {

	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public void setNumberOfVertices(int numberOfVertices) {
		this.numberOfVertices = numberOfVertices;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	public void setNumberOfEdges(int numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	public int getNumberOfAttributes() {
		return numberOfAttributes;
	}

	public void setNumberOfAttributes(int numberOfAttributes) {
		this.numberOfAttributes = numberOfAttributes;
	}

	public int getNumberOfPatterns() {
		return numberOfPatterns;
	}

	public void setNumberOfPatterns(int numberOfPatterns) {
		this.numberOfPatterns = numberOfPatterns;
	}

	public int getPatternVerticesSize() {
		return patternVerticesSize;
	}

	public void setPatternVerticesSize(int patternVerticesSize) {
		this.patternVerticesSize = patternVerticesSize;
	}

	public int getPatternAttributesHalfSize() {
		return patternAttributesHalfSize;
	}

	public void setPatternAttributesHalfSize(int patternAttributesHalfSize) {
		this.patternAttributesHalfSize = patternAttributesHalfSize;
	}

	public double getMinAttValue() {
		return minAttValue;
	}

	public void setMinAttValue(double minAttValue) {
		this.minAttValue = minAttValue;
	}

	public double getMaxAttValue() {
		return maxAttValue;
	}

	public void setMaxAttValue(double maxAttValue) {
		this.maxAttValue = maxAttValue;
	}

	public double getPatternContrastRate() {
		return patternContrastRate;
	}

	public void setPatternContrastRate(double patternContrastRate) {
		this.patternContrastRate = patternContrastRate;
	}

}
