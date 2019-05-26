package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/***
 * Geojson feature collection object
 */
public class FeatureCollection {
	public Type type;
	public List<Feature> features;

	public FeatureCollection(@JsonProperty("type")Type type, @JsonProperty("features")List<Feature> features) {
		this.type = type;
		this.features = features;
	}
}