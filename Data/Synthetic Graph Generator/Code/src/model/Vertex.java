package model;

import java.util.HashSet;

public class Vertex {
	private double[] descriptorValues = null;
	private String vertexId;
	private HashSet<String> neighbors;

	public Vertex(int sizeOfDescriptor, String vertexId) {
		descriptorValues = new double[sizeOfDescriptor];
		this.vertexId = vertexId;
		neighbors = new HashSet<>();
	}

	public double[] getDescriptorValues() {
		return descriptorValues;
	}

	public HashSet<String> getNeighbors() {
		return neighbors;
	}

	public String toJsonWithoutReturnLign(String tabulation) {
		String jsonString = tabulation + "{";
		jsonString += " vertexId : " + "\"" + vertexId + "\", ";
		jsonString += "descriptorsValues :[ ";
		jsonString += "[";
		boolean firstValue = true;
		for (double value : descriptorValues) {
			if (firstValue) {
				firstValue = false;
			} else {
				jsonString += ",";
			}
			jsonString += formatDouble(value);
		}
		jsonString += "] ";
		jsonString += "] ";
		jsonString += tabulation + "}";
		return jsonString;
	}

	public String getVertexId() {
		return vertexId;
	}

	private String formatDouble(double d) {
		int n = (int) Math.floor(d);
		return Integer.toString(n);
	}
}
