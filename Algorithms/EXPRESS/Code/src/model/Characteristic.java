package model;

import java.util.HashSet;

public class Characteristic {
	private DescriptorMetaData descriptorMetaData;
	private HashSet<Integer> sPlus;
	private HashSet<Integer> sMinus;
	private double score;

	public Characteristic(DescriptorMetaData descriptorMetaData, HashSet<Integer> sPlus, HashSet<Integer> sMinus,
			double score) {
		this.descriptorMetaData = descriptorMetaData;
		this.sPlus = sPlus;
		this.sMinus = sMinus;
		this.score = score;
	}

	public HashSet<Integer> getsPlus() {
		return sPlus;
	}

	public HashSet<Integer> getsMinus() {
		return sMinus;
	}

	public double getScore() {
		return score;
	}

	public String toJson(String tabulation) {
		String jsonString = tabulation + "{\n";
		jsonString += tabulation + "\t\"descriptorName\" : \"" + descriptorMetaData.getDescriptorName() + "\",\n";
		jsonString += tabulation + "\t\"positiveAttributes\" : [";
		boolean firstInsert = true;
		for (int i : sPlus) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				jsonString += ",";
			}
			jsonString += "\"" + descriptorMetaData.getAttributesName()[i] + "\"";
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"negativeAttributes\" : [";
		firstInsert = true;
		for (int i : sMinus) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				jsonString += ",";
			}
			jsonString += "\"" + descriptorMetaData.getAttributesName()[i] + "\"";
		}
		jsonString += "],\n";
		jsonString += tabulation + "\t\"score\" : " + score + "\n";
		jsonString += tabulation + "}";
		return jsonString;
	}
}
