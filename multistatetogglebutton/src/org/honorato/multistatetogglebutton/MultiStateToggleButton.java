package org.honorato.multistatetogglebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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

    List<View> buttons;
    boolean mMultipleChoice = false;
    private LinearLayout mainLayout;
    private boolean mFontPadding;
    private String mTextSize;
    private String mButtonPadding;
    private String[]  mButtonPaddingArray = new String[4];

    public MultiStateToggleButton(Context context) {
        super(context, null);
    }

    public MultiStateToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateToggleButton, 0, 0);


        mTextSize = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize");
        mFontPadding = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android","includeFontPadding",true);
        mButtonPadding =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "padding");
        mButtonPaddingArray = new String[]{mButtonPadding,mButtonPadding,mButtonPadding,mButtonPadding};
        //Overwrite padding elements if present
        mButtonPaddingArray[0] =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "paddingLeft");
        mButtonPaddingArray[1] =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "paddingTop");
        mButtonPaddingArray[2] =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "paddingRight");
        mButtonPaddingArray[3] =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "paddingBottom");

        try {
            CharSequence[] texts = a.getTextArray(R.styleable.MultiStateToggleButton_values);
            colorPressed = a.getColor(R.styleable.MultiStateToggleButton_mstbPrimaryColor, 0);
            colorNotPressed = a.getColor(R.styleable.MultiStateToggleButton_mstbSecondaryColor, 0);
            colorPressedText = a.getColor(R.styleable.MultiStateToggleButton_mstbColorPressedText, 0);
            colorPressedBackground = a.getColor(R.styleable.MultiStateToggleButton_mstbColorPressedBackground, 0);
            pressedBackgroundResource = a.getResourceId(R.styleable.MultiStateToggleButton_mstbColorPressedBackgroundResource, 0);
            colorNotPressedText = a.getColor(R.styleable.MultiStateToggleButton_mstbColorNotPressedText, 0);
            colorNotPressedBackground = a.getColor(R.styleable.MultiStateToggleButton_mstbColorNotPressedBackground, 0);
            notPressedBackgroundResource = a.getResourceId(R.styleable.MultiStateToggleButton_mstbColorNotPressedBackgroundResource, 0);

            setElements(texts, null, new boolean[texts.length]);
        } finally {
            a.recycle();
        }
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
     * Set the enabled state of this MultiStateToggleButton, including all of its child buttons.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    @Override
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param texts            An array of CharSequences for the buttons
     * @param imageResourceIds an optional icon to show, either text, icon or both needs to be set.
     * @param selected         The default value for the buttons
     */
    public void setElements(CharSequence[] texts, int[] imageResourceIds, boolean[] selected) {
        final int textCount = texts != null ? texts.length : 0;
        final int iconCount = imageResourceIds != null ? imageResourceIds.length : 0;
        final int elementCount = Math.max(textCount, iconCount);
        if (elementCount == 0) {
            throw new IllegalArgumentException("neither texts nor images are setup");
        }

        boolean enableDefaultSelection = true;
        if (selected == null || elementCount != selected.length) {
            Log.d(TAG, "Invalid selection array");
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mainLayout == null) {
            mainLayout = (LinearLayout) inflater.inflate(R.layout.view_multi_state_toggle_button, this, true);
        }
        mainLayout.removeAllViews();

        this.buttons = new ArrayList<>();
        for (int i = 0; i < elementCount; i++) {
            Button b;

            if (i == 0) {
                // Add a special view when there's only one element
                if (elementCount == 1) {
                    b = (Button) inflater.inflate(R.layout.view_single_toggle_button, mainLayout, false);
                } else {
                    b = (Button) inflater.inflate(R.layout.view_left_toggle_button, mainLayout, false);
                }
            } else if (i == elementCount - 1) {
                b = (Button) inflater.inflate(R.layout.view_right_toggle_button, mainLayout, false);
            } else {
                b = (Button) inflater.inflate(R.layout.view_center_toggle_button, mainLayout, false);
            }
            b.setText(texts != null ? texts[i] : "");
            b.setIncludeFontPadding(mFontPadding);
            setTextSize(b);
            setButtonPaddingBottom(b);
            setButtonPaddingTop(b);
            //Left and right padding only when meaningfull
            if(i == 0) setButtonPaddingLeft(b);
            if(i == elementCount-1) setButtonPaddingRight(b);
            if (imageResourceIds != null && imageResourceIds[i] != 0) {
                b.setCompoundDrawablesWithIntrinsicBounds(imageResourceIds[i], 0, 0, 0);
            }
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


    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param buttons  the array of button views to use
     * @param selected The default value for the buttons
     */
    public void setButtons(View[] buttons, boolean[] selected) {
        final int elementCount = buttons.length;
        if (elementCount == 0) {
            throw new IllegalArgumentException("neither texts nor images are setup");
        }

        boolean enableDefaultSelection = true;
        if (selected == null || elementCount != selected.length) {
            Log.d(TAG, "Invalid selection array");
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mainLayout == null) {
            mainLayout = (LinearLayout) inflater.inflate(R.layout.view_multi_state_toggle_button, this, true);
        }
        mainLayout.removeAllViews();

        this.buttons = new ArrayList<>();
        for (int i = 0; i < elementCount; i++) {
            View b = buttons[i];
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
        setElements(elements, null, new boolean[size]);
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
        setElements(elements, selectedArray);
    }

    public void setElements(List<?> texts, boolean[] selected) {
        if (texts == null) {
            texts = new ArrayList<>(0);
        }
        int size = texts.size();
        setElements(texts.toArray(new String[size]), null, selected);
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
        setElements(elements, null, selected);
    }

    public void setElements(int arrayResourceId, boolean[] selected) {
        setElements(this.getResources().getStringArray(arrayResourceId), null, selected);
    }

    public void setButtonState(View button, boolean selected) {
        if (button == null) {
            return;
        }
        button.setSelected(selected);
        button.setBackgroundResource(selected ? R.drawable.button_pressed : R.drawable.button_not_pressed);
        if (colorNotPressed != 0 || colorPressed != 0) {
            button.setBackgroundColor(selected ? colorPressed : colorNotPressed);
        } else if(colorNotPressedBackground != 0 || colorNotPressedBackground != 0) {
          button.setBackgroundColor(selected ? colorPressedBackground : colorNotPressedBackground);
        }
        if (button instanceof Button) {
            int style = selected ? R.style.WhiteBoldText : R.style.PrimaryNormalText;
            ((AppCompatButton) button).setTextAppearance(this.getContext(), style);
            if (colorPressed != 0 || colorNotPressed != 0) {
                ((AppCompatButton) button).setTextColor(!selected ? colorPressed : colorNotPressed);
            } else if(colorPressedText != 0 || colorNotPressedText != 0) {
              ((AppCompatButton) button).setTextColor(selected ? colorPressedText : colorNotPressedText);
            }
            if(pressedBackgroundResource != 0 || notPressedBackgroundResource != 0) {
              button.setBackgroundResource(selected ? pressedBackgroundResource : notPressedBackgroundResource);
            }
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
                    View b = buttons.get(i);
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
        for (View b : this.buttons) {
            setButtonState(b, selected[count]);
            count++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColors(int colorPressed, int colorNotPressed) {
        super.setColors(colorPressed, colorNotPressed);
        refresh();
    }

    private void refresh() {
        boolean[] states = getStates();
        for (int i = 0; i < states.length; i++) {
            setButtonState(buttons.get(i), states[i]);
        }
    }


    private void setTextSize(Button b){
        if(mTextSize == null || mTextSize.isEmpty()){
                return;
        }
        if(mTextSize.contains("dip")){
            b.setTextSize(TypedValue.COMPLEX_UNIT_DIP,Integer.getInteger(mTextSize.substring(0,mTextSize.length()-5)));
        }
        else if(mTextSize.contains("dp")){
            b.setTextSize(TypedValue.COMPLEX_UNIT_DIP,Integer.getInteger(mTextSize.substring(0,mTextSize.length()-4)));
        }
        else if(mTextSize.contains("sp")){
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(mTextSize.substring(0,mTextSize.length()-4)));
        }
    }


    private void setButtonPaddingLeft(Button b){
        if(mButtonPaddingArray[0] == null || mButtonPaddingArray[0].isEmpty()){
            return;
        }
        float scale = getResources().getDisplayMetrics().density;

        if(mButtonPaddingArray[0].contains("dip")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[0].substring(0,mButtonPaddingArray[0].length()-5))*scale + 0.5f);
            b.setPadding(dpAsPixels, (int) b.getPaddingTop(), (int) b.getPaddingRight(), (int) b.getPaddingBottom());
        }
        else if(mButtonPaddingArray[0].contains("dp")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[0].substring(0,mButtonPaddingArray[0].length()-4))*scale + 0.5f);
            b.setPadding(dpAsPixels, (int) b.getPaddingTop(), (int) b.getPaddingRight(), (int) b.getPaddingBottom());
        }
    }

    private void setButtonPaddingTop(Button b){
        if(mButtonPaddingArray[1] == null || mButtonPaddingArray[1].isEmpty()){
            return;
        }
        float scale = getResources().getDisplayMetrics().density;

        if(mButtonPaddingArray[1].contains("dip")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[1].substring(0,mButtonPaddingArray[1].length()-5))*scale + 0.5f);
            b.setPadding(b.getPaddingLeft(), dpAsPixels, (int) b.getPaddingRight(), (int) b.getPaddingBottom());
        }
        else if(mButtonPaddingArray[1].contains("dp")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[1].substring(0,mButtonPaddingArray[1].length()-4))*scale + 0.5f);
            b.setPadding(b.getPaddingLeft(), dpAsPixels, (int) b.getPaddingRight(), (int) b.getPaddingBottom());
        }
    }

    private void setButtonPaddingRight(Button b){
        if(mButtonPaddingArray[2] == null || mButtonPaddingArray[2].isEmpty()){
            return;
        }
        float scale = getResources().getDisplayMetrics().density;

        if(mButtonPaddingArray[2].contains("dip")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[2].substring(0,mButtonPaddingArray[2].length()-5))*scale + 0.5f);
            b.setPadding((int) b.getPaddingLeft(), (int) b.getPaddingTop(), dpAsPixels, (int) b.getPaddingBottom());
        }
        else if(mButtonPaddingArray[2].contains("dp")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[2].substring(0,mButtonPaddingArray[2].length()-4))*scale + 0.5f);
            b.setPadding((int) b.getPaddingLeft(), (int) b.getPaddingTop(), dpAsPixels, (int) b.getPaddingBottom());
        }
    }

    private void setButtonPaddingBottom(Button b){
        if(mButtonPaddingArray[3] == null || mButtonPaddingArray[3].isEmpty()){
            return;
        }
        float scale = getResources().getDisplayMetrics().density;

        if(mButtonPaddingArray[3].contains("dip")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[3].substring(0,mButtonPaddingArray[3].length()-5))*scale + 0.5f);
            b.setPadding((int) b.getPaddingLeft(), (int) b.getPaddingTop(), (int) b.getPaddingRight(), dpAsPixels);
        }
        else if(mButtonPaddingArray[3].contains("dp")){
            int dpAsPixels = (int) (Integer.getInteger(mButtonPaddingArray[3].substring(0,mButtonPaddingArray[3].length()-4))*scale + 0.5f);
            b.setPadding((int) b.getPaddingLeft(), (int) b.getPaddingTop(), (int) b.getPaddingRight(), dpAsPixels);
        }
    }
}
