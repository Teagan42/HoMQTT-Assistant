package rocks.teagantotally.homqtt_assistant.di.components;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import rocks.teagantotally.homqtt_assistant.di.modules.EventBusModule;

/**
 * Created by tglenn on 8/30/17.
 */

@Singleton
@Component(modules = {EventBusModule.class})
public interface EventBusComponent {
    /**
     * @return The event bus used in the application
     */
    EventBus eventBus();
}


