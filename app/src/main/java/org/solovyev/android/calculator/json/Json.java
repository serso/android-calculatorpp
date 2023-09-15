package org.solovyev.android.calculator.json;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public final class Json {

    @NonNull
    private static final String TAG = "Json";

    private Json() {
    }

    @NonNull
    public static <T> List<T> fromJson(@NonNull JSONArray array, @NonNull Creator<T> creator) {
        final List<T> items = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            final JSONObject json = array.optJSONObject(i);
            if (json == null) {
                continue;
            }
            try {
                items.add(creator.create(json));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return items;
    }

    @NonNull
    public static JSONArray toJson(@NonNull List<? extends Jsonable> items) {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            final Jsonable item = items.get(i);
            try {
                array.put(i, item.toJson());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return array;
    }

    @Nonnull
    public static <T> List<T> load(@Nonnull File file, @NonNull FileSystem fileSystem,
        @NonNull Creator<T> creator) throws IOException, JSONException {
        if (!file.exists()) {
            return Collections.emptyList();
        }
        final CharSequence json = fileSystem.read(file);
        if (isEmpty(json)) {
            return Collections.emptyList();
        }
        return fromJson(new JSONArray(json.toString()), creator);
    }

    public interface Creator<T> {
        @NonNull
        T create(@NonNull JSONObject json) throws JSONException;
    }
}
