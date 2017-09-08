package rocks.teagantotally.homqtt_assistant.di;


import android.support.multidex.MultiDex;

import rocks.teagantotally.homqtt_assistant.Application;
import rocks.teagantotally.homqtt_assistant.di.components.ApplicationComponent;
import rocks.teagantotally.homqtt_assistant.di.components.DaggerApplicationComponent;
import rocks.teagantotally.homqtt_assistant.di.modules.ApplicationContextModule;

/**
 * Created by tglenn on 9/8/17.
 */

public enum Injector {
    INSTANCE;

    ApplicationComponent applicationComponent;
    Application application;

    Injector() {

    }

    /**
     * Initialize with a custom build dagger component builder
     *
     * @param builder Component builder
     */
    public static void initialize(DaggerApplicationComponent.Builder builder) {
        ApplicationContextModule appContext = new ApplicationContextModule(INSTANCE.application);

        builder.applicationContextModule(appContext);

        INSTANCE.applicationComponent = builder.build();
    }

    /**
     * Initialize the singleton instance
     *
     * @param application The application object
     */
    public static void initialize(Application application) {
        if (INSTANCE.applicationComponent != null) {
            throw new IllegalStateException("The injector has already been initialized");
        }

        INSTANCE.application = application;

        MultiDex.install(application);

        initialize(DaggerApplicationComponent.builder());
    }

    /**
     * @return The application component
     */
    public static ApplicationComponent get() {
        return INSTANCE.applicationComponent;
    }
}
