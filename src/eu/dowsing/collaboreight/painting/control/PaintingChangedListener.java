package eu.dowsing.collaboreight.painting.control;

import eu.dowsing.collaboreight.painting.model.ScaledPath;


public interface PaintingChangedListener {
	
	void onRedrawPainting();
	
	void onPathCommited(ScaledPath path);
}
