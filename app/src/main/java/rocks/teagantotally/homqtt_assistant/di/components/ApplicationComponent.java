package rocks.teagantotally.homqtt_assistant.di.components;


import javax.inject.Singleton;

import dagger.Component;
import rocks.teagantotally.homqtt_assistant.di.modules.ApplicationContextModule;
import rocks.teagantotally.homqtt_assistant.di.modules.EventBusModule;

/**
 * Created by tglenn on 8/30/17.
 */

@Singleton
@Component(modules = {
          ApplicationContextModule.class,
          EventBusModule.class
})
public interface ApplicationComponent
          extends ApplicationContextComponent,
                  EventBusComponent {

}

