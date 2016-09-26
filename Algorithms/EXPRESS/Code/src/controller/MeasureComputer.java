package controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashSet;
import java.util.Random;

import org.apache.lucene.util.OpenBitSet;

import model.Graph;
import model.Vertex;
import utils.UtilsFunctions;

public class MeasureComputer {
	private Graph graph;
	private double[][] totalDescriptorsValues;
	private double[] descriptorsTotalSum;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating
	// whether the vertex with the corresponding index has positive value for
	// this characteristic
	private OpenBitSet[][] positiveCharacteristics;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating
	// whether the vertex with the corresponding index has negative value for
	// this characteristic
	private OpenBitSet[][] negativeCharacteristics;

	private double[][] weighedDistribution;
	private double[][] frequencyDistribution;
	private double[][] maxMeasuresOfVertices;
	private Random random;

	public MeasureComputer(Graph graph) {
		random = new Random();
		this.graph = graph;
		positiveCharacteristics = new OpenBitSet[graph.getDescriptorsMetaData().length][];
		negativeCharacteristics = new OpenBitSet[graph.getDescriptorsMetaData().length][];
		for (int i = 0; i < graph.getDescriptorsMetaData().length; i++) {
			positiveCharacteristics[i] = new OpenBitSet[graph.getDescriptorsMetaData()[i].getAttributesName().length];
			negativeCharacteristics[i] = new OpenBitSet[graph.getDescriptorsMetaData()[i].getAttributesName().length];
			for (int j = 0; j < graph.getDescriptorsMetaData()[i].getAttributesName().length; j++) {
				positiveCharacteristics[i][j] = new OpenBitSet(graph.getVertices().length);
				negativeCharacteristics[i][j] = new OpenBitSet(graph.getVertices().length);
			}
		}
	}

	public OpenBitSet[][] getPositiveCharacteristics() {
		return positiveCharacteristics;
	}

	public OpenBitSet[][] getNegativeCharacteristics() {
		return negativeCharacteristics;
	}

	public Graph getGraph() {
		return graph;
	}

	public Random getRandom() {
		return random;
	}

	public void processVertexMeasures(boolean activateSMinus) {
		totalDescriptorsValues = new double[graph.getDescriptorsMetaData().length][];
		descriptorsTotalSum = new double[graph.getDescriptorsMetaData().length];
		for (int descriptorIndex = 0; descriptorIndex < graph.getDescriptorsMetaData().length; descriptorIndex++) {
			totalDescriptorsValues[descriptorIndex] = new double[graph.getDescriptorsMetaData()[descriptorIndex]
					.getAttributesName().length];
			double descriptorTotalSum = 0;
			for (int attIndex = 0; attIndex < graph.getDescriptorsMetaData()[descriptorIndex]
					.getAttributesName().length; attIndex++) {
				double attributeTotalSum = 0;
				for (Vertex v : graph.getVertices()) {
					attributeTotalSum += v.getDescriptorValue(descriptorIndex, attIndex);
				}
				totalDescriptorsValues[descriptorIndex][attIndex] = attributeTotalSum;
				descriptorTotalSum += attributeTotalSum;
			}
			descriptorsTotalSum[descriptorIndex] = descriptorTotalSum;
		}
		// process measure for each vertex
		for (int i = 0; i < graph.getVertices().length; i++) {
			Vertex v = graph.getVertices()[i];
			double[][] scores = new double[graph.getDescriptorsMetaData().length][];
			for (int descriptorIndex = 0; descriptorIndex < graph.getDescriptorsMetaData().length; descriptorIndex++) {
				scores[descriptorIndex] = new double[graph.getDescriptorsMetaData()[descriptorIndex]
						.getAttributesName().length];
				for (int attIndex = 0; attIndex < graph.getDescriptorsMetaData()[descriptorIndex]
						.getAttributesName().length; attIndex++) {
					if (v.getDescriptorTotal(descriptorIndex) == 0) {
						scores[descriptorIndex][attIndex] = 0;
					} else {
						scores[descriptorIndex][attIndex] = ((v.getDescriptorValue(descriptorIndex, attIndex)
								/ v.getDescriptorTotal(descriptorIndex))
								- ((totalDescriptorsValues[descriptorIndex][attIndex])
										/ (descriptorsTotalSum[descriptorIndex])))
								* UtilsFunctions.fraction(1, (double) graph.getVertices().length);
					}
					if (scores[descriptorIndex][attIndex] > 0) {
						positiveCharacteristics[descriptorIndex][attIndex].fastSet(i);
					} else {
						positiveCharacteristics[descriptorIndex][attIndex].fastClear(i);
					}
					if (scores[descriptorIndex][attIndex] < 0) {
						negativeCharacteristics[descriptorIndex][attIndex].fastSet(i);
					} else {
						negativeCharacteristics[descriptorIndex][attIndex].fastClear(i);
					}
				}
			}
			v.setDescriptorsScores(scores);
		}
		createWeighedDistribution(activateSMinus);
	}

	public void createWeighedDistribution(boolean activateSMinus) {
		weighedDistribution = new double[graph.getDescriptorsMetaData().length][];
		frequencyDistribution = new double[graph.getDescriptorsMetaData().length][];
		maxMeasuresOfVertices = new double[graph.getDescriptorsMetaData().length][];
		for (int i = 0; i < graph.getDescriptorsMetaData().length; i++) {
			weighedDistribution[i] = new double[graph.getVertices().length];
			maxMeasuresOfVertices[i] = new double[graph.getVertices().length];
			frequencyDistribution[i] = new double[graph.getVertices().length];
			BigDecimal[] weights = new BigDecimal[graph.getVertices().length];
			BigDecimal[] freqWeights = new BigDecimal[graph.getVertices().length];
			BigDecimal sumWeights = BigDecimal.ZERO;
			BigDecimal sumFreqWeights = BigDecimal.ZERO;
			for (int j = 0; j < graph.getVertices().length; j++) {
				int power = -1;
				for (int k = 0; k < graph.getDescriptorsMetaData()[i].getAttributesName().length; k++) {
					if (positiveCharacteristics[i][k].get(j)) {
						power++;
					}
					if (activateSMinus && negativeCharacteristics[i][k].get(j)) {
						power++;
					}
				}
				BigDecimal weight = getPower(2, power);
				double maxM = 0;
				for (int k = 0; k < graph.getDescriptorsMetaData()[i].getAttributesName().length; k++) {
					if (graph.getVertices()[j].getAttributeDescriptorScore(i, k) > 0) {
						maxM += graph.getVertices()[j].getAttributeDescriptorScore(i, k);
					} else if (activateSMinus) {
						maxM = maxM - graph.getVertices()[j].getAttributeDescriptorScore(i, k);
					}
				}
				maxMeasuresOfVertices[i][j] = maxM;
				weights[j] = weight.multiply(new BigDecimal(Double.toString(maxM)));
				freqWeights[j] = weight.multiply(new BigDecimal("2"));
				sumFreqWeights = sumFreqWeights.add(freqWeights[j]);
				sumWeights = sumWeights.add(weights[j]);
			}
			double sumWeightNormalized = 0;
			double sumFreqNormalized = 0;
			for (int j = 0; j < graph.getVertices().length; j++) {
				sumWeightNormalized += weights[j].divide(sumWeights, MathContext.DECIMAL128).doubleValue();
				sumFreqNormalized += freqWeights[j].divide(sumFreqWeights, MathContext.DECIMAL128).doubleValue();
				weighedDistribution[i][j] = sumWeightNormalized;
				frequencyDistribution[i][j] = sumFreqNormalized;
			}
		}
	}

	public int randomGenerateVertex(int descriptorIndex) {
		double randDouble = random.nextDouble();
		int i = 0;
		while (i < weighedDistribution[descriptorIndex].length
				&& weighedDistribution[descriptorIndex][i] < randDouble) {
			i++;
		}
		return i;
	}

	public int randomGenerateVertexByFreqDistribution(int descriptorIndex) {
		double randDouble = random.nextDouble();
		int i = 0;
		while (i < frequencyDistribution[descriptorIndex].length
				&& frequencyDistribution[descriptorIndex][i] < randDouble) {
			i++;
		}
		return i;
	}

	public int randomGenerateVertexUniformly() {
		double randDouble = random.nextDouble();
		return ((int) Math.floor(randDouble * ((double) graph.getVertices().length)));
	}

	public static BigDecimal getPower(int a, int b) {
		BigDecimal power = new BigDecimal(Integer.toString(1));
		if (b < 0) {
			return BigDecimal.ZERO;
		}
		for (int i = 0; i < b; i++) {
			power = power.multiply(new BigDecimal(Integer.toString(a)));
		}
		return power;
	}

}
