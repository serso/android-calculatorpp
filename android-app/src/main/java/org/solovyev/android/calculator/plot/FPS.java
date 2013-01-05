package org.solovyev.android.calculator.plot;

class FPS {
    private int drawCnt;
    private long lastTime;
    private int fps;

    boolean incFrame() {
        if (--drawCnt > 0) {
            return false;
        }
        drawCnt = 100;
        long now = System.currentTimeMillis();
        fps = Math.round(100000f / (now - lastTime));
        lastTime = now;
        return true;
    }

    int getValue() {
        return fps;
    }    
}
