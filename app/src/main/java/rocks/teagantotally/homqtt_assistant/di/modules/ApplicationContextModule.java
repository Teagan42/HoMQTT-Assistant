package rocks.teagantotally.homqtt_assistant.di.modules;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rocks.teagantotally.homqtt_assistant.Application;

/**
 * Created by tglenn on 8/30/17.
 */

@Module
public class ApplicationContextModule {
    private android.app.Application application;

    /**
     * Creates a new application context module
     *
     * @param application
     */
    public ApplicationContextModule(Application application) {
        this.application = application;
    }

    /**
     * Provides the application dependency object
     *
     * @return
     */
    @Provides
    @Singleton
    public android.app.Application application() {
        return this.application;
    }

    /**
     * Provides the application context
     *
     * @return
     */
    @Provides
    @Singleton
    public Context applicationContext() {
        return this.application;
    }
}

