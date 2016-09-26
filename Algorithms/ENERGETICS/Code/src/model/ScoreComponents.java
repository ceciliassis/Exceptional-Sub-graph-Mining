package model;

public class ScoreComponents {
	private double measureValue;
	private int sizeOfHyperzone;
	private double sumOfH;
	private double AScore;

	public ScoreComponents(double measureValue, int sizeOfHyperzone, double sumOfH, double aScore) {
		this.measureValue = measureValue;
		this.sizeOfHyperzone = sizeOfHyperzone;
		this.sumOfH = sumOfH;
		AScore = aScore;
	}

	public double getMeasureValue() {
		return measureValue;
	}

	public int getSizeOfHyperzone() {
		return sizeOfHyperzone;
	}

	public double getSumOfH() {
		return sumOfH;
	}

	public double getAScore() {
		return AScore;
	}
}
