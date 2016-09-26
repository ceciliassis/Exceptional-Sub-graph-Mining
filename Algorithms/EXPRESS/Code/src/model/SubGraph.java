package model;

import org.apache.lucene.util.OpenBitSet;

public class SubGraph {
	private int descriptorIndex;
	private int sizeOfGraph;
	private int subgraphSize;
	private OpenBitSet vertices;
	private OpenBitSet neighbors;
	private double[] attAScores;
	private double AScore;
	private double sumOfD;

	public SubGraph(Vertex v, int sizeOfGraph, int descriptorIndex, int sizeOfDescriptor) {
		this.descriptorIndex = descriptorIndex;
		this.sizeOfGraph = sizeOfGraph;
		vertices = new OpenBitSet(sizeOfGraph);
		vertices.fastSet(v.getIndexInGraph());
		neighbors = new OpenBitSet(sizeOfGraph);
		neighbors.union(v.getNeighborsBitSet());
		attAScores = new double[sizeOfDescriptor];
		AScore = 0;
		for (int i = 0; i < sizeOfDescriptor; i++) {
			attAScores[i] = v.getAttributeDescriptorScore(descriptorIndex, i) * ((double) sizeOfGraph);
			if (attAScores[i] > 0) {
				AScore += attAScores[i];
			} else {
				AScore -= attAScores[i];
			}
		}
		subgraphSize = 1;
		sumOfD = v.getDescriptorTotal(descriptorIndex);
	}

	public void addNeighbor(Vertex v) {
		vertices.fastSet(v.getIndexInGraph());
		neighbors.union(v.getNeighborsBitSet());
		neighbors.andNot(vertices);
		double newAScore = 0;
		for (int i = 0; i < attAScores.length; i++) {
			if (attAScores[i] > 0 && v.getAttributeDescriptorScore(descriptorIndex, i) > 0) {
				attAScores[i] = (attAScores[i] * sumOfD + v.getAttributeDescriptorScore(descriptorIndex, i)
						* v.getDescriptorTotal(descriptorIndex) * ((double) sizeOfGraph))
						/ (sumOfD + v.getDescriptorTotal(descriptorIndex));
				newAScore += attAScores[i];
			} else if (attAScores[i] < 0 && v.getAttributeDescriptorScore(descriptorIndex, i) < 0) {
				attAScores[i] = (attAScores[i] * sumOfD + v.getAttributeDescriptorScore(descriptorIndex, i)
						* v.getDescriptorTotal(descriptorIndex) * ((double) sizeOfGraph))
						/ (sumOfD + v.getDescriptorTotal(descriptorIndex));
				newAScore -= attAScores[i];
			} else {
				attAScores[i] = 0;
			}
		}
		AScore = newAScore;
		subgraphSize++;
		sumOfD += v.getDescriptorTotal(descriptorIndex);
	}

	public OpenBitSet getVertices() {
		return vertices;
	}

	public OpenBitSet getNeighbors() {
		return neighbors;
	}

	public double[] getAttAScores() {
		return attAScores;
	}

	public int getSubgraphSize() {
		return subgraphSize;
	}

	public double getAScore() {
		return AScore;
	}

	public double getSumOfD() {
		return sumOfD;
	}

}
