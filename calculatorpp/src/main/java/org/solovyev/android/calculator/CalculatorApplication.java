package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.helper.DefaultBillingObserver;
import net.robotmedia.billing.model.BillingDB;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.history.AndroidCalculatorHistory;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
/*@ReportsCrashes(formKey = "dEhDaW1nZU1qcFdsVUpiSnhON0c0ZHc6MQ",
        mode = ReportingInteractionMode.TOAST)*/
@ReportsCrashes(formKey = "",
        mailTo = "se.solovyev+programming+calculatorpp+crashes@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crashed,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogText = R.string.crash_dialog_text)
public class CalculatorApplication extends android.app.Application {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    public static final String FACEBOOK_APP_URL = "http://www.facebook.com/calculatorpp";

    public static final String AD_FREE_PRODUCT_ID = "ad_free";
    public static final String AD_FREE_P_KEY = "org.solovyev.android.calculator_ad_free";

    public static final String ADMOB_USER_ID = "a14f02cf9c80cbc";

    @NotNull
    private static CalculatorApplication instance;

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    public CalculatorApplication() {
        instance = this;
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    @Override
    public void onCreate() {
        ACRA.init(this);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        CalculatorPreferences.setDefaultValues(preferences);

        setTheme(preferences);

        super.onCreate();

        final AndroidCalculator calculator = new AndroidCalculator();

        CalculatorLocatorImpl.getInstance().init(calculator,
                new AndroidCalculatorEngine(this),
                new AndroidCalculatorClipboard(this),
                new AndroidCalculatorNotifier(this),
                new AndroidCalculatorHistory(this, calculator));

        CalculatorLocatorImpl.getInstance().getCalculator().init();

        AdsController.getInstance().init(ADMOB_USER_ID, AD_FREE_PRODUCT_ID, new BillingController.IConfiguration() {

            @Override
            public byte[] getObfuscationSalt() {
                return new byte[]{81, -114, 32, -127, -32, -104, -40, -15, -47, 57, -13, -41, -33, 67, -114, 7, -11, 53, 126, 82};
            }

            @Override
            public String getPublicKey() {
                return CalculatorSecurity.getPK();
            }
        });

        BillingController.registerObserver(new DefaultBillingObserver(this, null));

        // init billing controller
        new Thread(new Runnable() {
            @Override
            public void run() {
                BillingDB.init(CalculatorApplication.this);
                BillingController.checkBillingSupported(CalculatorApplication.this);
                AdsController.getInstance().isAdFree(CalculatorApplication.this);
            }
        }).start();
    }

    private void setTheme(@NotNull SharedPreferences preferences) {
        final CalculatorPreferences.Gui.Theme theme = CalculatorPreferences.Gui.getTheme(preferences);
        setTheme(theme.getThemeId());
    }

    @NotNull
    public CalculatorActivityHelper createActivityHelper(int layoutResId, @NotNull String logTag) {
       return new CalculatorActivityHelperImpl(layoutResId, logTag);
    }

    @NotNull
    public CalculatorFragmentHelper createFragmentHelper(int layoutId) {
        return new CalculatorFragmentHelperImpl(layoutId);
    }

    @NotNull
     public CalculatorFragmentHelper createFragmentHelper(int layoutId, int titleResId) {
        return new CalculatorFragmentHelperImpl(layoutId, titleResId);
    }
    @NotNull
    public CalculatorFragmentHelper createFragmentHelper(int layoutId, int titleResId, boolean listenersOnCreate) {
        return new CalculatorFragmentHelperImpl(layoutId, titleResId, listenersOnCreate);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    @NotNull
    public static CalculatorApplication getInstance() {
        return instance;
    }

    public static void likeButtonPressed(@NotNull final Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_APP_URL)));
    }
}
