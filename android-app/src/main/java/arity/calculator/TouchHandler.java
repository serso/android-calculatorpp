// Copyright (C) 2009-2010 Mihai Preda

package arity.calculator;

import android.view.MotionEvent;
import android.view.VelocityTracker;

class TouchHandler {
    static interface TouchHandlerInterface {
        void onTouchDown(float x, float y);
        void onTouchMove(float x, float y);
        void onTouchUp(float x, float y);    
        void onTouchZoomDown(float x1, float y1, float x2, float y2);
        void onTouchZoomMove(float x1, float y1, float x2, float y2);
    }

    VelocityTracker velocityTracker = VelocityTracker.obtain();

    private boolean isAfterZoom;
    private TouchHandlerInterface listener;

    TouchHandler(TouchHandlerInterface listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Calculator.log("touch " + event + ' ' + event.getPointerCount() + event.getPointerId(0));

        int fullAction = event.getAction();
        int action  = fullAction & MotionEvent.ACTION_MASK;
        int pointer = (fullAction & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        float x = event.getX();
        float y = event.getY();
        int nPoints = MotionEventWrap.getPointerCount(event);

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            isAfterZoom = false;
            velocityTracker.clear();
            velocityTracker.addMovement(event);
            listener.onTouchDown(x, y);
            break;

        case MotionEvent.ACTION_MOVE:
            if (nPoints == 1) {
                if (isAfterZoom) {
                    velocityTracker.clear();
                    listener.onTouchDown(x, y);
                    isAfterZoom = false;
                }
                velocityTracker.addMovement(event);
                listener.onTouchMove(x, y);
            } else if (nPoints == 2) {
                listener.onTouchZoomMove(x, y, MotionEventWrap.getX(event, 1), MotionEventWrap.getY(event, 1));
            }
            break;

        case MotionEvent.ACTION_UP:
            velocityTracker.addMovement(event);
            velocityTracker.computeCurrentVelocity(1000);
            listener.onTouchUp(x, y);
            break;

        case MotionEvent.ACTION_POINTER_DOWN:
            if (nPoints == 2) {
                listener.onTouchZoomDown(x, y, MotionEventWrap.getX(event, 1), MotionEventWrap.getY(event, 1));
            }
            break;

        case MotionEvent.ACTION_POINTER_UP:
            if (nPoints == 2) {
                isAfterZoom = true;
            }
            break;
        }
        return true;
    }

}
