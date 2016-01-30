package org.solovyev.android.calculator.variables;

import com.google.common.base.Strings;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class JsclConstant implements IConstant {

    @Nonnull
    private final CppVariable variable;
    private Double doubleValue;
    private Constant constant;

    JsclConstant(@Nonnull CppVariable variable) {
        this.variable = variable;
    }

    @Nonnull
    @Override
    public Constant getConstant() {
        if (constant == null) {
            constant = new Constant(variable.name);
        }
        return constant;
    }

    @Nullable
    @Override
    public String getDescription() {
        return variable.description;
    }

    @Override
    public boolean isDefined() {
        return !Strings.isNullOrEmpty(variable.value);
    }

    @Nullable
    @Override
    public String getValue() {
        return variable.value;
    }

    @Nullable
    @Override
    public Double getDoubleValue() {
        if (doubleValue != null) {
            return doubleValue;
        }
        if (!Strings.isNullOrEmpty(variable.value)) {
            try {
                doubleValue = Double.valueOf(variable.value);
            } catch (NumberFormatException e) {
                // do nothing - string is not a double
            }
        }
        return doubleValue;
    }

    @Nonnull
    @Override
    public String toJava() {
        return Strings.nullToEmpty(variable.value);
    }

    @Nonnull
    @Override
    public String getName() {
        return variable.name;
    }

    @Override
    public boolean isSystem() {
        return variable.system;
    }

    @Nonnull
    @Override
    public Integer getId() {
        return variable.id == CppVariable.NO_ID ? null : variable.id;
    }

    @Override
    public void setId(@Nonnull Integer id) {
        variable.id = id;
    }

    @Override
    public boolean isIdDefined() {
        return variable.id != CppVariable.NO_ID;
    }

    @Override
    public void copy(@Nonnull MathEntity o) {
        if (!(o instanceof IConstant)) {
            throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + o.getClass());
        }
        final IConstant that = ((IConstant) o);
        variable.name = that.getName();
        variable.value = Strings.nullToEmpty(that.getValue());
        variable.description = Strings.nullToEmpty(that.getDescription());
        variable.system = that.isSystem();
        if (that.isIdDefined()) {
            variable.id = that.getId();
        } else {
            variable.id = CppVariable.NO_ID;
        }
        this.doubleValue = null;
        this.constant = null;
    }
}
