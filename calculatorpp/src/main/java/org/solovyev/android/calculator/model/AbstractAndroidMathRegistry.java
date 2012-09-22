/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.TextHelper;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 10/30/11
 * Time: 1:03 AM
 */
public abstract class AbstractAndroidMathRegistry<T extends MathEntity, P extends MathPersistenceEntity> implements CalculatorMathRegistry<T> {

	@NotNull
	private final MathRegistry<T> mathRegistry;

	@NotNull
	private final String prefix;

    @NotNull
    private final Context context;

	protected AbstractAndroidMathRegistry(@NotNull MathRegistry<T> mathRegistry,
                                          @NotNull String prefix,
                                          @NotNull Application application) {
		this.mathRegistry = mathRegistry;
		this.prefix = prefix;
        this.context = application;
	}



    @NotNull
	protected abstract Map<String, String> getSubstitutes();

	@Nullable
	@Override
	public String getDescription(@NotNull String mathEntityName) {
		final String stringName;

		final Map<String, String> substitutes = getSubstitutes();
		final String substitute = substitutes.get(mathEntityName);
		if (substitute == null) {
			stringName = prefix + mathEntityName;
		} else {
			stringName = prefix + substitute;
		}

		return new TextHelper(context.getResources(), R.class.getPackage().getName()).getText(stringName);
	}

	public synchronized void load() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (preferences != null) {
			final Integer preferenceStringId = getPreferenceStringId();
			if (preferenceStringId != null) {
				final String value = preferences.getString(context.getString(preferenceStringId), null);
				if (value != null) {
					final Serializer serializer = new Persister();
					try {
						final MathEntityPersistenceContainer<P> persistenceContainer = serializer.read(getPersistenceContainerClass(), value);
						for (P entity : persistenceContainer.getEntities()) {
							if (!contains(entity.getName())) {
								add(createBuilder(entity));
							}
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		/*Log.d(AndroidVarsRegistry.class.getName(), vars.size() + " variables registered!");
		for (Var var : vars) {
			Log.d(AndroidVarsRegistry.class.getName(), var.toString());
		}*/
	}

	@NotNull
	protected abstract JBuilder<? extends T> createBuilder(@NotNull P entity);

	@NotNull
	protected abstract Class<? extends MathEntityPersistenceContainer<P>> getPersistenceContainerClass();

	@Nullable
	protected abstract Integer getPreferenceStringId();


	@Override
	public synchronized void save() {
		final Integer preferenceStringId = getPreferenceStringId();

		if (preferenceStringId != null) {
			final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			final SharedPreferences.Editor editor = settings.edit();

			final MathEntityPersistenceContainer<P> container = createPersistenceContainer();
			for (T entity : this.getEntities()) {
				if (!entity.isSystem()) {
					final P persistenceEntity = transform(entity);
					if (persistenceEntity != null) {
						container.getEntities().add(persistenceEntity);
					}
				}
			}

			final StringWriter sw = new StringWriter();
			final Serializer serializer = new Persister();
			try {
				serializer.write(container, sw);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			editor.putString(context.getString(preferenceStringId), sw.toString());

			editor.commit();
		}
	}

	@Nullable
	protected abstract P transform(@NotNull T entity);

	@NotNull
	protected abstract MathEntityPersistenceContainer<P> createPersistenceContainer();

	@NotNull
	@Override
	public List<T> getEntities() {
		return mathRegistry.getEntities();
	}

	@NotNull
	@Override
	public List<T> getSystemEntities() {
		return mathRegistry.getSystemEntities();
	}

	@Override
	public T add(@NotNull JBuilder<? extends T> JBuilder) {
		return mathRegistry.add(JBuilder);
	}

	@Override
	public void remove(@NotNull T var) {
		mathRegistry.remove(var);
	}

	@NotNull
	@Override
	public List<String> getNames() {
		return mathRegistry.getNames();
	}

	@Override
	public boolean contains(@NotNull String name) {
		return mathRegistry.contains(name);
	}

	@Override
	public T get(@NotNull String name) {
		return mathRegistry.get(name);
	}

	@Override
	public T getById(@NotNull Integer id) {
		return mathRegistry.getById(id);
	}
}
