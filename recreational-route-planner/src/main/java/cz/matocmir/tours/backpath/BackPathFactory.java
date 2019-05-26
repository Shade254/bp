package cz.matocmir.tours.backpath;

import com.umotional.planningalgorithms.core.PathFactory;

public class BackPathFactory implements PathFactory<BackPath, BackPathLabel> {

	@Override
	public BackPath getPath(BackPathLabel label) {
		return new BackPath(label);
	}
}
