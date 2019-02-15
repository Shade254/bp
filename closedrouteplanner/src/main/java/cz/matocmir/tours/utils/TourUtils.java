package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourNode;

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

	public static double computeEuclideanDistance(TourNode node, TourNode node1) {
		return computeEuclideanDistance(node.getLatitude(), node.getLongitude(), node.getLatitude(), node.getLongitude());
	}

	//angle = 360/(2*pi*r/distance)
	//chord_length = 2*r*sin(angle)
	//we approximate by picewise linear function f, such that (l is circumference):
	//f(0)=0, f(l/2) = l/pi (half-circle), f(l) = 0
	//we can see that this function is symetrical by l/pi => f(x)=f(l-x)
	//than mean we can further approximate by linear function, which will work if we will use shorter from two distances only
	//this linear function g if therefore: g(x) = ((l/pi) / (l/2)) * x = 2*l*x/pi*l = 2*x/pi
	//this linear function is independent of circumference
	public static double getExpectedDisplacement(double distanceByCircumference){
		return 2*distanceByCircumference/Math.PI;
	}

	public static double computeGreatCircleDistance(TourNode node, TourNode node1) {
		return computeGreatCircleDistance(node.getLatitude(), node.getLongitude(), node1.getLatitude(), node1.getLongitude());
	}
}
