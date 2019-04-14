package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.umotional.geotools.geojson.Feature;
import com.umotional.geotools.geojson.FeatureCollection;
import com.umotional.geotools.geojson.Type;
import cz.matocmir.tours.utils.IOUtils;
import cz.matocmir.tours.utils.TourUtils;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tour {
	List<TourEdge> edges;
	Feature turningPointLast;
	Feature turningPointPlanned;
	double length;
	double roundness;
	double totalCost;

	public Tour(List<TourEdge> path, TourNode turningPointLast, TourNode turningPointPlanned, double totalCost) {
		this.turningPointLast = IOUtils.nodeToFeature(turningPointLast);
		this.turningPointPlanned = IOUtils.nodeToFeature(turningPointPlanned);
		this.length = TourUtils.getPathLength(path);
		this.roundness = TourUtils.tourPenalty(path, this.length);
		this.totalCost = totalCost;
		this.edges = path;
	}

	@JsonIgnore
	public List<TourEdge> getOriginalEdges(){
		return edges;
	}

	public FeatureCollection getPath() {
		return new FeatureCollection(Type.FeatureCollection,
				this.edges.stream().map(IOUtils::edgeToFeature).collect(Collectors.toList()));
	}


	public Feature getTurningPointLast() {
		return turningPointLast;
	}

	public void setTurningPointLast(Feature turningPointLast) {
		this.turningPointLast = turningPointLast;
	}

	public Feature getTurningPointPlanned() {
		return turningPointPlanned;
	}

	public void setTurningPointPlanned(Feature turningPointPlanned) {
		this.turningPointPlanned = turningPointPlanned;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getRoundness() {
		return roundness;
	}

	public void setRoundness(double roundness) {
		this.roundness = roundness;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
}
