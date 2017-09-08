package rocks.teagantotally.homqtt_assistant.di.modules;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tglenn on 8/30/17.
 */

@Module
public class EventBusModule {
    /**
     * Provide the event bus to use in the application
     *
     * @return
     */
    @Provides
    @Singleton
    public EventBus eventBus() {
        return EventBus.getDefault();
    }
}


