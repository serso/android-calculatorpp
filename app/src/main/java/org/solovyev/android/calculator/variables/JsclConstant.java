package org.solovyev.android.calculator.variables;

import com.google.common.base.Strings;

import org.solovyev.android.calculator.function.CppFunction;
import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.function.Constant;
import jscl.math.function.IConstant;

class JsclConstant extends CppVariable implements IConstant {

    private Double doubleValue;
    private Constant constant;

    JsclConstant(@Nonnull CppVariable variable) {
        super(variable);
    }

    @Nonnull
    @Override
    public Constant getConstant() {
        if (constant == null) {
            constant = new Constant(name);
        }
        return constant;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isDefined() {
        return !Strings.isNullOrEmpty(value);
    }

    @Nullable
    @Override
    public String getValue() {
        return value;
    }

    @Nullable
    @Override
    public Double getDoubleValue() {
        if (doubleValue != null) {
            return doubleValue;
        }
        if (!Strings.isNullOrEmpty(value)) {
            try {
                doubleValue = Double.valueOf(value);
            } catch (NumberFormatException e) {
                // do nothing - string is not a double
            }
        }
        return doubleValue;
    }

    @Nonnull
    @Override
    public String toJava() {
        return Strings.nullToEmpty(value);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSystem() {
        return system;
    }

    @Nonnull
    @Override
    public Integer getId() {
        return id == CppFunction.NO_ID ? null : id;
    }

    @Override
    public void setId(@Nonnull Integer id) {
        this.id = id;
    }

    @Override
    public boolean isIdDefined() {
        return id != CppFunction.NO_ID;
    }

    @Override
    public void copy(@Nonnull MathEntity o) {
        if (!(o instanceof IConstant)) {
            throw new IllegalArgumentException("Trying to make a copy of unsupported type: " + o.getClass());
        }
        final IConstant that = ((IConstant) o);
        this.name = that.getName();
        this.value = that.getValue();
        this.description = that.getDescription();
        this.system = that.isSystem();
        if (that.isIdDefined()) {
            this.id = that.getId();
        } else {
            this.id = CppFunction.NO_ID;
        }
        this.doubleValue = null;
        this.constant = null;
    }
}
