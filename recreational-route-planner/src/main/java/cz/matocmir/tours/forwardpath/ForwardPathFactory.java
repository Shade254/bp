package cz.matocmir.tours.forwardpath;

import com.umotional.planningalgorithms.core.PathFactory;

public class ForwardPathFactory implements PathFactory<ForwardPath, ForwardPathLabel> {
	@Override
	public ForwardPath getPath(ForwardPathLabel label) {
		return new ForwardPath(label);
	}
}
