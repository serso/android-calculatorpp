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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

public class FloatingCalculatorActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    public static final class OverlayPermissionDialog extends BaseDialogFragment {

        @Override
        protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
            final String permission = getString(R.string.cpp_permission_overlay);
            builder.setMessage(getString(R.string.cpp_missing_permission_message, permission));
            builder.setTitle(R.string.cpp_missing_permission_title);
            builder.setPositiveButton(android.R.string.ok, null);
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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final class PostNotificationsPermissionDialog extends BaseDialogFragment {

        @Override
        protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
            final String permission = getString(R.string.cpp_permission_post_notifications);
            builder.setMessage(getString(R.string.cpp_missing_permission_message, permission));
            builder.setTitle(R.string.cpp_missing_permission_title);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setNegativeButton(R.string.cpp_continue_without_permissions, null);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final FragmentActivity activity = getActivity();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    App.showSystemPermissionSettings(activity,
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    dismiss();
                    break;
                default:
                    launchFloatingCalculator(activity);
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

    @NonNull
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                launchFloatingCalculator(this);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final boolean hasOverlayPermission = FloatingCalculatorView.isOverlayPermissionGranted(this);
        final boolean hasPostNotificationsPermission = isPostNotificationsPermissionGranted();
        if (hasOverlayPermission && hasPostNotificationsPermission) {
            launchFloatingCalculator(this);
            return;
        }


        Check.isTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        if (savedInstanceState == null) {
            if (!hasOverlayPermission) {
                App.showDialog(new OverlayPermissionDialog(), "no-overlay-permission-dialog",
                        getSupportFragmentManager());
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.POST_NOTIFICATIONS)) {
                    App.showDialog(new PostNotificationsPermissionDialog(), "post-notifications-permission-dialog",
                            getSupportFragmentManager());
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                Check.shouldNotHappen();
                finish();
            }
        }
    }

    private static void launchFloatingCalculator(final Activity activity) {
        FloatingCalculatorService.show(activity);
        activity.finish();
    }

    private boolean isPostNotificationsPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true;
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED;
    }

}
