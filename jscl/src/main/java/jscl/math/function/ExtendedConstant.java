package jscl.math.function;

import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/7/11
 * Time: 12:06 PM
 */
public class ExtendedConstant implements Comparable<ExtendedConstant>, IConstant {

    @Nonnull
    private Constant constant;

    @Nullable
    private String value;

    @Nullable
    private String javaString;

    @Nullable
    private String description;

    ExtendedConstant() {
    }

    ExtendedConstant(@Nonnull Constant constant,
                     @Nullable String value,
                     @Nullable String javaString) {
        this.constant = constant;
        this.value = value;
        this.javaString = javaString;
    }

    ExtendedConstant(@Nonnull Constant constant,
                     @Nullable Double value,
                     @Nullable String javaString) {
        this.constant = constant;
        this.value = value == null ? null : String.valueOf(value);
        this.javaString = javaString;
    }

    @Nonnull
    public static String toString(@Nonnull IConstant constant) {
        final Double doubleValue = constant.getDoubleValue();
        if (doubleValue == null) {
            final String stringValue = constant.getValue();
            if (stringValue != null && stringValue.length() > 0) {
                return constant.getName() + " = " + stringValue;
            } else {
                return constant.getName();
            }
        } else {
            return constant.getName() + " = " + doubleValue;
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return this.constant.getName();
    }

    @Override
    public boolean isSystem() {
        return this.constant.isSystem();
    }

    @Nonnull
    @Override
    public Integer getId() {
        return constant.getId();
    }

    @Override
    public void setId(@Nonnull Integer id) {
        constant.setId(id);
    }

    @Override
    public boolean isIdDefined() {
        return constant.isIdDefined();
    }

    @Override
    public void copy(@Nonnull MathEntity that) {
        this.constant.copy(that);

        if (that instanceof IConstant) {
            this.description = ((IConstant) that).getDescription();
            this.value = ((IConstant) that).getValue();
        }

        if (that instanceof ExtendedConstant) {
            this.javaString = ((ExtendedConstant) that).javaString;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtendedConstant)) return false;

        ExtendedConstant that = (ExtendedConstant) o;

        return constant.equals(that.constant);

    }

    @Override
    public int hashCode() {
        return constant.hashCode();
    }

    @Override
    @Nonnull
    public Constant getConstant() {
        return constant;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isDefined() {
        return value != null;
    }

    @Override
    @Nullable
    public String getValue() {
        return value;
    }

    @Override
    public Double getDoubleValue() {
        Double result = null;

        if (value != null) {
            try {
                result = Double.valueOf(value);
            } catch (NumberFormatException e) {
                // do nothing - string is not a double
            }
        }

        return result;
    }

    @Override
    @Nonnull
    public String toJava() {
        if (javaString != null) {
            return javaString;
        } else if (value != null) {
            return String.valueOf(value);
        } else {
            return constant.getName();
        }
    }

    @Override
    public String toString() {
        return toString(this);
    }

    @Override
    public int compareTo(ExtendedConstant o) {
        return this.constant.compareTo(o.getConstant());
    }

    public static final class Builder {
        @Nonnull
        private Constant constant;

        @Nullable
        private String value;

        @Nullable
        private String javaString;

        @Nullable
        private String description;

        public Builder(@Nonnull Constant constant, @Nullable Double value) {
            this(constant, value == null ? null : String.valueOf(value));
        }

        public Builder(@Nonnull Constant constant, @Nullable String value) {
            this.constant = constant;
            this.value = value;
        }

        public Builder setJavaString(@Nullable String javaString) {
            this.javaString = javaString;
            return this;
        }

        public Builder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public ExtendedConstant create() {
            final ExtendedConstant result = new ExtendedConstant();

            result.constant = constant;
            result.value = value;
            result.javaString = javaString;
            result.description = description;

            return result;
        }
    }
}
