package model;

import java.util.Arrays;
import java.util.HashMap;

public class Graph {
	private Vertex[] vertices;
	private DescriptorMetaData[] descriptorsMetaData;
	private HashMap<String, Integer> indicesOfVertices;

	public Graph(int descriptorNumber, int numberOfVertices) {
		descriptorsMetaData = new DescriptorMetaData[descriptorNumber];
		vertices = new Vertex[numberOfVertices];
		indicesOfVertices = new HashMap<>();
	}

	public DescriptorMetaData[] getDescriptorsMetaData() {
		return descriptorsMetaData;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public HashMap<String, Integer> getIndicesOfVertices() {
		return indicesOfVertices;
	}

	public void sortVertices() {
		Arrays.sort(vertices);
		for (int i = 0; i < vertices.length; i++) {
			indicesOfVertices.put(vertices[i].getId(), i);
			vertices[i].setIndexInGraph(i);
		}
	}
}
