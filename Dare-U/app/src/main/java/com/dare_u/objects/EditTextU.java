package com.dare_u.objects;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

import com.dare_u.dare_u.R;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class EditTextU extends EditText {

    private Context context;
    private AttributeSet attrs;

    public EditTextU(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EditTextU(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public EditTextU(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    /**
     * Init method to charge the properties of the View.
     */
    public void init() {
        getAttributes();
        super.setBackgroundResource(R.drawable.edittext_white);
        super.setTextColor(Color.BLACK);
    }

    /**
     * Initialize the attributes of the View.
     */
    private void getAttributes() {
    }
}
