package model;

public class Candidate implements Comparable<Candidate> {
	private int vertexId;
	private ScoreComponents scoreComponents;

	public Candidate(int vertexId, ScoreComponents scoreComponents) {
		this.vertexId = vertexId;
		this.scoreComponents = scoreComponents;
	}

	public ScoreComponents getScoreComponents() {
		return scoreComponents;
	}

	public int getVertexId() {
		return vertexId;
	}

	@Override
	public int compareTo(Candidate anotherCandidate) {
		if (scoreComponents.getAScore() > anotherCandidate.getScoreComponents().getAScore()) {
			return 1;
		}
		if (scoreComponents.getAScore() < anotherCandidate.getScoreComponents().getAScore()) {
			return -1;
		}
		if (vertexId < anotherCandidate.getVertexId()) {
			return 1;
		}
		if (vertexId > anotherCandidate.getVertexId()) {
			return -1;
		}
		return 0;
	}
}
