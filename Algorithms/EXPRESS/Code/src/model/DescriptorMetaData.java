package model;

public class DescriptorMetaData {
	private String descriptorName;
	private AttributesType attributesType;
	private String[] attributesName;

	public DescriptorMetaData(String descriptorName, AttributesType attributesType, String[] attributesName) {
		this.descriptorName = descriptorName;
		this.attributesType = attributesType;
		this.attributesName = attributesName;
	}

	public String getDescriptorName() {
		return descriptorName;
	}

	public String[] getAttributesName() {
		return attributesName;
	}

	public AttributesType getAttributesType() {
		return attributesType;
	}

}
