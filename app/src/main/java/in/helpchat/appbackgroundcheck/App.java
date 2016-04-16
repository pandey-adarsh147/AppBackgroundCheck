package in.helpchat.appbackgroundcheck;

import android.app.Application;

import in.helpchat.library.ApplicationLifeCallback;
import in.helpchat.library.ApplicationListener;

/**
 * Created by adarshpandey on 4/13/16.
 */
public class App extends Application implements ApplicationLifeCallback {

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ApplicationListener(this));
    }

    @Override
    public void onApplicationBackground() {

    }

    @Override
    public void onApplicationForeground() {

    }
}
