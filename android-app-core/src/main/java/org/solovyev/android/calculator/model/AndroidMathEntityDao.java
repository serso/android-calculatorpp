package org.solovyev.android.calculator.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.MathEntityDao;
import org.solovyev.android.calculator.MathEntityPersistenceContainer;
import org.solovyev.android.calculator.MathPersistenceEntity;

import java.io.StringWriter;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:46 PM
 */
public class AndroidMathEntityDao<T extends MathPersistenceEntity> implements MathEntityDao<T> {

    @NotNull
    private static final String TAG = AndroidMathEntityDao.class.getSimpleName();

    @Nullable
    private final Integer preferenceStringId;

    @NotNull
    private final Context context;

    @Nullable
    private final Class<? extends MathEntityPersistenceContainer<T>> persistenceContainerClass;

    public AndroidMathEntityDao(@Nullable Integer preferenceStringId,
                                @NotNull Application application,
                                @Nullable Class<? extends MathEntityPersistenceContainer<T>> persistenceContainerClass) {
        this.preferenceStringId = preferenceStringId;
        this.context = application;
        this.persistenceContainerClass = persistenceContainerClass;
    }

    @Override
    public void save(@NotNull MathEntityPersistenceContainer<T> container) {
        if (preferenceStringId != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();

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
    @Override
    public MathEntityPersistenceContainer<T> load() {
        if (persistenceContainerClass != null && preferenceStringId != null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (preferences != null) {
                final String value = preferences.getString(context.getString(preferenceStringId), null);
                if (value != null) {
                    final Serializer serializer = new Persister();
                    try {
                        return serializer.read(persistenceContainerClass, value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public String getDescription(@NotNull String descriptionId) {
        final Resources resources = context.getResources();

        final int stringId = resources.getIdentifier(descriptionId, "string", App.getInstance().getApplication().getClass().getPackage().getName());
        try {
            return resources.getString(stringId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }
}
