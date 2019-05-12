package cz.matocmir.tours.forwardpath;

import cz.matocmir.tours.model.Candidate;
import cz.matocmir.tours.model.TourNode;
import cz.matocmir.tours.utils.TourUtils;

import java.util.*;

public class CandidatesPicker {

	private List<Candidate> candidates;
	private double[] probabilities;
	private double[] distances;
	private double[] unpleasantness;
	private Random r;
	private double centerLat;
	private double centerLon;
	private double minLength;

	public CandidatesPicker(List<Candidate> candidates, double centerLon, double centerLat, double minLength) {
		this.candidates = candidates;
		this.probabilities = new double[candidates.size()];
		this.r = new Random();
		this.centerLat = centerLat;
		this.centerLon = centerLon;
		this.minLength = minLength;

		distances = new double[candidates.size()];
		for (int i = 0; i < candidates.size(); i++)
			distances[i] = Double.MAX_VALUE;

		unpleasantness = new double[candidates.size()];

		//create histogram
		int[] unpleasantnessBins = new int[101];
		int pos = 0;
		for (Candidate c : candidates) {
			if (c.length != 0) {
				double d = c.weight / (c.length / 100);
				unpleasantness[pos] = d;
				int binNr = (int) (d * 10);
				if (binNr >= unpleasantnessBins.length)
					binNr = unpleasantnessBins.length - 1;
				unpleasantnessBins[binNr]++;
			}
			pos++;
		}

		//get significant counts (bin numbers)
		int sum = 0, pos10 = -1, pos50 = -1;
		for (int i = 0; i < unpleasantnessBins.length; i++) {
			sum += unpleasantnessBins[i];
			if (sum > unpleasantness.length / 10 && pos10 == -1)
				pos10 = i;
			if (sum > unpleasantness.length / 2 && pos50 == -1)
				pos50 = i;
		}

		//normalization of unpleasantness to 0.0 - 1.0 interval (but i do not fully understand how)
		if (pos10 < 100) {
			double pos10unpleasantness = pos10 / 10.;
			double factor = Math.log(0.25) / (1 - (pos50 + 1.) / pos10);
			for (int i = 0; i < probabilities.length; i++) {
				unpleasantness[i] = Math.exp(factor * Math.min(0, (1 - unpleasantness[i] / pos10unpleasantness)));
			}
		} else {
			for (int i = 0; i < probabilities.length; i++)
				unpleasantness[i] = 1;
		}

		// Set the probabilities
		setProbabilities();
	}

	public Candidate selectCandidate() {
		// Select candidate using the probabilities
		Candidate selected = selectCandidateUsingProbabilities();
		if (selected == null)
			return selected;

		// Update distances
		Iterator<Candidate> tmp = candidates.iterator();
		for (int j = 0; j < candidates.size(); j++) {
			double d = TourUtils
					.computeGreatCircleDistance(selected.correspNode.getNode(), tmp.next().correspNode.getNode());
			if (d < distances[j])
				distances[j] = d;
		}

		// Set the probabilities
		setProbabilities();
		return selected;
	}

	public LinkedList<Candidate> selectCandidates(int nr) {
		LinkedList<Candidate> out = new LinkedList<Candidate>();
		for (int i = 0; i < nr; i++) {
			Candidate c = selectCandidate();
			if (c == null)
				break;
			else
				out.add(c);
		}
		return out;
	}

	public Candidate selectCandidateUsingProbabilities() {
		double sum = 0;
		for (int i = 0; i < probabilities.length; i++)
			sum += probabilities[i];
		if (sum == 0)
			return null;
		double chosen = r.nextDouble() * sum;
		sum = 0;
		for (int i = 0; i < probabilities.length; i++) {
			sum += probabilities[i];
			if (sum > chosen)
				return candidates.get(i);
		}
		throw new IllegalAccessError("Unable to choose a random number for these probabilities");
	}

	public void setProbabilities() {
		double maxDistance = 0;
		for (double d : distances)
			if (d > maxDistance)
				maxDistance = d;

		//starting value
		if (maxDistance == Double.MAX_VALUE) {
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = unpleasantness[i];
			}
		} else if (maxDistance == 0) {
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = 0;
			}
		} else {
			double x = 0.2;
			double y = 0.5;
			double a = Math.log(0.01 / y) / (0.1 - x);
			double b = Math.log((1 - 0.95) / (1 - y)) / (x - 0.3);
			for (int i = 0; i < probabilities.length; i++) {
				if (distances[i] < x * maxDistance)
					probabilities[i] *= y * Math.exp(a * (distances[i] / maxDistance - x));
				else
					probabilities[i] *= 1 - (1 - y) * Math.exp(b * (x - distances[i] / maxDistance));
			}
		}
		double lonScale = TourUtils.computeGreatCircleDistance(centerLat, centerLon, centerLat, centerLon + 1);
		double latScale = TourUtils.computeGreatCircleDistance(centerLat, centerLon, centerLat + 1, centerLon + 1);

		// Density
		double latTile = 0.05 * minLength / latScale, lonTile = 0.05 * minLength / lonScale;
		HashMap<IntPair, MaxSum> tileMaxSum = new HashMap<>();
		int pos = 0;
		for (Candidate c : candidates) {
			TourNode cNode = c.correspNode.getNode();
			IntPair cPair = new IntPair((int) (cNode.getLatitude() / latTile), (int) (cNode.getLongitude() / lonTile));
			MaxSum cMaxSum = tileMaxSum.computeIfAbsent(cPair, k -> new MaxSum(0., 0.));
			cMaxSum.max = Math.max(cMaxSum.max, probabilities[pos]);
			cMaxSum.sum += probabilities[pos];
			pos++;
		}
		pos = 0;
		for (Candidate c : candidates) {
			TourNode cNode = c.correspNode.getNode();
			MaxSum cMaxSum = tileMaxSum
					.get(new IntPair((int) (cNode.getLatitude() / latTile), (int) (cNode.getLongitude() / lonTile)));
			if (cMaxSum.sum != 0)
				probabilities[pos] = probabilities[pos] * cMaxSum.max / cMaxSum.sum;
			pos++;
		}
	}

	private class MaxSum {
		public double max;
		public double sum;

		public MaxSum(double max, double sum) {
			this.max = max;
			this.sum = sum;
		}
	}

	private class IntPair {
		private int i1;
		private int i2;

		public IntPair(int i1, int i2) {
			this.i1 = i1;
			this.i2 = i2;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + i1;
			hash = hash * 31 + i2;
			return hash;
		}

		@Override
		public boolean equals(Object aThat) {
			if (this == aThat)
				return true;
			if (!(aThat instanceof IntPair))
				return false;
			IntPair that = (IntPair) aThat;
			return i1 == that.i1 && i2 == that.i2;
		}
	}
}
