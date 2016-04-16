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
public class ApplicationListener implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = ApplicationListener.class.getName();

    private static final int APP_BACKGROUND_INTERVAL_TIME = 10000;


    private long appWentToBackground = -1;
    private Activity currentActivity;

    public volatile boolean isAppForeground = false;

    private ApplicationLifeCallback applicationLifeCallback;

    private PublishSubject<Boolean> activeSubject = PublishSubject.create();

    public ApplicationListener(ApplicationLifeCallback applicationLifeCallback) {
        this.applicationLifeCallback = applicationLifeCallback;

        activeSubject.debounce(10, TimeUnit.SECONDS).subscribe(new Subscriber<Boolean>() {
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
        if (!isAppForeground && (System.currentTimeMillis() - appWentToBackground > APP_BACKGROUND_INTERVAL_TIME)) {
            // Activity on resumed
            onForeground();
        }
        isAppForeground = true;
        Log.d(TAG, "========= Init Fore ground ==========");
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
        Log.d(TAG, "========= Init Stopped ==========");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void onForeground() {
        Log.d(TAG, "================ Application in Foreground =================");
        if (applicationLifeCallback != null) {
            applicationLifeCallback.onApplicationForeground();
        }
    }

    public void onBackground() {
        Log.d(TAG, "================ Application in Background =================");
        if (applicationLifeCallback != null) {
            applicationLifeCallback.onApplicationBackground();
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
