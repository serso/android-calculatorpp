/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---------------------------------------------------------------------
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.common.msg;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * Interface represents translatable user message.
 * Implementation of this class will likely contains
 * some logic for translation message according to
 * it's message code and list of parameters.
 */
public interface Message {

    /**
     * @return message code
     */
    @Nonnull
    public String getMessageCode();

    /**
     * @return list of message parameters
     */
    @Nonnull
    public List<Object> getParameters();

    /**
     * @return message level
     */
    @Nonnull
    public MessageLevel getMessageLevel();

    /**
     * @param locale locate to which current message should be translated
     * @return message string translated to specified locale
     */
    @Nonnull
    public String getLocalizedMessage(@Nonnull Locale locale);

    /**
     * @return message string translated to deault locale (Locale.getDefault())
     */
    @Nonnull
    public String getLocalizedMessage();
}
