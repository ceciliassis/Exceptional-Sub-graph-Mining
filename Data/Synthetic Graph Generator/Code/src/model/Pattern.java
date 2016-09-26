package model;

import java.util.HashSet;

public class Pattern {
	HashSet<Integer> positiveAttributesIndex;
	HashSet<Integer> negativeAttributesIndex;
	private int minColumnIndex;
	private int maxColumnIndex;
	private int minRowIndex;
	private int maxRowIndex;
	private double patternDifferenceRate;
	int numberOfVertex;
	HashSet<Vertex> setOfVertices;

	public Pattern(HashSet<Integer> positiveAttributesIndex, HashSet<Integer> negativeAttributesIndex,
			int numberOfVertex, int minColumnIndex, int maxColumnIndex, int minRowIndex, int maxRowIndex,
			double patternDifferenceRate) {
		super();
		this.positiveAttributesIndex = positiveAttributesIndex;
		this.negativeAttributesIndex = negativeAttributesIndex;
		this.minColumnIndex = minColumnIndex;
		this.maxColumnIndex = maxColumnIndex;
		this.minRowIndex = minRowIndex;
		this.maxRowIndex = maxRowIndex;
		this.patternDifferenceRate = patternDifferenceRate;
		this.numberOfVertex=numberOfVertex;
	}

	public int addPatternToGraph(Graph graph) {
		int nbGeneratedEdges=0;
		setOfVertices=new HashSet<>();
		int numberOfLeftVertex = numberOfVertex;
		for (int i = minRowIndex; i <= maxRowIndex; i++) {
			for (int j = minColumnIndex; j <= maxColumnIndex; j++) {
				if (numberOfLeftVertex > 0) {
					if (i<maxRowIndex){
						graph.getTableOfVertices()[i][j].getNeighbors().add(graph.getTableOfVertices()[i+1][j].getVertexId());
						nbGeneratedEdges++;
					}
					if (j<minColumnIndex){
						graph.getTableOfVertices()[i][j].getNeighbors().add(graph.getTableOfVertices()[i][j+1].getVertexId());
						nbGeneratedEdges++;
					}
					setOfVertices.add(graph.getTableOfVertices()[i][j]);
					for (int k : positiveAttributesIndex) {
						graph.getTableOfVertices()[i][j]
								.getDescriptorValues()[k] = graph.getTableOfVertices()[i][j].getDescriptorValues()[k]
										* (1 + patternDifferenceRate);
					}
					for (int k : negativeAttributesIndex) {
						graph.getTableOfVertices()[i][j]
								.getDescriptorValues()[k] = graph.getTableOfVertices()[i][j].getDescriptorValues()[k]
										* (1 - patternDifferenceRate);
					}
					numberOfLeftVertex--;
				} else
					break;
			}
		}
		return nbGeneratedEdges;
	}
	public HashSet<Vertex> getSetOfVertices() {
		return setOfVertices;
	}
	public HashSet<Integer> getPositiveAttributesIndex() {
		return positiveAttributesIndex;
	}
	public HashSet<Integer> getNegativeAttributesIndex() {
		return negativeAttributesIndex;
	}
	public String toJSon(String tabulation){
		String jsonString=tabulation+"{\n";
		jsonString+=tabulation+"\tvertices : [";
		boolean firstInsert=true;
		for (Vertex v : setOfVertices){
			if (firstInsert){
				firstInsert=false;
			}
			else {
				jsonString+=",";
			}
			jsonString+="\""+v.getVertexId()+"\"";
		}
		jsonString+="],\n";
		jsonString+=tabulation+"\tpositiveContrastedAttributes : [";
		firstInsert=true;
		for (int i : positiveAttributesIndex){
			if (firstInsert){
				firstInsert=false;
			}
			else {
				jsonString+=",";
			}
			jsonString+="\"att"+i+"\"";
		}
		jsonString+="],\n";
		jsonString+=tabulation+"\tnegativeContrastedAttributes : [";
		firstInsert=true;
		for (int i : negativeAttributesIndex){
			if (firstInsert){
				firstInsert=false;
			}
			else {
				jsonString+=",";
			}
			jsonString+="\"att"+i+"\"";
		}
		jsonString+="]\n";
		jsonString+=tabulation+"}";
		return jsonString;
	}
}
