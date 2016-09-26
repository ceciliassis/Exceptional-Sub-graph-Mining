package model;

import java.util.ArrayList;

import org.apache.lucene.util.OpenBitSet;

public class SolutionsOfCC {
	private ArrayList<OneSolution> solutionsOfVertices;
	private int sizeOfBitSets;

	public SolutionsOfCC(int sizeOfBitSets) {
		solutionsOfVertices = new ArrayList<>();
		this.sizeOfBitSets = sizeOfBitSets;
	}

	public void addSolution(OpenBitSet indicesOfVertices, double measureValue) {
		solutionsOfVertices.add(new OneSolution(indicesOfVertices, measureValue));
	}

	public boolean isCoverSolutionExists(OpenBitSet k, OpenBitSet kUnionCand, double maxSim) {
		for (OneSolution solution : solutionsOfVertices) {
			if (lowerBoundSim(k, kUnionCand, solution.getSolutionVertices()) >= maxSim) {
				return true;
			}
		}
		return false;
	}

	private double lowerBoundSim(OpenBitSet k, OpenBitSet kUnionCand, OpenBitSet solution) {
		OpenBitSet kInterSol = new OpenBitSet(sizeOfBitSets);
		kInterSol.union(k);
		kInterSol.intersect(solution);
		if (kInterSol.cardinality() == 0) {
			return 0;
		}
		OpenBitSet candMinusSol = new OpenBitSet(sizeOfBitSets);
		candMinusSol.union(kUnionCand);
		candMinusSol.andNot(k);
		candMinusSol.andNot(solution);
		return (((double) kInterSol.cardinality()) / ((double) (k.cardinality() + candMinusSol.cardinality())));
	}

	// n represents the minimum number of vertices that we must add to k to make
	// the measure
	// exceed the threshold
	public boolean isCoverSolutionExistsUsingUB1(OpenBitSet k, OpenBitSet kUnionCand, double maxSim, int n) {
		for (OneSolution solution : solutionsOfVertices) {
			if (lowerBoundSimUsingUB1(k, kUnionCand, solution.getSolutionVertices(), n) >= maxSim) {
				return true;
			}
		}
		return false;
	}

	private double lowerBoundSimUsingUB1(OpenBitSet k, OpenBitSet kUnionCand, OpenBitSet solution, int n) {
		OpenBitSet kInterSol = new OpenBitSet(sizeOfBitSets);
		kInterSol.union(k);
		kInterSol.intersect(solution);
		OpenBitSet candMinusSol = new OpenBitSet(sizeOfBitSets);
		candMinusSol.union(kUnionCand);
		candMinusSol.andNot(k);
		candMinusSol.andNot(solution);
		long partNumerator = 0;
		long cardinalityOfCandMinusSol = candMinusSol.cardinality();
		long partDenominator = cardinalityOfCandMinusSol;
		if (n > cardinalityOfCandMinusSol) {
			partNumerator = n - cardinalityOfCandMinusSol;
			partDenominator = n;
		}
		return (((double) (kInterSol.cardinality() + partNumerator)) / ((double) (k.cardinality() + partDenominator)));
	}

}
