package org.solovyev.android.calculator.variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.calculator.json.Jsonable;

import javax.annotation.Nonnull;

public class CppVariable implements Jsonable {
    @Nonnull
    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }
}
