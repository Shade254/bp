package cz.matocmir.tours.filters;

import cz.matocmir.tours.model.Candidate;

import java.util.List;

/***
 * Common interface for every CandidateFilter, individual filters are described in the paper Chapter 4
 */
public interface CandidateFilter {
	List<Candidate> filter(List<Candidate> cands);
}
