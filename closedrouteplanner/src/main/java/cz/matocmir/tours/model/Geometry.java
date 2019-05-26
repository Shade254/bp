package cz.matocmir.tours;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geometry {
	public Type type;
	public Object[] coordinates;

	public Geometry(@JsonProperty("type")Type type, @JsonProperty("coordinates")Object[] coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}
}
