package model;

import java.util.HashSet;

import org.apache.lucene.util.OpenBitSet;

public class Vertex implements Comparable<Vertex> {
	private String id;
	private int indexInGraph;
	private double[][] descriptorsValues = null;
	private double[] descriptorsTotals = null;
	private double[][] descriptorsScores = null;
	private OpenBitSet neighborsBitSet;
	private HashSet<Integer> setOfNeighborsId;

	public Vertex(String id, double[][] attributesValues) {
		this.id = id;
		this.descriptorsValues = attributesValues;
		this.descriptorsTotals = new double[attributesValues.length];
		for (int i = 0; i < attributesValues.length; i++) {
			double sum = 0;
			for (int j = 0; j < attributesValues[i].length; j++) {
				sum += attributesValues[i][j];
			}
			descriptorsTotals[i] = sum;
		}
		setOfNeighborsId = new HashSet<>();
	}

	public void setIndexInGraph(int indexInGraph) {
		this.indexInGraph = indexInGraph;
	}

	public int getIndexInGraph() {
		return indexInGraph;
	}

	public HashSet<Integer> getSetOfNeighborsId() {
		return setOfNeighborsId;
	}

	public void setupNeighborsIds(int sizeOfGraph) {
		neighborsBitSet = new OpenBitSet(sizeOfGraph);
		for (int neighborId : setOfNeighborsId) {
			neighborsBitSet.fastSet(neighborId);
		}
	}

	public String getId() {
		return id;
	}

	public double getDescriptorValue(int descriptorIndex, int attributeIndex) {
		return descriptorsValues[descriptorIndex][attributeIndex];
	}

	public double getDescriptorTotal(int descriptorIndex) {
		return descriptorsTotals[descriptorIndex];
	}

	public void setDescriptorsScores(double[][] scores) {
		descriptorsScores = scores;
	}

	public double getAttributeDescriptorScore(int descriptorId, int attributeId) {
		return descriptorsScores[descriptorId][attributeId];
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((Vertex) obj).getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(Vertex o) {
		return id.compareTo(o.getId());
	}
}
