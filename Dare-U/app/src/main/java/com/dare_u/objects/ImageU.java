package com.dare_u.objects;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class ImageU extends VideoView {

    private Context context;
    private AttributeSet attrs;

    public ImageU(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ImageU(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public ImageU(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    /**
     * Init method to charge the properties of the View.
     */
    public void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open the image viewer
            }
        });
    }

}
