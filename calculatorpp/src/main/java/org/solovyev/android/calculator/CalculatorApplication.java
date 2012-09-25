package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import net.robotmedia.billing.BillingController;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.history.AndroidCalculatorHistory;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
public class CalculatorApplication extends android.app.Application {

    private static final String paypalDonateUrl = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=se%2esolovyev%40gmail%2ecom&lc=RU&item_name=Android%20Calculator&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";

    public static final String AD_FREE_PRODUCT_ID = "ad_free";
    public static final String AD_FREE_P_KEY = "org.solovyev.android.calculator_ad_free";

    public static final String ADMOB_USER_ID = "a14f02cf9c80cbc";
    public static final String REMOTE_STACK_TRACE_URL = "http://calculatorpp.com/crash_reports/upload.php";

    @NotNull
    private static CalculatorApplication instance;

    public CalculatorApplication() {
        instance = this;
    }

    @NotNull
    public static CalculatorApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
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

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        CalculatorPreferences.setDefaultValues(preferences);
    }

    public static void showDonationDialog(@NotNull final Context context) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.donate, null);

        final TextView donate = (TextView) view.findViewById(R.id.donateText);
        donate.setMovementMethod(LinkMovementMethod.getInstance());

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(true)
                .setNegativeButton(R.string.c_cancel, null)
                .setPositiveButton(R.string.c_donate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(paypalDonateUrl));
                        context.startActivity(i);
                    }
                })
                .setView(view);

        builder.create().show();
    }

    public static void registerOnRemoteStackTrace() {
        //Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(null, REMOTE_STACK_TRACE_URL));
    }
}
