package rocks.teagantotally.homqtt_assistant.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rocks.teagantotally.homqtt_assistant.di.Injector;
import rocks.teagantotally.homqtt_assistant.events.BottomSheetEvent;
import rocks.teagantotally.homqtt_assistant.ui.activities.BaseActivity;
import rocks.teagantotally.homqtt_assistant.ui.utils.KeyboardUtil;

/**
 * Created by tglenn on 8/30/17.
 */

public abstract class BaseFragment
          extends Fragment {
    private static final String TAG = "BaseFragment";

    protected static final LifecycleEvent DEFAULT_UNREGISTER_LIFECYCLE_EVENT = LifecycleEvent.ONSTOP;

    protected EventBus eventBus;
    private boolean isPaused;

    protected enum LifecycleEvent {
        ONSTOP,
        ONDESTROY
    }

    protected boolean isPaused() {
        return isPaused;
    }

    /**
     * Pops the backstack
     */
    protected void goBack() {
        KeyboardUtil.hideKeyboard(getActivity());
        getBaseActivity().onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus = Injector.get()
                           .eventBus();
        try {
            if (eventBus != null && !eventBus.isRegistered(this)) {
                eventBus.register(this);
            }
        } catch (EventBusException e) {
            Log.i(TAG,
                  "onCreate: No subscription methods");
        } catch (NullPointerException e) {
            Log.e(TAG,
                  "onCreate: Bus was not injected",
                  e);
        }

        setRetainInstance(true);
    }

    /**
     * @return The lifecycle event to unregister this object from the event bus
     */
    protected LifecycleEvent getUnregisterLifecycleEvent() {
        return DEFAULT_UNREGISTER_LIFECYCLE_EVENT;
    }

    @Override
    public void onStop() {
        if (getUnregisterLifecycleEvent() == LifecycleEvent.ONSTOP) {
            unregisterEventBus();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (getUnregisterLifecycleEvent() == LifecycleEvent.ONDESTROY) {
            unregisterEventBus();
        }
        super.onDestroy();
    }

    /**
     * Unregisters this object from the event bus
     */
    private void unregisterEventBus() {
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    /**
     * @return The base activity
     */
    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    /**
     * @return True if this fragment is the top most on the stack
     */
    public boolean isTop() {
        FragmentManager fragmentManager;
        String tag;
        if (getActivity() != null) {
            fragmentManager = getActivity().getSupportFragmentManager();
            tag = getTag();
        } else if (getParentFragment() != null) {
            fragmentManager = getParentFragment().getActivity()
                                                 .getSupportFragmentManager();
            tag = getParentFragment().getTag();
        } else {
            return false;
        }

        boolean hasFragment = fragmentManager.getBackStackEntryCount() > 0;
        return hasFragment
               && fragmentManager.getBackStackEntryAt(
                  fragmentManager.getBackStackEntryCount() - 1)
                                 .getName()
                                 .equals(tag);
    }

    /**
     * Event subscription for bottom sheet dialog events
     *
     * @param event Event data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BottomSheetEvent event) {
        BindableBottomSheetFragment bottomSheetFragment = new BindableBottomSheetFragment();
        bottomSheetFragment.setLayoutResourceIdentifier(event.getLayoutResourceIdentifier());
        bottomSheetFragment.setBindingVariableIdentifier(event.getBindingVariableIdentifier());
        bottomSheetFragment.setViewModel(event.getViewModel());
        bottomSheetFragment.show(getChildFragmentManager(),
                                 bottomSheetFragment.getTag());
    }
}
