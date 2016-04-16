package in.helpchat.appbackgroundcheck;

import android.app.Application;
import android.util.Log;

import in.helpchat.library.ApplicationLifecycleCallback;
import in.helpchat.library.ApplicationLifecycle;

/**
 * Created by adarshpandey on 4/13/16.
 */
public class App extends Application implements ApplicationLifecycleCallback {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ApplicationLifecycle(this));
    }

    @Override
    public void onApplicationBackground() {
        Log.d(TAG, "Application goes to background");
    }

    @Override
    public void onApplicationForeground() {
        Log.d(TAG, "Application in foreground");
    }
}
