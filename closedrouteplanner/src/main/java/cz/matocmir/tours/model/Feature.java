package cz.matocmir.tours;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Feature {
	public Type type;
	public Geometry geometry;
	public Map<String, Object> properties;

	public Feature(@JsonProperty("type") Type type, @JsonProperty("geometry") Geometry geometry,
			@JsonProperty("properties") Map<String, Object> properties) {
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}
}
