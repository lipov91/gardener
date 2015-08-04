package com.survivingwithandroid.actionbartabnavigation.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.survivingwithandroid.actionbartabnavigation.MainActivity;


public class LoginView extends View {

    private Context context;

    private ScaleGestureDetector multiGestures;
    private Matrix scale;
    private Bitmap droid;

    public LoginView(Context context, int iGraphicResourceId) {

        super(context);
        this.context = context;
        scale = new Matrix();
        GestureListener listener = new GestureListener(this);
        multiGestures = new ScaleGestureDetector(context, listener);
        droid = BitmapFactory
                .decodeResource(getResources(), iGraphicResourceId);

    }

    public void onScale(float factor) {

        scale.preScale(factor, factor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Matrix transform = new Matrix(scale);
        float width = droid.getWidth() / 2;
        float height = droid.getHeight() / 2;
        transform.postTranslate(-width, -height);
        transform.postConcat(scale);
        transform.postTranslate(width, height);
        canvas.drawBitmap(droid, transform, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean retVal = false;
        retVal = multiGestures.onTouchEvent(event);
        return retVal;
    }

    private class GestureListener implements
            ScaleGestureDetector.OnScaleGestureListener {

        LoginView view;

        public GestureListener(LoginView view) {
            this.view = view;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float scale = detector.getScaleFactor();
            view.onScale(scale);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}
