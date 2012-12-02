package org.solovyev.android.calculator.onscreen;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.prefs.AbstractPreference;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/21/12
 * Time: 10:55 PM
 */
public class CalculatorOnscreenViewState implements Parcelable {

    private static final String TAG = CalculatorOnscreenViewState.class.getSimpleName();

    public static final Parcelable.Creator<CalculatorOnscreenViewState> CREATOR = new Parcelable.Creator<CalculatorOnscreenViewState>() {
        public CalculatorOnscreenViewState createFromParcel(@NotNull Parcel in) {
            return CalculatorOnscreenViewState.fromParcel(in);
        }

        public CalculatorOnscreenViewState[] newArray(int size) {
            return new CalculatorOnscreenViewState[size];
        }
    };

    private int width;

    private int height;

    private int x;

    private int y;

    private CalculatorOnscreenViewState() {
    }

    @NotNull
    private static CalculatorOnscreenViewState fromParcel(@NotNull Parcel in) {
        final CalculatorOnscreenViewState result = new CalculatorOnscreenViewState();
        result.width = in.readInt();
        result.height = in.readInt();
        result.x = in.readInt();
        result.y = in.readInt();
        return result;
    }

    @NotNull
    public static CalculatorOnscreenViewState newDefaultState() {
        return newInstance(200, 400, 0, 0);
    }

    @NotNull
    public static CalculatorOnscreenViewState newInstance(int width, int height, int x, int y) {
        final CalculatorOnscreenViewState result = new CalculatorOnscreenViewState();
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
    public void writeToParcel(@NotNull Parcel out, int flags) {
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

    public static class Preference extends AbstractPreference<CalculatorOnscreenViewState> {

        public Preference(@NotNull String key, @Nullable CalculatorOnscreenViewState defaultValue) {
            super(key, defaultValue);
        }

        @Nullable
        @Override
        protected CalculatorOnscreenViewState getPersistedValue(@NotNull SharedPreferences preferences) {
            try {
                final CalculatorOnscreenViewState result = new CalculatorOnscreenViewState();
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
        protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull CalculatorOnscreenViewState value) {
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
