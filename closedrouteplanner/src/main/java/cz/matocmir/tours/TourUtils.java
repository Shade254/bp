package cz.matocmir.tours;

public class TourUtils {

	public static double computeEuclideanDistance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt((fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY));
	}

	public static double computeGreatCircleDistance(double fromLat, double fromLon, double toLat, double toLon) {
		double earthRadius = 6371000.0D;
		double dLat = Math.toRadians(toLat - fromLat);
		double dLng = Math.toRadians(toLon - fromLon);
		double a = Math.sin(dLat / 2.0D) * Math.sin(dLat / 2.0D) + Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(toLat)) * Math.sin(dLng / 2.0D) * Math.sin(dLng / 2.0D);
		double c = 2.0D * Math.atan2(Math.sqrt(a), Math.sqrt(1.0D - a));
		float dist = (float)(earthRadius * c);
		return (double)dist;
	}
}
