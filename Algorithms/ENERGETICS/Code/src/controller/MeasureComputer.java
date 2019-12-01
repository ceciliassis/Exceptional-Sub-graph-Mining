package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.lucene.util.OpenBitSet;

import model.AttributeFrequency;
import model.Candidate;
import model.Graph;
import model.KUnionCand;
import model.ScoreComponents;
import model.SubGraphWithScore;
import model.Vertex;
import utils.UtilsFunctions;

public class MeasureComputer {
	private Graph graph;
	private double[][] totalDescriptorsValues;
	private double[] descriptorsTotalSum;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating wether the vertex with the corresponding index
	// has positive value for this characteristic
	private OpenBitSet[][] positiveCharacteristics;
	// for each characteristic in each distribution, this variable contains an
	// openBitSet indicating wether the vertex with the corresponding index
	// has negative value for this characteristic
	private OpenBitSet[][] negativeCharacteristics;
	// for each distribution, this table contains a table of characteristic
	// indices ranked by their occurence number positively
	private AttributeFrequency[][] positiveRanked;
	// for each distribution, this table contains a table of characteristic
	// indices ranked by their occurence number negatively
	private AttributeFrequency[][] negativeRanked;

	public MeasureComputer(Graph graph) {
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

	private AttributeFrequency[] processPositiveRankedAttributes(int descriptorIndex) {
		AttributeFrequency[] rankedAttributes = new AttributeFrequency[graph.getDescriptorsMetaData()[descriptorIndex]
				.getAttributesName().length];
		for (int i = 0; i < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length; i++) {
			rankedAttributes[i] = new AttributeFrequency(i, positiveCharacteristics[descriptorIndex][i].cardinality());
		}
		Arrays.sort(rankedAttributes);
		return rankedAttributes;
	}

	private AttributeFrequency[] processNegativeRankedAttributes(int descriptorIndex) {
		AttributeFrequency[] rankedAttributes = new AttributeFrequency[graph.getDescriptorsMetaData()[descriptorIndex]
				.getAttributesName().length];
		for (int i = 0; i < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length; i++) {
			rankedAttributes[i] = new AttributeFrequency(i, negativeCharacteristics[descriptorIndex][i].cardinality());
		}
		Arrays.sort(rankedAttributes);
		return rankedAttributes;
	}

	public AttributeFrequency[] getNegativeRankedAttributes(int descriptorIndex) {
		return negativeRanked[descriptorIndex];
	}

	public AttributeFrequency[] getPositiveRankedAttributes(int descriptorIndex) {
		return positiveRanked[descriptorIndex];
	}

	public Graph getGraph() {
		return graph;
	}

//	NOTE: o graph apos construção recebe as medidas calculadas
	public void processVertexMeasures() {
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
		positiveRanked = new AttributeFrequency[graph.getDescriptorsMetaData().length][];
		negativeRanked = new AttributeFrequency[graph.getDescriptorsMetaData().length][];
		for (int i = 0; i < graph.getDescriptorsMetaData().length; i++) {
			positiveRanked[i] = processPositiveRankedAttributes(i);
			negativeRanked[i] = processNegativeRankedAttributes(i);
		}
	}

	public OpenBitSet getVerticesWithIntersectCharacteristics(OpenBitSet currentBitSet, int descriptorIndex,
			int currentAttribute, int sign) {
		OpenBitSet myBitSet = new OpenBitSet(graph.getVertices().length);
		myBitSet.union(currentBitSet);
		if (sign > 0) {
			myBitSet.intersect(positiveCharacteristics[descriptorIndex][currentAttribute]);
		} else {
			myBitSet.intersect(negativeCharacteristics[descriptorIndex][currentAttribute]);
		}
		return myBitSet;
	}

	

	private double getSumOfAttributesForVertex(int descriptorIndex, HashSet<Integer> attributesIndices, Vertex vertex) {
		double sum = 0;
		for (int i : attributesIndices) {
			sum += vertex.getDescriptorValue(descriptorIndex, i);
		}
		return sum;

	}

	public Candidate getCandidateForVertex(Vertex vertexCandidate, int descriptorIndex,
			HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes) {
		double sumOfH = vertexCandidate.getDescriptorTotal(descriptorIndex);
		double splusH = getSumOfAttributesForVertex(descriptorIndex, positiveAttributes, vertexCandidate);
		double splusG = 0;
		for (int i : positiveAttributes) {
			splusG += totalDescriptorsValues[descriptorIndex][i];
		}
		double sminusH = getSumOfAttributesForVertex(descriptorIndex, negativeAttributes, vertexCandidate);
		double sminusG = 0;
		for (int i : negativeAttributes) {
			sminusG += totalDescriptorsValues[descriptorIndex][i];
		}

		double scorePlus;
		double scoreMinus;
		if (sumOfH == 0) {
			scorePlus = 0;
			scoreMinus = 0;
		} else {
			scorePlus = (splusH / sumOfH) - (splusG / descriptorsTotalSum[descriptorIndex]);

			scoreMinus = (sminusH / sumOfH) - (sminusG / descriptorsTotalSum[descriptorIndex]);
		}
		double aScore = scorePlus - scoreMinus;
		double measureValue = (aScore) * UtilsFunctions.fraction((double) 1, (double) graph.getVertices().length);
		int sizeOfHyperzone = 1;
		return new Candidate(vertexCandidate.getIndexInGraph(),
				new ScoreComponents(measureValue, sizeOfHyperzone, sumOfH, aScore));
	}

	public ArrayList<Candidate> getRankedCandidates(ArrayList<Vertex> currentHyperzone, int descriptorIndex,
			HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes) {
		ArrayList<Candidate> listOfCandidates = new ArrayList<>();
		for (Vertex vertex : currentHyperzone) {
			listOfCandidates
					.add(getCandidateForVertex(vertex, descriptorIndex, positiveAttributes, negativeAttributes));
		}
		Collections.sort(listOfCandidates, Collections.reverseOrder());
		return listOfCandidates;
	}

	public ArrayList<KUnionCand> getRankedKUnionCand(SubGraphWithScore currentK, ArrayList<Candidate> candidates) {
		ArrayList<KUnionCand> listOfKUnionCand = new ArrayList<>();
		for (Candidate cand : candidates) {
			listOfKUnionCand.add(new KUnionCand(currentK, cand, graph.getVertices().length));
		}
		Collections.sort(listOfKUnionCand, Collections.reverseOrder());
		return listOfKUnionCand;

	}

}
