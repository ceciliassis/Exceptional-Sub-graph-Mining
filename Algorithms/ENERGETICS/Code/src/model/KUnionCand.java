package model;

import utils.UtilsFunctions;

public class KUnionCand implements Comparable<KUnionCand> {
	private int candidateId;
	private ScoreComponents scoreComponents;
	private Candidate cand;

	public Candidate getCand() {
		return cand;
	}

	public KUnionCand(SubGraphWithScore k, Candidate cand, int graphSize) {
		this.cand = cand;
		if (k.getScoreComponents() == null) {
			scoreComponents = cand.getScoreComponents();
		} else {
			double AScore = ((k.getScoreComponents().getMeasureValue()
					* UtilsFunctions.fraction(graphSize, k.getScoreComponents().getSizeOfHyperzone())
					* (k.getScoreComponents().getSumOfH()
							/ (k.getScoreComponents().getSumOfH() + cand.getScoreComponents().getSumOfH())))
					+ (cand.getScoreComponents().getMeasureValue() * UtilsFunctions.fraction(graphSize, 1)
							* (cand.getScoreComponents().getSumOfH()
									/ (k.getScoreComponents().getSumOfH() + cand.getScoreComponents().getSumOfH()))));
			double sumOfH = k.getScoreComponents().getSumOfH() + cand.getScoreComponents().getSumOfH();
			int sizeOfHyperzone = k.getScoreComponents().getSizeOfHyperzone() + 1;
			double measureValue = AScore * UtilsFunctions.fraction(sizeOfHyperzone, graphSize);
			scoreComponents = new ScoreComponents(measureValue, sizeOfHyperzone, sumOfH, AScore);
		}
		candidateId = cand.getVertexId();
	}

	public int getCandidateId() {
		return candidateId;
	}

	public ScoreComponents getScoreComponents() {
		return scoreComponents;
	}

	@Override
	public int compareTo(KUnionCand anotherCandidate) {
		if (scoreComponents.getAScore() > anotherCandidate.getScoreComponents().getAScore()) {
			return 1;
		}
		if (scoreComponents.getAScore() < anotherCandidate.getScoreComponents().getAScore()) {
			return -1;
		}
		if (candidateId < anotherCandidate.getCandidateId()) {
			return 1;
		}
		if (candidateId > anotherCandidate.getCandidateId()) {
			return -1;
		}
		return 0;
	}
}
