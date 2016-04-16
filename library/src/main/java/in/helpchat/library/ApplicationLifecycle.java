package in.helpchat.library;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 * Created by adarshpandey on 4/13/16.
 */
public class ApplicationLifecycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = ApplicationLifecycle.class.getName();

    private int backgroundDelay = 10000;
    private long appWentToBackground = -1;

    public volatile boolean isAppForeground = false;
    public volatile boolean isAppWasInForeground = false;

    private Activity currentActivity;
    private ApplicationLifecycleCallback applicationLifecycleCallback;

    private PublishSubject<Boolean> backgroundSubject = PublishSubject.create();

    public ApplicationLifecycle(ApplicationLifecycleCallback applicationLifecycleCallback, int backgroundDelay) {
        this.backgroundDelay = backgroundDelay;
        this.applicationLifecycleCallback = applicationLifecycleCallback;

        backgroundSubject.debounce(backgroundDelay, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (!isAppForeground) {
                    onBackground();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "Created: " + activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "Started: " + activity.getClass().getName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "Resumed: " + activity.getClass().getName());

        if (System.currentTimeMillis() - appWentToBackground > backgroundDelay) {
            // Activity on resumed
            onForeground();
        }

        isAppForeground = true;
        this.currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "Paused: " + activity.getClass().getName());

        // initiate here
        isAppForeground = false;
        appWentToBackground = System.currentTimeMillis();
        backgroundSubject.onNext(true);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "Stopped: " + activity.getClass().getName());

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "SaveInastanceState: " + activity.getClass().getName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "Destroyed: " + activity.getClass().getName());
    }

    public void onForeground() {
        if (applicationLifecycleCallback != null) {
            applicationLifecycleCallback.onApplicationForeground();
        }
    }

    public void onBackground() {
        if (applicationLifecycleCallback != null) {
            applicationLifecycleCallback.onApplicationBackground();
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
