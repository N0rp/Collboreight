package eu.dowsing.collaboreight;

import android.content.Context;
import android.view.ActionProvider;
import android.view.View;

public class PenActionProvider extends ActionProvider {

	public PenActionProvider(Context context) {
		super(context);
		
	}

	@Override
	@Deprecated
	public View onCreateActionView() {
		
		return null;
	}
}
