package cz.matocmir.tours.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/***
 * Response to a planning request (TourRequest)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourResponse {
	Tour[] tours;
	long responseTime;

	public Tour[] getTours() {
		return tours;
	}

	public void setTours(Tour[] tours) {
		this.tours = tours;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long reponseTime) {
		this.responseTime = reponseTime;
	}
}
