package model;

import org.apache.lucene.util.OpenBitSet;

public class OneSolution {
	private OpenBitSet solutionVertices;
	private double measureValue;
	public OneSolution(OpenBitSet solutionVertices, double measureValue) {
		super();
		this.solutionVertices = solutionVertices;
		this.measureValue = measureValue;
	}
	public OpenBitSet getSolutionVertices() {
		return solutionVertices;
	}
	public double getMeasureValue() {
		return measureValue;
	}

}
