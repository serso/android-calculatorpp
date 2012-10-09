package org.solovyev.android.calculator;

import android.app.PendingIntent;
import android.util.Log;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.ResponseCode;
import net.robotmedia.billing.model.Transaction;
import org.jetbrains.annotations.NotNull;

/**
* User: serso
* Date: 10/10/12
* Time: 12:27 AM
*/
class LoggingBillingObserver implements IBillingObserver {
    @Override
    public void onCheckBillingSupportedResponse(boolean supported) {
        Log.d("CalculatorppBilling", "onCheckBillingSupportedResponse");
    }

    @Override
    public void onPurchaseIntentOK(@NotNull String productId, @NotNull PendingIntent purchaseIntent) {
        Log.d("CalculatorppBilling", "onPurchaseIntentOK");
    }

    @Override
    public void onPurchaseIntentFailure(@NotNull String productId, @NotNull ResponseCode responseCode) {
        Log.d("CalculatorppBilling", "onPurchaseIntentFailure");
    }

    @Override
    public void onPurchaseStateChanged(@NotNull String productId, @NotNull Transaction.PurchaseState state) {
        Log.d("CalculatorppBilling", "onPurchaseStateChanged");
    }

    @Override
    public void onRequestPurchaseResponse(@NotNull String productId, @NotNull ResponseCode response) {
        Log.d("CalculatorppBilling", "onRequestPurchaseResponse");
    }

    @Override
    public void onTransactionsRestored() {
        Log.d("CalculatorppBilling", "onTransactionsRestored");
    }

    @Override
    public void onErrorRestoreTransactions(@NotNull ResponseCode responseCode) {
        Log.d("CalculatorppBilling", "onErrorRestoreTransactions");
    }
}
