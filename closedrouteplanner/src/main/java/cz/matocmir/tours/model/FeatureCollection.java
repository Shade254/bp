package cz.matocmir.tours;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FeatureCollection {
	public Type type;
	public List<Feature> features;

	public FeatureCollection(@JsonProperty("type")Type type, @JsonProperty("features")List<Feature> features) {
		this.type = type;
		this.features = features;
	}
}