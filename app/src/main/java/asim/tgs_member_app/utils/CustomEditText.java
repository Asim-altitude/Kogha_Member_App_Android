package asim.tgs_member_app.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import asim.tgs_member_app.R;


/**
 * Created by PC-GetRanked on 7/11/2018.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
        init(null);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {

        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);

            String fontName = a.getString(R.styleable.CustomEditText_font_text);



            try {

                if (fontName != null) {

                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font_text/" + fontName);

                    setTypeface(myTypeface);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }



            a.recycle();

        }

    }



}
