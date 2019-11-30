package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.apache.lucene.util.OpenBitSet;

import model.AttributesType;
import model.Candidate;
import model.DesignPoint;
import model.Graph;
import model.KUnionCand;
import model.Pattern;
import model.ReturnedValue;
import model.ScoreComponents;
import model.SolutionsOfCC;
import model.SubGraphWithScore;
import model.Vertex;
import utils.UtilsFunctions;

public class PatternComputer {
	private final Logger logger = Logger.getLogger("info");

	private Graph graph;
	private MeasureComputer measureComputer;
	private ArrayList<Pattern> patterns;
	public int nbExploredCharacteristics = 0;
	public int nbVerifiedCharacteristics = 0;
	public int nbExploredHyperzones = 0;
	public int nbFoundPatterns;

	private DesignPoint designPoint;
	private String resultFilePath = "retrievedPatternsFile.json";

	int concernedCCSize = 0;

	public PatternComputer(MeasureComputer measureComputer) {

		this.measureComputer = measureComputer;
		this.graph = measureComputer.getGraph();
	}

	public void setResultFilePath(String resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

	public ArrayList<Pattern> computePatterns(DesignPoint designPoint) {
		this.designPoint = designPoint;
		patterns = new ArrayList<>();
		measureComputer.processVertexMeasures();
		for (int i = 0; i < graph.getDescriptorsMetaData().length; i++) {
			if (graph.getDescriptorsMetaData()[i].getAttributesType() == AttributesType.NOMINAL) {
				computePatternsOfOneDescriptor(i);
			}
		}
		System.out.println("nb Verified Characteristiques " + nbVerifiedCharacteristics);
		System.out.println("nb explored caracteristiques " + nbExploredCharacteristics);
		System.out.println("nb explored hyperzones " + nbExploredHyperzones);
		nbFoundPatterns = patterns.size();

		System.out.println("nb found patterns : " + nbFoundPatterns);
		return patterns;
	}

	public void computePatternsOfOneDescriptor(int descriptorIndex) {
		HashSet<Integer> positiveAttributes = new HashSet<>();
		HashSet<Integer> negativeAttributes = new HashSet<>();
		OpenBitSet allVertices = new OpenBitSet(graph.getVertices().length);
		allVertices.set(0, graph.getVertices().length);
		if (designPoint.isActivateCharacFailFirstPrinciple()) {
			variateAttributesDirectly(descriptorIndex, positiveAttributes, negativeAttributes, 0, 0, allVertices);
		} else {
			variateAttributesWithFailFirst(descriptorIndex, positiveAttributes, negativeAttributes, 0, allVertices);
		}
	}

	private void variateAttributesWithFailFirst(int descriptorIndex, HashSet<Integer> positiveAttributes,
			HashSet<Integer> negativeAttributes, int i, OpenBitSet candidatesVertices) {
		if (i < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length) {
			if (designPoint.isActivateSPlus()) {
				OpenBitSet newCandidates1 = measureComputer.getVerticesWithIntersectCharacteristics(candidatesVertices,
						descriptorIndex, i, 1);
				positiveAttributes.add(i);
				nbVerifiedCharacteristics++;
				if (newCandidates1.cardinality() > 0) {
					int ii = 0;
					ArrayList<ArrayList<Vertex>> connectedSubgraphs = extractConnectedSubgraphs(newCandidates1);
					ii = 0;
					while (ii < connectedSubgraphs.size()) {
						if (connectedSubgraphs.get(ii).size() < designPoint.getMinSizeSubgraph()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates1.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else if (designPoint.isActivateUB1()
								&& getUB1(descriptorIndex, positiveAttributes, negativeAttributes,
										connectedSubgraphs.get(ii), i + 1, i, true) < designPoint.getThreshold()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates1.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else {
							ii++;
						}
					}

					variateAttributesWithFailFirst(descriptorIndex, positiveAttributes, negativeAttributes, i + 1,
							newCandidates1);

					if (designPoint.isActivateUniversalClosure()) {
						if (isClosedCharacteristic(descriptorIndex, newCandidates1, positiveAttributes,
								negativeAttributes)) {
							computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
									connectedSubgraphs);
						}
					} else {
						computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
								connectedSubgraphs);
					}
				}

				positiveAttributes.remove(i);
			}
			if (designPoint.isActivateSMinus()) {
				negativeAttributes.add(i);
				OpenBitSet newCandidates2 = measureComputer.getVerticesWithIntersectCharacteristics(candidatesVertices,
						descriptorIndex, i, -1);
				nbVerifiedCharacteristics++;
				if (newCandidates2.cardinality() > 0) {
					int ii;
					ArrayList<ArrayList<Vertex>> connectedSubgraphs = extractConnectedSubgraphs(newCandidates2);
					ii = 0;
					while (ii < connectedSubgraphs.size()) {
						if (connectedSubgraphs.get(ii).size() < designPoint.getMinSizeSubgraph()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates2.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else if (designPoint.isActivateUB1()
								&& getUB1(descriptorIndex, positiveAttributes, negativeAttributes,
										connectedSubgraphs.get(ii), i + 1, i + 1, true) < designPoint.getThreshold()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates2.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else {
							ii++;
						}
					}
					variateAttributesWithFailFirst(descriptorIndex, positiveAttributes, negativeAttributes, i + 1,
							newCandidates2);
					if (designPoint.isActivateUniversalClosure()) {
						if (isClosedCharacteristic(descriptorIndex, newCandidates2, positiveAttributes,
								negativeAttributes)) {
							computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
									connectedSubgraphs);
						}
					} else {
						computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
								connectedSubgraphs);
					}
				}
				negativeAttributes.remove(i);
			}
			variateAttributesWithFailFirst(descriptorIndex, positiveAttributes, negativeAttributes, i + 1,
					candidatesVertices);
		}
	}

	private void variateAttributesDirectly(int descriptorIndex, HashSet<Integer> positiveAttributes,
			HashSet<Integer> negativeAttributes, int i, int j, OpenBitSet candidatesVertices) {
		if (designPoint.isActivateSPlus()
				&& i < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length) {
			OpenBitSet newCandidates1 = measureComputer.getVerticesWithIntersectCharacteristics(candidatesVertices,
					descriptorIndex,
					measureComputer.getPositiveRankedAttributes(descriptorIndex)[i].getRealAttributeIndex(), 1);
			positiveAttributes
					.add(measureComputer.getPositiveRankedAttributes(descriptorIndex)[i].getRealAttributeIndex());
			nbVerifiedCharacteristics++;
			if (newCandidates1.cardinality() > 0) {
				int ii = 0;
				ArrayList<ArrayList<Vertex>> connectedSubgraphs = extractConnectedSubgraphs(newCandidates1);
				ii = 0;
				while (ii < connectedSubgraphs.size()) {
					if (connectedSubgraphs.get(ii).size() < designPoint.getMinSizeSubgraph()) {
						for (Vertex v : connectedSubgraphs.get(ii)) {
							newCandidates1.fastClear(v.getIndexInGraph());
						}
						connectedSubgraphs.remove(ii);
					} else if (designPoint.isActivateUB1()
							&& getUB1(descriptorIndex, positiveAttributes, negativeAttributes,
									connectedSubgraphs.get(ii), i + 1, j, true) < designPoint.getThreshold()) {
						for (Vertex v : connectedSubgraphs.get(ii)) {
							newCandidates1.fastClear(v.getIndexInGraph());
						}
						connectedSubgraphs.remove(ii);
					} else {
						ii++;
					}
				}
				variateAttributesDirectly(descriptorIndex, positiveAttributes, negativeAttributes, i + 1, j,
						newCandidates1);
				if (designPoint.isActivateUniversalClosure()) {
					if (isClosedCharacteristic(descriptorIndex, newCandidates1, positiveAttributes,
							negativeAttributes)) {
						computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
								connectedSubgraphs);
					}

				} else {
					computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes, connectedSubgraphs);
				}

			}
			positiveAttributes
					.remove(measureComputer.getPositiveRankedAttributes(descriptorIndex)[i].getRealAttributeIndex());
			variateAttributesDirectly(descriptorIndex, positiveAttributes, negativeAttributes, i + 1, j,
					candidatesVertices);
		} else if (designPoint.isActivateSMinus()
				&& j < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length) {
			boolean continu = true;
			while (j < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length && continu) {
				if (positiveAttributes.contains(
						measureComputer.getNegativeRankedAttributes(descriptorIndex)[j].getRealAttributeIndex())) {
					j++;
				} else {
					continu = false;
				}
			}
			if (!continu) {
				OpenBitSet newCandidates2 = measureComputer.getVerticesWithIntersectCharacteristics(candidatesVertices,
						descriptorIndex,
						measureComputer.getNegativeRankedAttributes(descriptorIndex)[j].getRealAttributeIndex(), -1);
				negativeAttributes
						.add(measureComputer.getNegativeRankedAttributes(descriptorIndex)[j].getRealAttributeIndex());
				nbVerifiedCharacteristics++;
				if (newCandidates2.cardinality() > 0) {
					int ii = 0;
					ArrayList<ArrayList<Vertex>> connectedSubgraphs = extractConnectedSubgraphs(newCandidates2);
					ii = 0;
					while (ii < connectedSubgraphs.size()) {
						if (connectedSubgraphs.get(ii).size() < designPoint.getMinSizeSubgraph()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates2.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else if (designPoint.isActivateUB1()
								&& getUB1(descriptorIndex, positiveAttributes, negativeAttributes,
										connectedSubgraphs.get(ii), i, j + 1, true) < designPoint.getThreshold()) {
							for (Vertex v : connectedSubgraphs.get(ii)) {
								newCandidates2.fastClear(v.getIndexInGraph());
							}
							connectedSubgraphs.remove(ii);
						} else {
							ii++;
						}
					}
					variateAttributesDirectly(descriptorIndex, positiveAttributes, negativeAttributes, i, j + 1,
							newCandidates2);
					if (designPoint.isActivateUniversalClosure()) {
						if (isClosedCharacteristic(descriptorIndex, newCandidates2, positiveAttributes,
								negativeAttributes)) {
							computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
									connectedSubgraphs);
						}
					} else {
						computeCharacteristic(descriptorIndex, positiveAttributes, negativeAttributes,
								connectedSubgraphs);

					}
				}
				negativeAttributes.remove(
						measureComputer.getNegativeRankedAttributes(descriptorIndex)[j].getRealAttributeIndex());
				variateAttributesDirectly(descriptorIndex, positiveAttributes, negativeAttributes, i, j + 1,
						candidatesVertices);
			}
		}
	}

	private double getUB1(int descriptorIndex, HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes,
			ArrayList<Vertex> vertices, int posIndex, int negIndex, boolean withBalancing) {
		double maxA = 0;
		int k = 0;
		for (Vertex vertex : vertices) {
			k = vertex.getIndexInGraph();
			double currentScore = 0;
			for (int j = 0; j < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length; j++) {
				if (!withBalancing) {
					if (graph.getVertices()[k].getAttributeDescriptorScore(descriptorIndex, j) >= 0) {
						if (designPoint.isActivateSPlus() && (j >= posIndex || positiveAttributes.contains(j))) {
							currentScore = currentScore
									+ graph.getVertices()[k].getAttributeDescriptorScore(descriptorIndex, j);
						}
					} else {
						if (designPoint.isActivateSMinus() && (j >= negIndex || negativeAttributes.contains(j))) {
							currentScore = currentScore
									- graph.getVertices()[k].getAttributeDescriptorScore(descriptorIndex, j);
						}
					}
				} else {
					int currentPositiveIndex = measureComputer.getPositiveRankedAttributes(descriptorIndex)[j]
							.getRealAttributeIndex();
					int currentNegativeIndex = measureComputer.getNegativeRankedAttributes(descriptorIndex)[j]
							.getRealAttributeIndex();
					if (designPoint.isActivateSPlus() && graph.getVertices()[k]
							.getAttributeDescriptorScore(descriptorIndex, currentPositiveIndex) >= 0) {
						if (j >= posIndex || positiveAttributes.contains(currentPositiveIndex)) {
							currentScore = currentScore + graph.getVertices()[k]
									.getAttributeDescriptorScore(descriptorIndex, currentPositiveIndex);
						}
					}
					if (designPoint.isActivateSMinus() && graph.getVertices()[k]
							.getAttributeDescriptorScore(descriptorIndex, currentNegativeIndex) < 0) {
						if (j >= negIndex || negativeAttributes.contains(currentNegativeIndex)) {

							currentScore = currentScore - graph.getVertices()[k]
									.getAttributeDescriptorScore(descriptorIndex, currentNegativeIndex);
						}
					}
				}
			}
			currentScore = currentScore * ((double) graph.getVertices().length);
			if (maxA < currentScore) {
				maxA = currentScore;
			}
		}

		return maxA * ((double) vertices.size()) / ((double) graph.getVertices().length);

	}

	private double getUB2(SubGraphWithScore k, int candidatesNumber) {
		return k.getScoreComponents().getAScore() * UtilsFunctions
				.fraction(k.getScoreComponents().getSizeOfHyperzone() + candidatesNumber, graph.getVertices().length);
	}

	private ArrayList<Vertex> computeCharacteristic(int descriptorIndex, HashSet<Integer> positiveAttributes,
			HashSet<Integer> negativeAttributes, ArrayList<ArrayList<Vertex>> connectedSubgraphs) {
		nbExploredCharacteristics++;
		for (ArrayList<Vertex> connectedSubgraph : connectedSubgraphs) {
			if (designPoint.isActivateVariateHyperzoneNaively()) {
				variateHyperzoneNaively(descriptorIndex, positiveAttributes, negativeAttributes, connectedSubgraph);
			} else {
				variateHyperzone(descriptorIndex, positiveAttributes, negativeAttributes, connectedSubgraph);
			}
		}
		return null;
	}

	private ArrayList<ArrayList<Vertex>> extractConnectedSubgraphs(OpenBitSet candidates) {
		OpenBitSet rest = new OpenBitSet(graph.getVertices().length);
		rest.union(candidates);
		boolean continu = true;
		ArrayList<OpenBitSet> connectedComponents = new ArrayList<>();
		while (continu) {
			int nextOne = rest.nextSetBit(0);
			if (nextOne < 0) {
				continu = false;
			} else {
				OpenBitSet found = new OpenBitSet(graph.getVertices().length);
				found.fastSet(nextOne);
				rest.fastClear(nextOne);
				exploreDepthFirst(nextOne, found, rest);
				connectedComponents.add(found);
			}
		}
		ArrayList<ArrayList<Vertex>> listOfCCs = new ArrayList<>();
		for (OpenBitSet cc : connectedComponents) {
			ArrayList<Vertex> listOfVertices = new ArrayList<>();
			int i = 0;
			int k;
			continu = true;
			while (continu) {
				k = cc.nextSetBit(i);
				if (k < 0) {
					continu = false;
				} else {
					listOfVertices.add(graph.getVertices()[k]);
					i = k + 1;
				}
			}
			listOfCCs.add(listOfVertices);
		}
		return listOfCCs;
	}

	private ArrayList<Vertex> descoverBitsetVertices(OpenBitSet vertices){
		ArrayList<Vertex> listOfVertices = new ArrayList<>();
		int i = 0;
		int k;
		boolean continu = true;
		while (continu) {
			k = vertices.nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				listOfVertices.add(graph.getVertices()[k]);
				i = k + 1;
			}
		}

		return listOfVertices;
	}

	private void variateHyperzone(int descriptorIndex, HashSet<Integer> oldPositiveAttributes,
			HashSet<Integer> oldNegativeAttributes, ArrayList<Vertex> verticesWithCharacteristic) {
		System.out.println("sizeOfHyp : " + verticesWithCharacteristic.size());
		HashSet<Integer> positiveAttributes = new HashSet<>(oldPositiveAttributes);
		HashSet<Integer> negativeAttributes = new HashSet<>(oldNegativeAttributes);
		SubGraphWithScore k = new SubGraphWithScore(graph.getVertices().length);

		OpenBitSet concernedVertices = new OpenBitSet(graph.getVertices().length);
		for (Vertex v : verticesWithCharacteristic) {
			concernedVertices.fastSet(v.getIndexInGraph());
		}

//		ArrayList<Candidate> candidates = measureComputer.getRankedCandidates(verticesWithCharacteristic, descriptorIndex, positiveAttributes, negativeAttributes);


//		NOTE: candidates == concerned vertices

//		ArrayList<Double> sortedSums = getRankedSums(descriptorIndex, verticesWithCharacteristic);
//		SolutionsOfCC solutionsOfCC = new SolutionsOfCC(graph.getVertices().length);
//		concernedCCSize = verticesWithCharacteristic.size();


//		TODO: 1. write graph to file

		write(concernedVertices, descriptorIndex);


//		TODO: 2. call fractal




//		[onpaper] SUB-CC(S,(K,Y),R,δ,σ)
//		[onpaper]                             S+                   S-               K       Y                     R
//		enumGoodHyperzones(descriptorIndex, positiveAttributes, negativeAttributes, k, candidates, sortedSums, solutionsOfCC, concernedVertices);
	}

	private void write(OpenBitSet concernedVertices, int descriptorIndex) {
		logger.info("Gathering vertices to write file");

		ArrayList<Vertex> currentVertices = descoverBitsetVertices(concernedVertices);
		HashSet<Integer> currentVecticesId = new HashSet<>();

		String path =  "candidates";

		for (Vertex v : currentVertices) {
			Integer id  = v.getIndexInGraph();
			path += "_" + id.toString();
			currentVecticesId.add(id);
		}

		ArrayList<Vertex> copyiedVerticies = new ArrayList<>();

		for (Vertex v : currentVertices) {
			HashSet<Integer> allowedNeighbors = new HashSet<>();

			HashSet<Integer> toCheck = v.getSetOfNeighborsId();
			HashSet<Integer> smallest = currentVecticesId;
			if (smallest.size() > toCheck.size()) {
				smallest = toCheck;
				toCheck = currentVecticesId;
			}

			for (Integer i: smallest){
				if (toCheck.contains(i)) {
					allowedNeighbors.add(i);
				}
			}

			Vertex copy = new Vertex();
			copy.setId(v.getId());											 // string label
			copy.setIndexInGraph(v.getIndexInGraph());						 // int id
			copy.setDescriptorsValues(v.getDescriptorsValues());
			copy.setDescriptorsScores(v.getDescriptorsScores());
			copy.setDescriptorsTotals(v.getDescriptorsTotals());
			copy.setSetOfNeighborsId(allowedNeighbors);

			copyiedVerticies.add(copy);
		}

		path = path.concat(".graph");

		writeGraph(path, copyiedVerticies);
		writeProperties(path, copyiedVerticies, descriptorIndex);

		logger.info("Finished writing for path: " + path);
	}

	private void writeGraph(String path, ArrayList<Vertex> vertexes) {
		logger.info("Writing graph");
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(path));

			for(Vertex vertex: vertexes) {
				StringBuilder line =
						new StringBuilder(vertex.getIndexInGraph() + " " + vertex.getIndexInGraph());

				for (Integer neighbour: vertex.getSetOfNeighborsId()) {
					line.append(" ").append(neighbour);
				}

				file.write(line.toString() + '\n');
			}

			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeProperties(String path, ArrayList<Vertex> vertexes, int descriptorIndex) {
		logger.info("Writing graph properties");

		path = path + ".prop";

		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(path));

			for(Vertex vertex: vertexes) {
				StringBuilder line =
						new StringBuilder("v " + vertex.getIndexInGraph());

				for (Double attribute: vertex.getDescriptorsValues()[descriptorIndex]) {
					line.append(" ").append(attribute);
				}

				file.write(line.toString() + '\n');
			}

			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Double> getRankedSums(int descriptorIndex, ArrayList<Vertex> verticesWithCharacteristic) {
		if (!designPoint.isActivateUB3()) {
			return new ArrayList<>();
		}
		ArrayList<Double> sums = new ArrayList<>();
		for (Vertex v : verticesWithCharacteristic) {
			sums.add(v.getDescriptorTotal(descriptorIndex));
		}
		Collections.sort(sums);
		return sums;

	}

//  [onpaper] SUB-CC(S,(K,Y),R,δ,σ)
//  [onpaper]                                                                                S+                                 S-                      K                     Y                                                               R
	private ReturnedValue enumGoodHyperzones(int descriptorIndex, HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes, SubGraphWithScore k, ArrayList<Candidate> oldCandidates, ArrayList<Double> oldSums, SolutionsOfCC solutionsOfCC, OpenBitSet oldConcernedVertices) {
		ReturnedValue myReturnedValue = new ReturnedValue();
		ArrayList<Candidate> nextCandidates = new ArrayList<>(oldCandidates);
		int n = 1;


//		[onpaper] if UB2(S,K, Y) < δ then
		if (designPoint.isActivateUB2()) {
			if (k.getScoreComponents() != null && getUB2(k, nextCandidates.size()) < designPoint.getThreshold()) {
				myReturnedValue.UB2IsBad = true;
				return myReturnedValue;
			}
			if (k.getScoreComponents() != null) {
				n = ((int) Math.ceil(designPoint.getThreshold() * ((double) graph.getVertices().length)
						/ ((double) k.getScoreComponents().getAScore()))) - k.getScoreComponents().getSizeOfHyperzone();
			}
		}

//		[onpaper] while |K ∪ Y|≥ σ and Y != ∅ do
		int currentSize = 0;
		if (k.getScoreComponents() != null) {
			currentSize = k.getScoreComponents().getSizeOfHyperzone();
		}
		if (currentSize + nextCandidates.size() < designPoint.getMinSizeSubgraph()) {
			return myReturnedValue;
		}

//		[onpaper] if UB3(S,K, Y) < δ then
		ArrayList<Double> sums = new ArrayList<>(oldSums);
		if (designPoint.isActivateUB3()) {
			if (k.getScoreComponents() != null && isUB3Bad(k, nextCandidates, sums)) {
				return myReturnedValue;
			}
		}

		OpenBitSet concernedVertices = new OpenBitSet();
		concernedVertices.union(oldConcernedVertices);

		nbExploredHyperzones++;
		ArrayList<KUnionCand> listOfNewHyperzones = measureComputer.getRankedKUnionCand(k, nextCandidates);
		ScoreComponents currentScoreComponents = k.getScoreComponents();
		while (listOfNewHyperzones.size() > 0) {
			KUnionCand currentUnion = listOfNewHyperzones.get(0);
			k.addCandidate(currentUnion);
			listOfNewHyperzones.remove(0);
			int j = 0;
			Vertex nextV = null;
			Candidate nextVCand = null;
			j = Collections.binarySearch(nextCandidates, currentUnion.getCand(), Collections.reverseOrder());
			nextV = graph.getVertices()[nextCandidates.get(j).getVertexId()];
			nextVCand = nextCandidates.get(j);
			nextCandidates.remove(j);
			int index = Collections.binarySearch(sums, nextV.getDescriptorTotal(descriptorIndex));
			if (index < 0) {
				new RuntimeException("erreur in calculations");
			}
			if (designPoint.isActivateUB3()) {
				sums.remove(index);
			}
			ReturnedValue childValue = enumGoodHyperzones(descriptorIndex, positiveAttributes, negativeAttributes, k,
					nextCandidates, sums, solutionsOfCC, concernedVertices);
			concernedVertices.fastClear(nextV.getIndexInGraph());
			k.removeCandidate(nextV.getIndexInGraph(), currentScoreComponents);

			// verif child.UB2IsBad
			if (designPoint.isActivateUB2() && childValue.UB2IsBad) {
				break;
			}
			if (!childValue.promising) {
				if (designPoint.isActivateSiblingBasedUB()) {
					pruneCandidates(descriptorIndex, listOfNewHyperzones, nextCandidates, nextVCand, sums,
							concernedVertices);
				}
			} else {
				myReturnedValue.promising = true;
			}
			if (currentSize + nextCandidates.size() < designPoint.getMinSizeSubgraph()) {
				return myReturnedValue;
			}
			if (myReturnedValue.promising) {
				if (designPoint.isActivateCoveringPruning()) {
					if (solutionsOfCC.doesCoverSolutionExistUsingUB1(k.getSetOfVerticesIds(), concernedVertices,
							designPoint.getMinCovering(), n)) {
						return myReturnedValue;
					}
				}
				if (designPoint.isPruneWithConnection()) {
					if (!isConnectionPossible(k.getSetOfVerticesIds(), concernedVertices)) {
						return myReturnedValue;
					}
				}
			}

		}
		if (k.getScoreComponents() != null && k.getScoreComponents().getMeasureValue() >= designPoint.getThreshold()) {
			myReturnedValue.promising = true;
			if (designPoint.isActivateCoveringPruning()) {
				if (isConnectionPossible(k.getSetOfVerticesIds(), k.getSetOfVerticesIds())
						&& !solutionsOfCC.doesCoverSolutionExist(k.getSetOfVerticesIds(), k.getSetOfVerticesIds(),
								designPoint.getMinCovering())) {
					solutionsOfCC.addSolution(concernedVertices, k.getScoreComponents().getMeasureValue());
					System.out.println("solution found" + k.getScoreComponents().getSizeOfHyperzone()
							+ " concerned cc size : " + concernedCCSize);
					addPatternToResult(descriptorIndex, positiveAttributes, negativeAttributes, k);
				}
			} else {
				if (isConnectionPossible(k.getSetOfVerticesIds(), k.getSetOfVerticesIds())) {
					System.out.println(k.getScoreComponents().getSizeOfHyperzone());
					addPatternToResult(descriptorIndex, positiveAttributes, negativeAttributes, k);
				}
			}
		}
		return myReturnedValue;
	}

	private boolean isUB3Bad(SubGraphWithScore k, ArrayList<Candidate> candidates, ArrayList<Double> sums) {
		if (k.getScoreComponents().getMeasureValue() >= designPoint.getThreshold()) {
			return false;
		}
		double aBar = k.getScoreComponents().getAScore();
		double currentSum = k.getScoreComponents().getSumOfH();

		double factor = UtilsFunctions.fraction(k.getScoreComponents().getSizeOfHyperzone() + candidates.size(),
				graph.getVertices().length);
		if (aBar * factor < designPoint.getThreshold()) {
			return true;
		}
		int i = 0;
		if (candidates.size() == 0) {
			return false;
		}
		double minSum = sums.get(0);
		while (i < candidates.size()) {
			Candidate currentH = candidates.get(i);
			aBar = aBar * (currentSum / (currentSum + minSum))
					+ currentH.getScoreComponents().getAScore() * (minSum / (currentSum + minSum));
			currentSum += sums.get(sums.size() - i - 1);
			if (aBar * UtilsFunctions.fraction(k.getScoreComponents().getSizeOfHyperzone() + i + 1,
					graph.getVertices().length) >= designPoint.getThreshold()) {
				return false;
			}
			if (aBar * factor < designPoint.getThreshold()) {
				return true;
			}
			i++;
		}
		return true;
	}

	private boolean pruneCandidates(int descriptorIndex, ArrayList<KUnionCand> listOfNewHyperzones,
			ArrayList<Candidate> nextCandidates, Candidate nextVCand, ArrayList<Double> sums,
			OpenBitSet concernedVertices) {

		OpenBitSet removedCandsSet = new OpenBitSet(graph.getVertices().length);
		HashMap<Double, Integer> removedSums = new HashMap<>();
		int i = nextCandidates.size() - 1;
		boolean continu = true;
		if (i < 0) {
			continu = false;
		}
		while (continu) {
			Candidate myNext = nextCandidates.get(i);
			Vertex vertex = graph.getVertices()[myNext.getVertexId()];
			if (myNext.getScoreComponents().getAScore() <= nextVCand.getScoreComponents().getAScore()) {
				concernedVertices.fastClear(vertex.getIndexInGraph());
				nextCandidates.remove(i);
				i--;
				removedCandsSet.fastSet(vertex.getIndexInGraph());
				if (removedSums.containsKey(vertex.getDescriptorTotal(descriptorIndex))) {
					removedSums.put(vertex.getDescriptorTotal(descriptorIndex),
							removedSums.get(vertex.getDescriptorTotal(descriptorIndex)) + 1);
				} else {
					removedSums.put(vertex.getDescriptorTotal(descriptorIndex), 1);
				}
				if (i < 0) {
					continu = false;
				}
			} else {
				continu = false;
			}
		}
		i = 0;
		while (i < listOfNewHyperzones.size()) {
			KUnionCand myNext = listOfNewHyperzones.get(i);
			if (removedCandsSet.fastGet(myNext.getCandidateId())) {
				listOfNewHyperzones.remove(i);
			} else {
				i++;
			}
		}
		if (listOfNewHyperzones.size() != nextCandidates.size()) {
			new RuntimeException("sizes not equal");
		}
		i = 0;
		while (i < sums.size()) {
			if (removedSums.containsKey(sums.get(i))) {
				removedSums.put(sums.get(i), removedSums.get(sums.get(i)) - 1);
				if (removedSums.get(sums.get(i)) == 0) {
					removedSums.remove(sums.get(i));
				}
				sums.remove(i);
			} else {
				i++;
			}
		}
		return false;
	}

	private boolean isConnectionPossible(OpenBitSet currentK, OpenBitSet kUnionCand) {
		OpenBitSet found = new OpenBitSet(graph.getVertices().length);
		OpenBitSet rest = new OpenBitSet(graph.getVertices().length);
		int firstIndex = currentK.nextSetBit(0);
		if (firstIndex < 0) {
			return true;
		}
		found.set(firstIndex);
		rest.union(kUnionCand);
		rest.clear(firstIndex);
		exploreDepthFirst(firstIndex, found, rest);
		return (!currentK.intersects(rest));
	}

	private void exploreDepthFirst(int currentIndex, OpenBitSet found, OpenBitSet rest) {
		for (Integer neighborIndex : graph.getVertices()[currentIndex].getSetOfNeighborsId()) {
			if (rest.get(neighborIndex)) {
				found.set(neighborIndex);
				rest.clear(neighborIndex);
				exploreDepthFirst(neighborIndex, found, rest);
			}
		}
	}

	private void addPatternToResult(int descriptorIndex, HashSet<Integer> positiveAttributes,
			HashSet<Integer> negativeAttributes, SubGraphWithScore currentSubGraphScore) {
		double measureValue = currentSubGraphScore.getScoreComponents().getMeasureValue();
		HashSet<Vertex> vertices = new HashSet<>();
		int i = 0;
		int k;
		boolean continu = true;
		while (continu) {
			k = currentSubGraphScore.getSetOfVerticesIds().nextSetBit(i);
			if (k < 0) {
				continu = false;
			} else {
				vertices.add(graph.getVertices()[k]);
				i = k + 1;
			}
		}
		Pattern pattern = new Pattern(vertices, graph.getDescriptorsMetaData()[descriptorIndex], positiveAttributes,
				negativeAttributes, measureValue);
		patterns.add(pattern);
	}

	public void writeResultInFile() {
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

	private boolean isClosedCharacteristic(int descriptorIndex, OpenBitSet verticesSet,
			HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes) {
		for (int i = 0; i < graph.getDescriptorsMetaData()[descriptorIndex].getAttributesName().length; i++) {
			if (!positiveAttributes.contains(i)) {
				OpenBitSet newCandidates1 = measureComputer.getVerticesWithIntersectCharacteristics(verticesSet,
						descriptorIndex, i, 1);
				if (newCandidates1.cardinality() == verticesSet.cardinality()) {
					return false;
				}
			}
			if (!negativeAttributes.contains(i)) {
				OpenBitSet newCandidates1 = measureComputer.getVerticesWithIntersectCharacteristics(verticesSet,
						descriptorIndex, i, -1);
				if (newCandidates1.cardinality() == verticesSet.cardinality()) {
					return false;
				}
			}
		}
		return true;
	}

	/****************************************************************************
	 ******************* naive version of subgraph exploration ******************
	 ****************************************************************************/

	private void variateHyperzoneNaively(int descriptorIndex, HashSet<Integer> positiveAttributes,
			HashSet<Integer> negativeAttributes, ArrayList<Vertex> verticesWithCharacteristic) {
		// more efficient method to generate all subgraphs, verified
		HashSet<Vertex> verticesNotYetExplored = new HashSet<>(verticesWithCharacteristic);
		HashSet<Vertex> neighbors = new HashSet<>();
		HashSet<Vertex> connectedSubGraph = new HashSet<>();
		generateHyperzonesNaively(verticesWithCharacteristic, descriptorIndex, positiveAttributes, negativeAttributes,
				verticesNotYetExplored, connectedSubGraph, neighbors, null);
	}

	private void generateHyperzonesNaively(ArrayList<Vertex> verticesWithCharacteristic, int descriptorIndex,
			HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes,
			HashSet<Vertex> verticesNotYetExplored, HashSet<Vertex> connectedSubGraph, HashSet<Vertex> neighbors,
			ScoreComponents scoreComponent) {
		if (connectedSubGraph.size() + verticesNotYetExplored.size() < designPoint.getMinSizeSubgraph()) {
			return;
		}
		HashSet<Vertex> candidates;
		nbExploredHyperzones++;
		if (connectedSubGraph.isEmpty()) {
			candidates = new HashSet<>(verticesNotYetExplored);
		} else {
			candidates = new HashSet<>(verticesNotYetExplored);
			candidates.retainAll(neighbors);
		}
		if (candidates.isEmpty()) {
			verifyCharacteristicInZone(connectedSubGraph, descriptorIndex, positiveAttributes, negativeAttributes,
					scoreComponent);
		} else {
			Vertex currentV = candidates.iterator().next();
			verticesNotYetExplored.remove(currentV);
			generateHyperzonesNaively(verticesWithCharacteristic, descriptorIndex, positiveAttributes,
					negativeAttributes, verticesNotYetExplored, connectedSubGraph, neighbors, scoreComponent);
			connectedSubGraph.add(currentV);
			ScoreComponents newScore = null;
			if (scoreComponent == null) {
				Candidate c = measureComputer.getCandidateForVertex(currentV, descriptorIndex, positiveAttributes,
						negativeAttributes);
				newScore = c.getScoreComponents();
			} else {
				Candidate c = measureComputer.getCandidateForVertex(currentV, descriptorIndex, positiveAttributes,
						negativeAttributes);
				SubGraphWithScore ss = new SubGraphWithScore(scoreComponent);
				KUnionCand kUnionC = new KUnionCand(ss, c, graph.getVertices().length);
				newScore = kUnionC.getScoreComponents();
			}
			HashSet<Vertex> neighborsOfV = new HashSet<>();
			for (Vertex otherV : verticesWithCharacteristic) {
				if (currentV.getSetOfNeighborsId().contains(otherV.getIndexInGraph())) {
					neighborsOfV.add(otherV);
				}
			}
			neighborsOfV.removeAll(neighbors);
			neighbors.addAll(neighborsOfV);
			generateHyperzonesNaively(verticesWithCharacteristic, descriptorIndex, positiveAttributes,
					negativeAttributes, verticesNotYetExplored, connectedSubGraph, neighbors, newScore);
			verticesNotYetExplored.add(currentV);
			connectedSubGraph.remove(currentV);
			neighbors.removeAll(neighborsOfV);
		}

	}

	private void verifyCharacteristicInZone(HashSet<Vertex> currentExploredHyperzone, int descriptorIndex,
			HashSet<Integer> positiveAttributes, HashSet<Integer> negativeAttributes, ScoreComponents scoreComponent) {
		if (scoreComponent == null) {
			return;
		}
		double measureValue = scoreComponent.getMeasureValue();
		if (measureValue >= designPoint.getThreshold()) {
			Pattern pattern = new Pattern(currentExploredHyperzone, graph.getDescriptorsMetaData()[descriptorIndex],
					positiveAttributes, negativeAttributes, measureValue);
			patterns.add(pattern);
		}

	}

}
