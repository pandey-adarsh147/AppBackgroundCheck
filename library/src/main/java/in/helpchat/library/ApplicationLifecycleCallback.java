package in.helpchat.library;

/**
 * Created by adarshpandey on 4/16/16.
 */
public interface ApplicationLifecycleCallback {

    void onApplicationBackground();

    void onApplicationForeground();
}
