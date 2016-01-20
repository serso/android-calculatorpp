package org.solovyev.android.calculator.function;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import jscl.math.function.IFunction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CppFunction implements Jsonable {

    public static final Json.Creator<CppFunction> JSON_CREATOR = new Json.Creator<CppFunction>() {
        @NonNull
        @Override
        public CppFunction create(@NonNull JSONObject json) throws JSONException {
            return new CppFunction(json);
        }
    };
    private static final String JSON_NAME = "n";
    private static final String JSON_BODY = "b";
    private static final String JSON_PARAMETERS = "ps";
    private static final String JSON_DESCRIPTION = "d";
    @Nonnull
    protected final List<String> parameters = new ArrayList<>();
    @Nonnull
    protected String name;
    @Nonnull
    protected String body;
    @Nonnull
    protected String description = "";

    private CppFunction(@Nonnull String name, @Nonnull String body) {
        Check.isNotEmpty(name);
        Check.isNotEmpty(body);
        this.name = name;
        this.body = body;
    }

    private CppFunction(@NonNull JSONObject json) throws JSONException {
        name = json.getString(JSON_NAME);
        body = json.getString(JSON_BODY);
        final JSONArray array = json.optJSONArray(JSON_PARAMETERS);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final String parameter = array.getString(i);
                if (!TextUtils.isEmpty(parameter)) {
                    parameters.add(parameter);
                }
            }
        }
        description = json.optString(JSON_DESCRIPTION, "");
    }

    private CppFunction(@NonNull CppFunction that) {
        name = that.name;
        body = that.body;
        description = that.description;
        parameters.addAll(that.parameters);
    }

    private CppFunction(@NonNull IFunction that) {
        name = that.getName();
        body = that.getContent();
        description = Strings.getNotEmpty(that.getDescription(), "");
        parameters.addAll(that.getParameterNames());
    }

    @Nonnull
    public static Builder builder(@Nonnull String name, @Nonnull String body) {
        return new Builder(name, body);
    }

    @Nonnull
    public static Builder builder(@Nonnull CppFunction function) {
        return new Builder(function);
    }

    @Nonnull
    public static Builder builder(@Nonnull IFunction function) {
        return new Builder(function);
    }

    @Nonnull
    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_NAME, name);
        json.put(JSON_BODY, body);
        if (!parameters.isEmpty()) {
            final JSONArray array = new JSONArray();
            int j = 0;
            for (int i = 0; i < parameters.size(); i++) {
                final String parameter = parameters.get(i);
                if (!TextUtils.isEmpty(parameter)) {
                    array.put(j++, parameter);
                }
            }
            json.put(JSON_PARAMETERS, array);
        }
        if (!TextUtils.isEmpty(description)) {
            json.put(JSON_DESCRIPTION, description);
        }
        return json;
    }

    @Nonnull
    public String getBody() {
        return body;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public List<String> getParameters() {
        return parameters;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public static final class Builder extends CppFunction {

        private boolean built;

        private Builder(@Nonnull String name, @Nonnull String body) {
            super(name, body);
        }

        public Builder(@NonNull CppFunction function) {
            super(function);
        }

        public Builder(@NonNull IFunction function) {
            super(function);
        }

        @Nonnull
        public Builder withDescription(@Nonnull String description) {
            Check.isTrue(!built);
            this.description = description;
            return this;
        }

        @Nonnull
        public Builder withParameters(@Nonnull Collection<? extends String> parameters) {
            Check.isTrue(!built);
            this.parameters.addAll(parameters);
            return this;
        }

        @Nonnull
        public Builder withParameter(@Nonnull String parameter) {
            Check.isTrue(!built);
            parameters.add(parameter);
            return this;
        }

        @Nonnull
        public CppFunction build() {
            built = true;
            return this;
        }

        public void withValuesFrom(@Nonnull IFunction that) {
            Check.isTrue(!built);
            name = that.getName();
            body = that.getContent();
            description = Strings.getNotEmpty(that.getDescription(), "");
            parameters.clear();
            parameters.addAll(that.getParameterNames());
        }
    }
}
