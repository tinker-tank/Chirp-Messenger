/*------------------------------------------------------------------------------
 *
 *  This file is part of the Chirp SDK for Android.
 *  For full information on usage and licensing, see http://chirp.io/
 *
 *  Copyright © 2011-2017, Asio Ltd.
 *  All rights reserved.
 *
 *----------------------------------------------------------------------------*/

package io.chirp.chirpshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import io.chirp.sdk.model.ChirpAudioState;
import io.chirp.sdk.model.Chirp;

public class ShapeView extends ImageView {

    private static final int MAX_CLICK_DURATION = 100;
    private static final int SHAPE_DIMENSION = 60;
    private static final int SHAPE_WIDTH_PX = 120;
    private static final int SHAPE_HEIGHT_PX = 120;
    public static final int SHAPE_ROTATION = 40;

    protected static final int[] SHAPE_ICON = {
            R.drawable.icon_img,
            R.drawable.icon_video,
            R.drawable.icon_music,
            R.drawable.icon_wallet,
            R.drawable.icon_folder
    };

    MainActivity activity;
    RelativeLayout parent;

    int shapeWidth;
    int shapeHeight;
    int _xDelta;
    int _yDelta;

    private long startTouchTime;

    public ShapeView(Context context, int x, int y, int color, int rotation) {
        super(context);
        activity = (MainActivity) context;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        shapeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHAPE_DIMENSION, displayMetrics);
        shapeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHAPE_DIMENSION, displayMetrics);

        setRotation((float) rotation - SHAPE_ROTATION / 2);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), SHAPE_ICON[color]);
        setImageBitmap(Bitmap.createScaledBitmap(bm, shapeWidth, shapeHeight, true));
        setTag(color);
        setBackgroundColor(0x0);
        setScaleType(ImageView.ScaleType.FIT_CENTER);

        parent = (RelativeLayout) activity.findViewById(R.id.relativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(shapeWidth, shapeHeight);
        params.leftMargin = x * displayMetrics.widthPixels / 100 - shapeWidth / 2;
        params.topMargin = y * displayMetrics.heightPixels / 100 - shapeHeight / 2;
        params.rightMargin = -250;
        params.bottomMargin = -250;
        parent.addView(this, params);
        
        Animation grow = AnimationUtils.loadAnimation(getContext(), R.anim.grow);
        startAnimation(grow);
    }

    protected void onClicked() {
        /*------------------------------------------------------------------------------
         * When a shape is tapped, generate a 10-character chirp identifier
         * that encapsulates the shapes's position, colour and rotation.
         *----------------------------------------------------------------------------*/
        if (activity.chirpSDK.getChirpAudioState() == ChirpAudioState.ChirpAudioStateChirping || activity.chirpSDK.getChirpAudioState() == ChirpAudioState.ChirpAudioStateReceiving) {
            return;
        }

        String identifier = getShapeIdentifier();
        activity.chirpSDK.chirp(new Chirp(identifier));
        final ShapeView shapeView = this;

        Animation shrink = AnimationUtils.loadAnimation(getContext(), R.anim.shrink);
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                shapeView.parent.post(new Runnable() {
                    @Override
                    public void run() {
                        shapeView.parent.removeView(shapeView);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(shrink);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        parent.bringChildToFront(this);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startTouchTime = Calendar.getInstance().getTimeInMillis();
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this
                        .getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                long touchDuration = Calendar.getInstance().getTimeInMillis() - startTouchTime;

                if (touchDuration < MAX_CLICK_DURATION) {
                    onClicked();
                    return false;
                }
        }
        return true;
    }

    protected String getShapeIdentifier() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int shapeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHAPE_WIDTH_PX, displayMetrics);
        int shapeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHAPE_HEIGHT_PX, displayMetrics);

        int x = (int) (getX() + shapeWidth / 2) * 100 / displayMetrics.widthPixels;
        int y = (int) (getY() + shapeHeight / 2) * 100 / displayMetrics.heightPixels;
        int c = (int) getTag();
        int r = (int) getRotation() + SHAPE_ROTATION / 2;
        Log.d(MainActivity.TAG, String.format("getShapeIdentifier: %d - %d - %d - %d", x, y, c, r));
        return String.format("%03d%03d%01d%03d", x, y, c, r);
    }
}
