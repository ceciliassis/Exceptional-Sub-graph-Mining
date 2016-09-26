package model;

import org.apache.lucene.util.OpenBitSet;

public class SubGraphWithScore {
	private OpenBitSet setOfVerticesIds;
	private ScoreComponents scoreComponents;

	public SubGraphWithScore(int sizeOfGraph) {
		setOfVerticesIds = new OpenBitSet(sizeOfGraph);
		scoreComponents = null;
	}

	public SubGraphWithScore(ScoreComponents scoreComponent) {
		setOfVerticesIds = null;
		this.scoreComponents = scoreComponent;
	}

	public ScoreComponents getScoreComponents() {
		return scoreComponents;
	}

	public OpenBitSet getSetOfVerticesIds() {
		return setOfVerticesIds;
	}

	public void addCandidate(KUnionCand kUnionCand) {
		setOfVerticesIds.fastSet(kUnionCand.getCandidateId());
		scoreComponents = kUnionCand.getScoreComponents();
	}

	public void removeCandidate(int indexOfCandidate, ScoreComponents previousScoreComponent) {
		setOfVerticesIds.fastClear(indexOfCandidate);
		scoreComponents = previousScoreComponent;
	}

}
