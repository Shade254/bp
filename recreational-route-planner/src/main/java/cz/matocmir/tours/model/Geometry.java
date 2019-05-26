package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/***
 * Geojson geometry object
 */
public class Geometry {
	public Type type;
	public Object[] coordinates;

	public Geometry(@JsonProperty("type")Type type, @JsonProperty("coordinates")Object[] coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}
}
