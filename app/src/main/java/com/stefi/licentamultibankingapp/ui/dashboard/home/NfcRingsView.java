package com.stefi.licentamultibankingapp.ui.dashboard.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class NfcRingsView extends View {

    private Paint paint;
    private float animProgress = 0f;
    private android.os.Handler handler = new android.os.Handler();

    public NfcRingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        startAnimation();
    }

    private void startAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animProgress += 0.03f;
                if (animProgress > 1f) animProgress = 0f;
                invalidate();
                handler.postDelayed(this, 30);
            }
        }, 30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float maxRadius = Math.min(cx, cy);

        // 3 cercuri concentrice cu animatie
        for (int i = 0; i < 3; i++) {
            float phase = (animProgress + i * 0.33f) % 1f;
            float radius = phase * maxRadius;
            float alpha = 1f - phase;
            paint.setColor(Color.argb((int)(alpha * 200), 255, 255, 255));
            canvas.drawCircle(cx, cy, radius, paint);
        }

        // Punct central
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, 8f, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}