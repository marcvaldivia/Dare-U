package com.dare_u.objects;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import com.dare_u.dare_u.R;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class ButtonU extends Button {

    private Context context;
    private AttributeSet attrs;

    private boolean white;
    private boolean red;

    public ButtonU(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ButtonU(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public ButtonU(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public void init() {
        getAttributes();
        if (white) super.setBackgroundResource(R.drawable.button_grey);
        else if (red) {
            super.setBackgroundResource(R.drawable.button_red);
            super.setTextColor(Color.WHITE);
        }
        else {
            super.setBackgroundResource(R.drawable.button_blue);
            super.setTextColor(Color.WHITE);
        }
    }

    /**
     * Initialize the attributes of the View.
     */
    private void getAttributes() {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonU,
                0, 0);
        try {
            white = typedArray.getBoolean(R.styleable.ButtonU_buttonWhite, false);
            red = typedArray.getBoolean(R.styleable.ButtonU_buttonRed, false);
        } finally {
            typedArray.recycle();
        }
    }

}
