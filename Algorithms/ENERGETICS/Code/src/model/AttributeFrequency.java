package model;

public class AttributeFrequency implements Comparable<AttributeFrequency> {
	private int realAttributeIndex;
	private long attributeFrequency;

	public AttributeFrequency(int realAttributeIndex, long attributeFrequency) {
		this.realAttributeIndex = realAttributeIndex;
		this.attributeFrequency = attributeFrequency;
	}

	public int getRealAttributeIndex() {
		return realAttributeIndex;
	}

	public long getAttributeFrequency() {
		return attributeFrequency;
	}

	@Override
	public int compareTo(AttributeFrequency o) {
		if (attributeFrequency > o.getAttributeFrequency()) {
			return 1;
		} else if (attributeFrequency < o.getAttributeFrequency()) {
			return -1;
		}
		return 0;
	}

}
