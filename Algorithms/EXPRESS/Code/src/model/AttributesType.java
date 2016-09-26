package model;

public enum AttributesType {
	NOMINAL, ORDINAL;

	public static AttributesType getFromString(String typeAsString) {
		if (typeAsString.equals("nominal")) {
			return AttributesType.NOMINAL;
		} else if (typeAsString.equals("ordinal")) {
			return AttributesType.ORDINAL;
		} else {
			new RuntimeException("non attributes type named :" + typeAsString);
		}
		return null;
	}
}
