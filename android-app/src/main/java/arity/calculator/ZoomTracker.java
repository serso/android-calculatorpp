// Copyright (C) 2010 Mihai Preda

package arity.calculator;

class ZoomTracker {
    private float sx1, sy1, sx2, sy2;
    private float initialDist;
    private float initialValue;

    float value;
    float moveX, moveY;

    void start(float value, float x1, float y1, float x2, float y2) {
        sx1 = x1;
        sy1 = y1;
        sx2 = x2;
        sy2 = y2;
        initialDist = distance(x1, y1, x2, y2);
        initialValue = value;
    }

    boolean update(float x1, float y1, float x2, float y2) {
        final float LIMIT = 1.5f;
        if (Math.abs(x1 - sx1) < LIMIT && Math.abs(y1 - sy1) < LIMIT && 
            Math.abs(x2 - sx2) < LIMIT && Math.abs(y2 - sy2) < LIMIT) {
            return false;
        }
        moveX = common(x1, sx1, x2, sx2);
        moveY = common(y1, sy1, y2, sy2);
        float dist = distance(x1, y1, x2, y2);
        value = initialDist / dist * initialValue;
        sx1 = x1;
        sx2 = x2;
        sy1 = y1;
        sy2 = y2;
        return true;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        final float dx = x1-x2;
        final float dy = y1-y2;
        // return (float) Math.sqrt(dx*dx+dy*dy);
        return Math.max(dx*dx, dy*dy);
    }

    private float common(float x1, float sx1, float x2, float sx2) {
        float dx1 = x1 - sx1;
        float dx2 = x2 - sx2;
        return (dx1 < 0 && dx2 < 0) ? Math.max(dx1, dx2) :
            (dx1 > 0 && dx2 > 0) ? Math.min(dx1, dx2):
            0;
    }
}
