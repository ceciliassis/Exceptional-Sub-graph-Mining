package model;

import java.util.HashSet;

public class Pattern {
	private HashSet<Vertex> hyperzone;
	private Characteristic characteristic;

	public Pattern(HashSet<Vertex> hyperzone, DescriptorMetaData descriptorMetaData, HashSet<Integer> sPlus,
			HashSet<Integer> sMinus, double score) {
		this.hyperzone = hyperzone;
		characteristic = new Characteristic(descriptorMetaData, sPlus, sMinus, score);
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
			jsonString += "\"" + v.getIndexInGraph() + "\"";
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"characteristic\" : \n";
		jsonString += characteristic.toJson(tabulation + "\t");
		jsonString += "\n";
		jsonString += tabulation + "}";
		return jsonString;
	}
}
