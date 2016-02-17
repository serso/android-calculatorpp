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

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.prefs.AbstractPreference;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloatingCalculatorViewState implements Parcelable {

    private static final String TAG = FloatingCalculatorViewState.class.getSimpleName();
    private int width;
    private int height;
    private int x;
    private int y;
    public static final Parcelable.Creator<FloatingCalculatorViewState> CREATOR = new Parcelable.Creator<FloatingCalculatorViewState>() {
        public FloatingCalculatorViewState createFromParcel(@Nonnull Parcel in) {
            return FloatingCalculatorViewState.fromParcel(in);
        }

        public FloatingCalculatorViewState[] newArray(int size) {
            return new FloatingCalculatorViewState[size];
        }
    };

    private FloatingCalculatorViewState() {
    }

    @Nonnull
    private static FloatingCalculatorViewState fromParcel(@Nonnull Parcel in) {
        final FloatingCalculatorViewState result = new FloatingCalculatorViewState();
        result.width = in.readInt();
        result.height = in.readInt();
        result.x = in.readInt();
        result.y = in.readInt();
        return result;
    }

    @Nonnull
    public static FloatingCalculatorViewState createDefault() {
        return create(200, 400, 0, 0);
    }

    @Nonnull
    public static FloatingCalculatorViewState create(int width, int height, int x, int y) {
        final FloatingCalculatorViewState result = new FloatingCalculatorViewState();
        result.width = width;
        result.height = height;
        result.x = x;
        result.y = y;
        return result;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@Nonnull Parcel out, int flags) {
        out.writeInt(width);
        out.writeInt(height);
        out.writeInt(x);
        out.writeInt(y);
    }

    @Override
    public String toString() {
        return "CalculatorOnscreenViewState{" +
                "y=" + y +
                ", x=" + x +
                ", height=" + height +
                ", width=" + width +
                '}';
    }

    public static class Preference extends AbstractPreference<FloatingCalculatorViewState> {

        public Preference(@Nonnull String key, @Nullable FloatingCalculatorViewState defaultValue) {
            super(key, defaultValue);
        }

        @Nullable
        @Override
        protected FloatingCalculatorViewState getPersistedValue(@Nonnull SharedPreferences preferences) {
            try {
                final FloatingCalculatorViewState result = new FloatingCalculatorViewState();
                final JSONObject jsonObject = new JSONObject(preferences.getString(getKey(), "{}"));
                result.width = jsonObject.getInt("width");
                result.height = jsonObject.getInt("height");
                result.x = jsonObject.getInt("x");
                result.y = jsonObject.getInt("y");

                Log.d(TAG, "Reading onscreen view state: " + result);

                return result;
            } catch (JSONException e) {
                return getDefaultValue();
            }
        }

        @Override
        protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull
        FloatingCalculatorViewState value) {
            final Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("width", value.getWidth());
            properties.put("height", value.getHeight());
            properties.put("x", value.getX());
            properties.put("y", value.getY());

            final JSONObject jsonObject = new JSONObject(properties);

            final String json = jsonObject.toString();
            Log.d(TAG, "Persisting onscreen view state: " + json);
            editor.putString(getKey(), json);
        }
    }
}
