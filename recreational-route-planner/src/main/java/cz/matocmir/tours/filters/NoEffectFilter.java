package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;

import java.util.List;

public class NoEffectFilter implements CandidateFilter {
	@Override
	public List<Candidate> filter(List<Candidate> cands) {
		return cands;
	}
}
