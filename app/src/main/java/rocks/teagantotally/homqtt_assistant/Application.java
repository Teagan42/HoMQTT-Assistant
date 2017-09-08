package rocks.teagantotally.homqtt_assistant;


/**
 * Created by tglenn on 8/30/17.
 */

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import rocks.teagantotally.homqtt_assistant.di.Injector;


public class Application
          extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Injector.initialize(this);
    }
}

