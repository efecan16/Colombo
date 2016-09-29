package com.synthform.colombo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.synthform.colombo.util.StaticUtils;

public class CircleImageView extends ImageView {
    Paint paint;

    public CircleImageView(final Context context) {
        super(context);
        paint = new Paint();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Bitmap image = StaticUtils.drawableToBitmap(getDrawable());
        if (image != null) {
            int size = Math.min(getWidth(), getHeight());
            image = ThumbnailUtils.extractThumbnail(image, size, size);

            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), image);

            roundedBitmapDrawable.setCornerRadius(size / 2);
            roundedBitmapDrawable.setAntiAlias(true);

            canvas.drawBitmap(StaticUtils.drawableToBitmap(roundedBitmapDrawable), 0, 0, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
    }
}
