package org.solovyev.android;

import android.os.Build;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

public class CalculatorTestRunner extends RobolectricGradleTestRunner {
    public static final int SUPPORTED_SDK = Build.VERSION_CODES.LOLLIPOP;

    public CalculatorTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        final String manifestFilePath = "app/src/main/AndroidManifest.xml";
        final String resourcesFilePath = "app/src/main/res";
        final String assetsFilePath = "app/src/main/assets";
        return new AndroidManifest(Fs.fileFromPath(manifestFilePath), Fs.fileFromPath(resourcesFilePath), Fs.fileFromPath(assetsFilePath)) {
            @Override
            public int getTargetSdkVersion() {
                return SUPPORTED_SDK;
            }
        };
    }
}
