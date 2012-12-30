// Copyright (C) 2009-2010 Mihai Preda

package arity.calculator;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Build;

import java.nio.ShortBuffer;
import java.io.*;

class Util {
    public static final int SDK_VERSION = getSdkVersion();

    private static int getSdkVersion() {
        try {
            return Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            Calculator.log("invalid SDK " + Build.VERSION.SDK);
            return 3;
        }
    }

    static String saveBitmap(Bitmap bitmap, String dir, String baseName) {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File pictureDir = new File(sdcard, dir);
            pictureDir.mkdirs();
            File f = null;
            for (int i = 1; i < 200; ++i) {
                String name = baseName + i + ".png";
                f = new File(pictureDir, name);
                if (!f.exists()) {
                    break;
                }
            }
            if (!f.exists()) {
                String name = f.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(name);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                return name;
            }
        } catch (Exception e) {
            Calculator.log("exception saving screenshot: " + e);
        } finally {
            /*
            if (fos != null) {
                fos.close();
            }
            */
        }
        return null;
    }

    static void bitmapBGRtoRGB(Bitmap bitmap, int width, int height) {
        int size = width * height;
        short data[] = new short[size];
        ShortBuffer buf = ShortBuffer.wrap(data);
        bitmap.copyPixelsToBuffer(buf);
        for (int i = 0; i < size; ++i) {
            //BGR-565 to RGB-565
            short v = data[i];
            data[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
        }
        buf.rewind();
        bitmap.copyPixelsFromBuffer(buf);
    }
}
