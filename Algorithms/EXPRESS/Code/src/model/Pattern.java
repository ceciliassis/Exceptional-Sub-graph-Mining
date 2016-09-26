package model;

import java.util.HashSet;

import org.apache.lucene.util.OpenBitSet;

public class Pattern {
	public OpenBitSet vertices;
	private HashSet<Vertex> hyperzone;
	private Characteristic characteristic;

	public Pattern(OpenBitSet vertices, HashSet<Vertex> hyperzone, DescriptorMetaData descriptorMetaData,
			HashSet<Integer> sPlus, HashSet<Integer> sMinus, double score) {
		this.hyperzone = hyperzone;
		characteristic = new Characteristic(descriptorMetaData, sPlus, sMinus, score);
		this.vertices = vertices;
	}

	public OpenBitSet getVertices() {
		return vertices;
	}

	public HashSet<Vertex> getHyperzone() {
		return hyperzone;
	}

	public Characteristic getCharacteristic() {
		return characteristic;
	}

	public String toJson(String tabulation) {
		String jsonString = tabulation + "{\n";
		jsonString += tabulation + "\t\"subgraph\" : [";
		boolean firstInsert = true;
		for (Vertex v : hyperzone) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				jsonString += ",";
			}
			jsonString += "\"" + v.getId() + "\"";
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"characteristic\" : \n";
		jsonString += characteristic.toJson(tabulation + "\t");
		jsonString += "\n";
		jsonString += tabulation + "}";
		return jsonString;
	}
}
