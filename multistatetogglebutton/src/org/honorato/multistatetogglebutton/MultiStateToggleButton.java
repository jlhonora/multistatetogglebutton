package org.honorato.multistatetogglebutton;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MultiStateToggleButton extends ToggleButton {

    private static final String TAG = MultiStateToggleButton.class.getSimpleName();

    private static final String KEY_BUTTON_STATES = "button_states";
    private static final String KEY_INSTANCE_STATE = "instance_state";
    List<Button> buttons;
    boolean mMultipleChoice = false;

    public MultiStateToggleButton(Context context) {
        super(context, null);
        if (this.isInEditMode()) {
            return;
        }
    }

    public MultiStateToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (this.isInEditMode()) {
            return;
        }
        int[] set = {
                android.R.attr.entries
        };
        TypedArray a = context.obtainStyledAttributes(attrs, set);
        CharSequence[] texts = a.getTextArray(0);
        a.recycle();

        setElements(texts, new boolean[texts.length]);
    }

    /**
     * If multiple choice is enabled, the user can select multiple
     * values simultaneously.
     *
     * @param enable
     */
    public void enableMultipleChoice(boolean enable) {
        this.mMultipleChoice = enable;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBooleanArray(KEY_BUTTON_STATES, getStates());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setStates(bundle.getBooleanArray(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param texts    An array of CharSequences for the buttons
     * @param selected The default value for the buttons
     */
    public void setElements(CharSequence[] texts, boolean[] selected) {
        // TODO: Add an exception
        if (texts == null || texts.length < 1) {
            Log.d(TAG, "Minimum quantity: 1");
            return;
        }

        boolean enableDefaultSelection = true;
        if (selected == null || texts.length != selected.length) {
            Log.d(TAG, "Invalid selection array");
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mainLayout = (LinearLayout) inflater.inflate(R.layout.view_multi_state_toggle_button, this, true);
        mainLayout.removeAllViews();

        this.buttons = new ArrayList<Button>();
        for (int i = 0; i < texts.length; i++) {
            Button b = null;
            if (i == 0) {
                // Add a special view when there's only one element
                if (texts.length == 1) {
                    b = (Button) inflater.inflate(R.layout.view_single_toggle_button, mainLayout, false);
                } else {
                    b = (Button) inflater.inflate(R.layout.view_left_toggle_button, mainLayout, false);
                }
            } else if (i == texts.length - 1) {
                b = (Button) inflater.inflate(R.layout.view_right_toggle_button, mainLayout, false);
            } else {
                b = (Button) inflater.inflate(R.layout.view_center_toggle_button, mainLayout, false);
            }
            b.setText(texts[i]);
            final int position = i;
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setValue(position);
                }

            });
            mainLayout.addView(b);
            if (enableDefaultSelection) {
                setButtonState(b, selected[i]);
            }
            this.buttons.add(b);
        }
        mainLayout.setBackgroundResource(R.drawable.button_section_shape);
    }

    public void setElements(CharSequence[] elements) {
        int size = elements == null ? 0 : elements.length;
        setElements(elements, new boolean[size]);
    }

    public void setElements(List<?> elements) {
        int size = elements == null ? 0 : elements.size();
        setElements(elements, new boolean[size]);
    }

    public void setElements(List<?> elements, Object selected) {
        int size = 0;
        int index = -1;
        if (elements != null) {
            size = elements.size();
            index = elements.indexOf(selected);
        }
        boolean[] selectedArray = new boolean[size];
        if (index != -1 && index < size) {
            selectedArray[index] = true;
        }
        setElements(elements, new boolean[size]);
    }

    public void setElements(List<?> texts, boolean[] selected) {
        int size = texts == null ? 0 : texts.size();
        setElements(texts.toArray(new String[size]), selected);
    }

    public void setElements(int arrayResourceId, int selectedPosition) {
        // Get resources
        String[] elements = this.getResources().getStringArray(arrayResourceId);

        // Set selected boolean array
        int size = elements == null ? 0 : elements.length;
        boolean[] selected = new boolean[size];
        if (selectedPosition >= 0 && selectedPosition < size) {
            selected[selectedPosition] = true;
        }

        // Super
        setElements(elements, selected);
    }

    public void setElements(int arrayResourceId, boolean[] selected) {
        setElements(this.getResources().getStringArray(arrayResourceId), selected);
    }

    public void setButtonState(Button button, boolean selected) {
        if (button == null) {
            return;
        }
        button.setSelected(selected);
        // TODO: Inherit these colors from primary/secondary colors
        if (selected) {
            button.setBackgroundResource(R.drawable.button_pressed);
            button.setTextAppearance(this.context, R.style.WhiteBoldText);
        } else {
            button.setBackgroundResource(R.drawable.button_not_pressed);
            button.setTextAppearance(this.context, R.style.PrimaryNormalText);
        }
    }

    public int getValue() {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setValue(int position) {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (mMultipleChoice) {
                if (i == position) {
                    Button b = buttons.get(i);
                    if (b != null) {
                        setButtonState(b, !b.isSelected());
                    }
                }
            } else {
                if (i == position) {
                    setButtonState(buttons.get(i), true);
                } else if (!mMultipleChoice) {
                    setButtonState(buttons.get(i), false);
                }
            }
        }
        super.setValue(position);
    }

    public boolean[] getStates() {
        int size = this.buttons == null ? 0 : this.buttons.size();
        boolean[] result = new boolean[size];
        for (int i = 0; i < size; i++) {
            result[i] = this.buttons.get(i).isSelected();
        }
        return result;
    }

    public void setStates(boolean[] selected) {
        if (this.buttons == null || selected == null ||
                this.buttons.size() != selected.length) {
            return;
        }
        int count = 0;
        for (Button b : this.buttons) {
            setButtonState(b, selected[count]);
            count++;
        }
    }
}
