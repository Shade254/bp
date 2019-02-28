package cz.matocmir.tours.point2point;

import com.umotional.planningalgorithms.core.PathFactory;

public class Point2PointPathFactory implements PathFactory<Point2PointPath, Point2PointLabel> {

	@Override
	public Point2PointPath getPath(Point2PointLabel label) {
		return new Point2PointPath(label);
	}
}
