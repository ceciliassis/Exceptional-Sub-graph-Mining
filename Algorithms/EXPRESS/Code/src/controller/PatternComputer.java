package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.util.OpenBitSet;

import model.AttributesType;
import model.DesignPoint;
import model.Graph;
import model.Pattern;
import model.SubGraph;
import model.Vertex;

public class PatternComputer {
	public static boolean continuExecution = true;
	private Graph graph;
	private MeasureComputer measureComputer;
	private ArrayList<Pattern> patterns;

	public int nbExploredHyperzones = 0;
	public int nbFoundPatterns;
	public int foundRepetitively = 0;
	private DesignPoint designPoint;

	public PatternComputer(MeasureComputer measureComputer,DesignPoint designPoint) {
		this.designPoint = designPoint;
		this.measureComputer = measureComputer;
		this.graph = measureComputer.getGraph();
		patterns = new ArrayList<>();
		measureComputer.processVertexMeasures(designPoint.isActivateSMinus());
	}

	public ArrayList<Pattern> computePatterns() {
		
		for (int i = 0; i < graph.getDescriptorsMetaData().length; i++) {
			if (!continuExecution) {
				break;
			}
			if (graph.getDescriptorsMetaData()[i].getAttributesType() == AttributesType.NOMINAL) {
				computePatternsOfOneDescriptor(i);
			}
		}
		System.out.println("nb explored hyperzones " + nbExploredHyperzones);
		nbFoundPatterns = patterns.size();

		return patterns;
	}

	public void computePatternsOfOneDescriptor(int descriptorIndex) {
		while (continuExecution) {
			Pattern newPattern = generatePattern(descriptorIndex);
			if (newPattern != null) {
				if (newPattern.getCharacteristic().getScore() >= designPoint.getThreshold()
						&& newPattern.getVertices().cardinality() >= designPoint.getMinSizeSubgraph()) {
					patterns.add(newPattern);
				}
			} else {
				System.out.print("pattern is null");
			}
			nbExploredHyperzones++;
		}
	}

	public Pattern generatePattern(int descriptorIndex) {
		int vertexIndex = 0;
		switch (designPoint.getGenerationType()) {
		case WeighedGeneration:
			vertexIndex = measureComputer.randomGenerateVertex(descriptorIndex);
			break;
		case FreqGeneration:
			vertexIndex = measureComputer.randomGenerateVertexByFreqDistribution(descriptorIndex);
			break;
		case UniformGeneration:
			vertexIndex = measureComputer.randomGenerateVertexUniformly();
			break;
		}

		SubGraph subgraph = new SubGraph(graph.getVertices()[vertexIndex], graph.getVertices().length, descriptorIndex,
				graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length);
		boolean continu = true;
		while (continu) {
			if (!continuExecution) {
				return null;
			}
			double[] candidateWeights = new double[(int) (subgraph.getNeighbors().cardinality() + 1)];
			double[] candidateMeasures = new double[(int) (subgraph.getNeighbors().cardinality() + 1)];
			candidateMeasures[0] = subgraph.getAScore() * ((double) subgraph.getSubgraphSize())
					/ ((double) graph.getVertices().length);
			double sumOfMeasures = candidateMeasures[0];
			double minimumMeasure = candidateMeasures[0];
			double maximumMeasure = candidateMeasures[0];
			int candIndex = 0;
			int i = 0;
			int neighborIndex = 0;
			boolean continu2 = true;
			while (continu2) {
				if (!continuExecution) {
					return null;
				}
				neighborIndex = subgraph.getNeighbors().nextSetBit(i);
				if (neighborIndex < 0) {
					continu2 = false;
				} else {
					candIndex++;
					candidateMeasures[candIndex] = 0;
					for (int j = 0; j < subgraph.getAttAScores().length; j++) {
						if (subgraph.getAttAScores()[j] > 0 && graph.getVertices()[neighborIndex]
								.getAttributeDescriptorScore(descriptorIndex, j) > 0) {
							candidateMeasures[candIndex] += (subgraph.getAttAScores()[j] * subgraph.getSumOfD()
									+ graph.getVertices()[neighborIndex].getAttributeDescriptorScore(descriptorIndex, j)
											* graph.getVertices()[neighborIndex].getDescriptorTotal(descriptorIndex)
											* ((double) graph.getVertices().length))
									/ (graph.getVertices()[neighborIndex].getDescriptorTotal(descriptorIndex)
											+ subgraph.getSumOfD());
						} else if (designPoint.isActivateSMinus() && subgraph.getAttAScores()[j] < 0
								&& graph.getVertices()[neighborIndex].getAttributeDescriptorScore(descriptorIndex,
										j) < 0) {
							candidateMeasures[candIndex] -= (subgraph.getAttAScores()[j] * subgraph.getSumOfD()
									+ graph.getVertices()[neighborIndex].getAttributeDescriptorScore(descriptorIndex, j)
											* graph.getVertices()[neighborIndex].getDescriptorTotal(descriptorIndex)
											* ((double) graph.getVertices().length))
									/ (graph.getVertices()[neighborIndex].getDescriptorTotal(descriptorIndex)
											+ subgraph.getSumOfD());
						}
					}
					candidateMeasures[candIndex] = candidateMeasures[candIndex]
							* ((double) (subgraph.getSubgraphSize() + 1)) / ((double) graph.getVertices().length);
					sumOfMeasures += candidateMeasures[candIndex];
					if (minimumMeasure > candidateMeasures[candIndex]) {
						minimumMeasure = candidateMeasures[candIndex];
					}
					if (maximumMeasure < candidateMeasures[candIndex]) {
						maximumMeasure = candidateMeasures[candIndex];
					}
					i = neighborIndex + 1;
				}
			}
			double sumOfWeights = 0;
			double sumOfDiffs = sumOfMeasures - minimumMeasure * ((double) candidateMeasures.length);
			for (i = 0; i < candidateMeasures.length; i++) {
				sumOfWeights += (candidateMeasures[i] - minimumMeasure) / sumOfDiffs;
				candidateWeights[i] = sumOfWeights;
			}
			// distribution table is done, the next step is chose value randomly
			// and see to which thing it corresponds
			double randDouble = measureComputer.getRandom().nextDouble();
			if (maximumMeasure > minimumMeasure) {
				if (randDouble <= candidateWeights[0]) {
					continu = false;
				} else {
					int currentNeighbor = subgraph.getNeighbors().nextSetBit(0);
					i = 1;
					while ((i < (candidateWeights.length - 1)) && randDouble > candidateWeights[i]) {
						i++;
						currentNeighbor = subgraph.getNeighbors().nextSetBit(currentNeighbor + 1);
					}
					subgraph.addNeighbor(graph.getVertices()[currentNeighbor]);
				}
			} else {
				int index = (int) Math.floor(randDouble * ((double) candidateWeights.length));
				if (index == candidateWeights.length) {
					index--;
				}
				if (index == 0) {
					continu = false;
				} else {
					int currentNeighbor = subgraph.getNeighbors().nextSetBit(0);
					for (i = 1; i < index; i++) {
						currentNeighbor = subgraph.getNeighbors().nextSetBit(currentNeighbor + 1);
					}
					subgraph.addNeighbor(graph.getVertices()[currentNeighbor]);
				}
			}
		}
		HashSet<Vertex> connectedSubgraph = new HashSet<>();
		int i = 0;
		int k = 0;
		continu = true;
		while (continu) {
			k = subgraph.getVertices().nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				connectedSubgraph.add(graph.getVertices()[k]);
				i = k + 1;
			}
		}
		HashSet<Integer> sPlus = new HashSet<>();
		HashSet<Integer> sMinus = new HashSet<>();
		for (i = 0; i < subgraph.getAttAScores().length; i++) {
			if (subgraph.getAttAScores()[i] > 0) {
				sPlus.add(i);
			}
			if (designPoint.isActivateSMinus() && subgraph.getAttAScores()[i] < 0) {
				sMinus.add(i);
			}
		}
		Pattern pattern = new Pattern(subgraph.getVertices(), connectedSubgraph,
				graph.getDescriptorsMetaData()[descriptorIndex], sPlus, sMinus,
				subgraph.getAScore() * ((double) subgraph.getSubgraphSize()) / ((double) graph.getVertices().length));
		return pattern;
	}

	public void removeRedundancies() {
		ArrayList<Pattern> summary = new ArrayList<>();
		for (Pattern pattern : patterns) {
			boolean continu = true;
			int i = 0;
			while (i < summary.size() && continu) {
				if (equalBitSet(pattern.getVertices(), summary.get(i).getVertices())) {
					continu = false;
				} else {
					i++;
				}
			}
			if (continu) {
				summary.add(pattern);
			}
		}
		patterns = summary;
		nbFoundPatterns = patterns.size();
		System.out.println("nbptot : " + nbFoundPatterns);
	}

	public boolean equalBitSet(OpenBitSet subgraphToAdd, OpenBitSet addedSubgraph) {
		OpenBitSet intersect = new OpenBitSet(graph.getVertices().length);
		intersect.union(subgraphToAdd);
		intersect.xor(addedSubgraph);
		return (intersect.cardinality() == 0);
	}

	public void writeResultInFile(String resultFilePath) {
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFilePath));
			writeResultAsJSon(resultFile);
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeResultAsJSon(BufferedWriter resultFile) throws IOException {
		resultFile.write("{\n");
		resultFile.write("\t\"numberOfPatterns\" :" + nbFoundPatterns + ",\n");
		resultFile.write("\t\"patterns\" : [\n");
		boolean firstInsert = true;
		for (Pattern pattern : patterns) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				resultFile.write(",\n");
			}
			resultFile.write(pattern.toJson("\t\t"));
		}
		resultFile.write("\n\t]\n}");
	}

}
