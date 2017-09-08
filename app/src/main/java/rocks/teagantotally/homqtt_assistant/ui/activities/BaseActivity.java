package rocks.teagantotally.homqtt_assistant.ui.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import rocks.teagantotally.homqtt_assistant.BuildConfig;
import rocks.teagantotally.homqtt_assistant.R;
import rocks.teagantotally.homqtt_assistant.databinding.ActivityContainerBinding;
import rocks.teagantotally.homqtt_assistant.di.Injector;
import rocks.teagantotally.homqtt_assistant.events.BaseEvent;
import rocks.teagantotally.homqtt_assistant.events.CancelEvent;
import rocks.teagantotally.homqtt_assistant.events.NavigationEvent;
import rocks.teagantotally.homqtt_assistant.events.notifications.ProgressDialogNotificationEvent;
import rocks.teagantotally.homqtt_assistant.events.notifications.SnackbarNotificationEvent;
import rocks.teagantotally.homqtt_assistant.routing.Router;
import rocks.teagantotally.homqtt_assistant.ui.fragments.BaseFragment;
import rocks.teagantotally.homqtt_assistant.ui.utils.KeyboardUtil;

/**
 * Created by tglenn on 8/30/17.
 */

public abstract class BaseActivity
          extends AppCompatActivity {

    /**
     * @return The top most activity
     */
    public static BaseActivity getTopActivity() {
        return topActivity;
    }

    private static final String TAG = "BaseActivity";
    private static BaseActivity topActivity;

    protected EventBus eventBus;
    protected KeyboardUtil keyboardUtil;
    private boolean isRunning;
    private ActivityContainerBinding binding;
    private Map<ProgressDialogNotificationEvent, ProgressDialog> progressDialogMap = new HashMap<>();

    protected String getPermissionReason() {
        return null;
    }

    @IntRange(from = 0)
    protected int getPermissionRequestCode() {
        return 0;
    }

    protected String[] getOptionalPermissions() {
        return new String[0];
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this,
                                                 R.layout.activity_container);

        eventBus = Injector.get()
                           .eventBus();
        try {
            eventBus.register(this);
        } catch (EventBusException e) {
            Log.e(TAG,
                  "onCreate: Unable to register",
                  e);
        } catch (NullPointerException e) {
            Log.e(TAG,
                  "onCreate: Bus was not injected",
                  e);
        }

        keyboardUtil = new KeyboardUtil(this,
                                        binding.coordinatorLayout);

        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(Router.FRAGMENT) && args.get(Router.FRAGMENT) != null) {
            String tag = (String) args.get(Router.KEY);
            Class fragment = (Class) args.get(Router.FRAGMENT);
            try {
                BaseFragment baseFragment = (BaseFragment) fragment.newInstance();
                baseFragment.setArguments(args.getBundle(Router.EXTRAS));
                FragmentTransaction transaction =
                          getSupportFragmentManager().beginTransaction()
                                                     .replace(R.id.container,
                                                              baseFragment,
                                                              tag);
                if (args.getBoolean(Router.BACKSTACK,
                                    true)) {
                    transaction.addToBackStack(tag);
                }
                transaction.commit();
            } catch (Exception e) {
                Log.e(TAG,
                      "onCreate: Error adding fragment",
                      e
                );
                new SnackbarNotificationEvent("Error adding fragment",
                                              SnackbarNotificationEvent.Length.SHORT);
            }
        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        topActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (eventBus != null && !eventBus.isRegistered(this)) {
            try {
                eventBus.register(this);
            } catch (EventBusException e) {
                Log.i(TAG,
                      "onStart: no subscribed methods");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
        super.onDestroy();
    }

//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(final DialogNotificationEvent event) {
//        AlertDialog.Builder builder =
//                  new AlertDialog.Builder(this,
//                                          R.style.DialogTheme)
//                            .setMessage(event.getMessage());
//        if (event.getTitle() != null) {
//            builder.setTitle(event.getTitle());
//        }
//        if (event.getOptions() != null) {
//            ArrayAdapter<CharSequence> options =
//                      new ArrayAdapter<>(this,
//                                         android.R.layout.simple_list_item_1,
//                                         event.getOptions());
//            builder.setAdapter(options,
//                               event.getPositive().second);
//            builder.setCancelable(true);
//        } else {
//            builder.setPositiveButton(event.getPositive()
//                                                .first,
//                                      event.getPositive()
//                                                .second);
//            builder.setCancelable(false);
//        }
//        if (event.getNegative() != null) {
//            builder.setNegativeButton(event.getNegative()
//                                                .first,
//                                      event.getNegative()
//                                                .second);
//        }
//
//        builder.create()
//               .show();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ProgressDialogNotificationEvent event) {
        if (progressDialogMap.containsKey(event)) {
            return;
        }

        progressDialogMap.put(event,
                              ProgressDialog.show(this,
                                                  event.getTitle(),
                                                  event.getMessage(),
                                                  true,
                                                  false));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CancelEvent event) {
        BaseEvent eventToCancel = event.getEventToCancel();
        if (!(eventToCancel instanceof ProgressDialogNotificationEvent) ||
            !progressDialogMap.containsKey(eventToCancel)) {
            return;
        }
        progressDialogMap.remove(eventToCancel)
                         .dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SnackbarNotificationEvent event) {
        CoordinatorLayout coordinatorLayout = binding.coordinatorLayout;
        if (coordinatorLayout != null) {
            int length = event.getLength() == SnackbarNotificationEvent.Length.LONG
                         ? Snackbar.LENGTH_LONG
                         : Snackbar.LENGTH_SHORT;
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                              event.getMessage(),
                                              length);

            if (event.getActionText() != null && event.getActionClickListener() != null) {
                snackbar.setAction(event.getActionText()
                                        .toUpperCase(),
                                   event.getActionClickListener());
            }
            snackbar.show();
        } else {
            int length = event.getLength() == SnackbarNotificationEvent.Length.LONG
                         ? Toast.LENGTH_LONG
                         : Toast.LENGTH_SHORT;
            Toast.makeText(this,
                           event.getMessage(),
                           length)
                 .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NavigationEvent event) {
        if (isRunning) {
            Router.navigateTo(this,
                              event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SubscriberExceptionEvent event) {
        Log.e(TAG,
              "onEvent: Error dispatching event",
              event.throwable);
        if (BuildConfig.DEBUG) {
            new SnackbarNotificationEvent(event.throwable.getMessage(),
                                          SnackbarNotificationEvent.Length.SHORT);
        }
    }

    protected void navigateTo(String to) {
        new NavigationEvent(to);
    }

    protected void navigateTo(Uri to) {
        navigateTo(to.toString());
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount == 0) {
            super.onBackPressed();
        }

        Fragment topMostFragment =
                  getSupportFragmentManager().findFragmentByTag(
                            getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1)
                                                       .getName());
        if (backStackCount == 1) {
            finish();
        }

        super.onBackPressed();
    }
}
