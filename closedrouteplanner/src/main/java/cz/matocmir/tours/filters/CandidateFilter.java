package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;

import java.util.List;

public interface CandidateFilter {
	List<Candidate> filter(List<Candidate> cands);
}
