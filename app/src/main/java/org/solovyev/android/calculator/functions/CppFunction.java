package org.solovyev.android.calculator.functions;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.common.JBuilder;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;

public class CppFunction implements Jsonable, Parcelable {

    public static final Json.Creator<CppFunction> JSON_CREATOR = new Json.Creator<CppFunction>() {
        @NonNull
        @Override
        public CppFunction create(@NonNull JSONObject json) throws JSONException {
            return new CppFunction(json);
        }
    };
    public static final Creator<CppFunction> CREATOR = new Creator<CppFunction>() {
        @Override
        public CppFunction createFromParcel(Parcel in) {
            return new CppFunction(in);
        }

        @Override
        public CppFunction[] newArray(int size) {
            return new CppFunction[size];
        }
    };
    public static final int NO_ID = -1;
    private static final String JSON_NAME = "n";
    private static final String JSON_BODY = "b";
    private static final String JSON_PARAMETERS = "ps";
    private static final String JSON_DESCRIPTION = "d";
    @Nonnull
    protected final List<String> parameters = new ArrayList<>();
    protected int id = NO_ID;
    @Nonnull
    protected String name;
    @Nonnull
    protected String body;
    @Nonnull
    protected String description = "";

    private CppFunction(@Nonnull String name, @Nonnull String body) {
        this.name = name;
        this.body = body;
    }

    private CppFunction(@NonNull JSONObject json) throws JSONException {
        final JSONArray array = json.optJSONArray(JSON_PARAMETERS);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final String parameter = array.getString(i);
                if (!TextUtils.isEmpty(parameter)) {
                    parameters.add(parameter);
                }
            }
        }
        name = json.getString(JSON_NAME);
        body = json.getString(JSON_BODY);
        description = json.optString(JSON_DESCRIPTION, "");
    }

    private CppFunction(@NonNull CppFunction that) {
        id = that.id;
        parameters.addAll(that.parameters);
        name = that.name;
        body = that.body;
        description = that.description;
    }

    private CppFunction(@NonNull IFunction that) {
        id = that.isIdDefined() ? that.getId() : NO_ID;
        parameters.addAll(that.getParameterNames());
        name = that.getName();
        body = that.getContent();
        description = Strings.getNotEmpty(that.getDescription(), "");
    }

    protected CppFunction(Parcel in) {
        id = in.readInt();
        parameters.addAll(in.createStringArrayList());
        name = in.readString();
        body = in.readString();
        description = in.readString();
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
        Check.isNotEmpty(name);
        Check.isNotEmpty(body);

        final JSONObject json = new JSONObject();
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
        json.put(JSON_NAME, name);
        json.put(JSON_BODY, body);
        if (!TextUtils.isEmpty(description)) {
            json.put(JSON_DESCRIPTION, description);
        }
        return json;
    }

    public int getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeStringList(parameters);
        dest.writeString(name);
        dest.writeString(body);
        dest.writeString(description);
    }

    @Nonnull
    public JBuilder<? extends Function> toJsclBuilder() {
        final CustomFunction.Builder builder = new CustomFunction.Builder(name, parameters, body);
        builder.setDescription(description);
        if (id != NO_ID) {
            builder.setId(id);
        }
        return builder;
    }

    public static final class Builder {

        @NonNull
        private final CppFunction function;
        private boolean built;

        private Builder(@Nonnull String name, @Nonnull String body) {
            function = new CppFunction(name, body);
        }

        public Builder(@NonNull CppFunction that) {
            function = new CppFunction(that);
        }

        public Builder(@NonNull IFunction that) {
            function = new CppFunction(that);
        }

        @Nonnull
        public Builder withDescription(@Nonnull String description) {
            Check.isTrue(!built);
            function.description = description;
            return this;
        }

        @Nonnull
        public Builder withParameters(@Nonnull Collection<? extends String> parameters) {
            Check.isTrue(!built);
            function.parameters.addAll(parameters);
            return this;
        }

        @Nonnull
        public Builder withParameter(@Nonnull String parameter) {
            Check.isTrue(!built);
            function.parameters.add(parameter);
            return this;
        }

        @Nonnull
        public Builder withId(int id) {
            Check.isTrue(!built);
            function.id = id;
            return this;
        }

        @Nonnull
        public CppFunction build() {
            built = true;
            return function;
        }
    }
}
