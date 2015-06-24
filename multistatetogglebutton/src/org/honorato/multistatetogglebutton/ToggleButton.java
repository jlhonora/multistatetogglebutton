package org.honorato.multistatetogglebutton;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class ToggleButton extends LinearLayout {

	public interface OnValueChangedListener {
		// TODO: Add this callback:
		// public void onValueChanged(int value, boolean selected);
		public void onValueChanged(int value);
	}
	
	OnValueChangedListener listener;
	Context context;
	
	public ToggleButton(Context context) {
		super(context, null);
		this.context = context;
	}
	
    public ToggleButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }
	
	public void setOnValueChangedListener(OnValueChangedListener l) {
		this.listener = l;
	}
	
	public void setValue(int value) {
		if(this.listener != null) {
			listener.onValueChanged(value);
		}
	}
}
