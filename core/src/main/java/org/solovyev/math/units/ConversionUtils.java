package org.solovyev.math.units;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.StringUtils;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:01
 */
public class ConversionUtils {
    @NotNull
    public static String doConversion(@NotNull UnitConverter<String> converter,
                                      @Nullable String from,
                                      @NotNull UnitType<String> fromUnitType,
                                      @NotNull UnitType<String> toUnitType) throws ConversionException {
        final String result;

        if (StringUtils.isEmpty(from)) {
            result = "";
        } else {

            String to = null;
            try {
                if (converter.isSupported(fromUnitType, toUnitType)) {
                    to = converter.convert(UnitImpl.newInstance(from, fromUnitType), toUnitType).getValue();
                }
            } catch (RuntimeException e) {
                throw new ConversionException(e);
            }

            result = to;
        }

        return result;
    }
}
