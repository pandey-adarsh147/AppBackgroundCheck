package in.helpchat.library;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

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

    private Activity currentActivity;
    private ApplicationLifecycleCallback applicationLifecycleCallback;
    private PublishSubject<Boolean> activeSubject = PublishSubject.create();

    public ApplicationLifecycle(ApplicationLifecycleCallback applicationLifecycleCallback, int backgroundDelay) {
        this.backgroundDelay = backgroundDelay;
        this.applicationLifecycleCallback = applicationLifecycleCallback;

        activeSubject.debounce(backgroundDelay, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Boolean>() {
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

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
        if (!isAppForeground && (System.currentTimeMillis() - appWentToBackground > backgroundDelay)) {
            // Activity on resumed
            onForeground();
        }
        isAppForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        // initiate here
        isAppForeground = false;
        appWentToBackground = System.currentTimeMillis();
        activeSubject.onNext(true);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

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
