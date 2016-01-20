package org.solovyev.android.calculator.json;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public interface Jsonable {
    @Nonnull
    JSONObject toJson() throws JSONException;
}
