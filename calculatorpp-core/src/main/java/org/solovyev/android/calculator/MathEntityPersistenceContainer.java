package org.solovyev.android.calculator;

import java.util.List;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 5:03 PM
 */
public interface MathEntityPersistenceContainer<T extends MathPersistenceEntity> {

    public List<T> getEntities();

}
