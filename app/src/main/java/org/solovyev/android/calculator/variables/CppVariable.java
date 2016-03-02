package org.solovyev.android.calculator.variables;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import jscl.math.function.IConstant;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;

import javax.annotation.Nonnull;

import static com.google.common.base.Strings.nullToEmpty;

public class CppVariable implements Jsonable, Parcelable {

    public static final Json.Creator<CppVariable> JSON_CREATOR = new Json.Creator<CppVariable>() {
        @NonNull
        @Override
        public CppVariable create(@NonNull JSONObject json) throws JSONException {
            return new CppVariable(json);
        }
    };
    public static final Creator<CppVariable> CREATOR = new Creator<CppVariable>() {
        @Override
        public CppVariable createFromParcel(Parcel in) {
            return new CppVariable(in);
        }

        @Override
        public CppVariable[] newArray(int size) {
            return new CppVariable[size];
        }
    };
    public static final int NO_ID = CppFunction.NO_ID;
    private static final String JSON_NAME = "n";
    private static final String JSON_VALUE = "v";
    private static final String JSON_DESCRIPTION = "d";
    protected int id = NO_ID;
    @Nonnull
    protected String name;
    @Nonnull
    protected String value = "";
    @Nonnull
    protected String description = "";
    protected boolean system;

    private CppVariable(@Nonnull String name) {
        this.name = name;
    }

    protected CppVariable(@Nonnull CppVariable that) {
        this.id = that.id;
        this.name = that.name;
        this.value = that.value;
        this.description = that.description;
        this.system = that.system;
    }

    protected CppVariable(@NonNull IConstant that) {
        id = that.isIdDefined() ? that.getId() : NO_ID;
        name = that.getName();
        value = nullToEmpty(that.getValue());
        description = nullToEmpty(that.getDescription());
        system = that.isSystem();
    }

    private CppVariable(@NonNull JSONObject json) throws JSONException {
        this.name = json.getString(JSON_NAME);
        this.value = json.optString(JSON_VALUE);
        this.description = json.optString(JSON_DESCRIPTION);
    }

    protected CppVariable(Parcel in) {
        id = in.readInt();
        name = in.readString();
        value = in.readString();
        description = in.readString();
        system = in.readByte() != 0;
    }

    @NonNull
    public static CppVariable.Builder builder(@NonNull String name) {
        return new Builder(name);
    }

    @NonNull
    public static CppVariable.Builder builder(@NonNull String name, double value) {
        return new Builder(name).withValue(value);
    }

    @NonNull
    public static Builder builder(@NonNull IConstant constant) {
        return new Builder(constant);
    }

    @Nonnull
    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_NAME, name);
        if (!TextUtils.isEmpty(value)) {
            json.put(JSON_VALUE, value);
        }
        if (!TextUtils.isEmpty(description)) {
            json.put(JSON_DESCRIPTION, description);
        }
        return json;
    }

    @Nonnull
    public IConstant toJsclConstant() {
        return new JsclConstant(CppVariable.this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CppVariable)) return false;

        CppVariable that = (CppVariable) o;

        if (id != that.id) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(value);
        dest.writeString(description);
        dest.writeByte((byte) (system ? 1 : 0));
    }

    @Override
    public String toString() {
        if (id == NO_ID) {
            return name + "=" + value;
        } else {
            return name + "[#" + id + "]=" + value;
        }
    }

    public static class Builder {
        @NonNull
        private final CppVariable variable;
        private boolean built;

        private Builder(@NonNull String name) {
            variable = new CppVariable(name);
        }

        private Builder(@NonNull IConstant constant) {
            variable = new CppVariable(constant);
        }

        @Nonnull
        public Builder withDescription(@Nonnull String description) {
            Check.isTrue(!built);
            variable.description = description;
            return this;
        }

        @Nonnull
        public Builder withValue(@NonNull String value) {
            Check.isTrue(!built);
            variable.value = value;
            return this;
        }

        @Nonnull
        public Builder withValue(double value) {
            Check.isTrue(!built);
            return withValue(Double.toString(value));
        }

        @Nonnull
        public Builder withSystem(boolean system) {
            Check.isTrue(!built);
            variable.system = system;
            return this;
        }

        @Nonnull
        public Builder withId(int id) {
            Check.isTrue(!built);
            variable.id = id;
            return this;
        }

        @Nonnull
        public CppVariable build() {
            built = true;
            return variable;
        }
    }
}
