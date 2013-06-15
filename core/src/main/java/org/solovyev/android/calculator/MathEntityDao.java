package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:43 PM
 */
public interface MathEntityDao<T extends MathPersistenceEntity> {

	void save(@Nonnull MathEntityPersistenceContainer<T> container);

	@Nullable
	MathEntityPersistenceContainer<T> load();

	@Nullable
	String getDescription(@Nonnull String descriptionId);
}
