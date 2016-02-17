/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.floating;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

public class FloatingCalculatorActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    public static final class MyFragment extends BaseDialogFragment {

        @Override
        protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
            final String permission = getString(R.string.cpp_permission_overlay);
            builder.setMessage(getString(R.string.cpp_missing_permission_msg, permission));
            builder.setTitle(R.string.cpp_missing_permission_title);
            builder.setPositiveButton(R.string.ok, this);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    final FragmentActivity activity = getActivity();
                    App.showSystemPermissionSettings(activity,
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    dismiss();
                    break;
                default:
                    super.onClick(dialog, which);
                    break;
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FloatingCalculatorView.isOverlayPermissionGranted(this)) {
            FloatingCalculatorService.show(this);
            finish();
            return;
        }

        Check.isTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        if (savedInstanceState == null) {
            App.showDialog(new MyFragment(), "no-overlay-permission-dialog",
                    getSupportFragmentManager());
        }
    }

}
