package rocks.teagantotally.homqtt_assistant.events;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by tglenn on 8/30/17.
 */

public class NavigationEvent
          extends BaseEvent {
    private String to;
    private boolean addToBackstack = true;
    private Integer flags;

    public NavigationEvent(@NonNull String to) {
        this(to,
             true);
    }

    public NavigationEvent(@NonNull String to,
                           boolean addToBackstack) {
        Objects.requireNonNull(to,
                               "To cannot be null");
        this.to = to;
        this.addToBackstack = addToBackstack;
    }

    public NavigationEvent(@NonNull String to,
                           int flags) {
        this(to,
             false);
        this.flags = flags;
    }

    /**
     * @return The screen we are navigating to
     */
    public String getTo() {
        return to;
    }

    /**
     * @return Whether to add to backstack
     */
    public boolean shouldAddToBackstack() {
        return addToBackstack;
    }

    /**
     * @return Intent flags
     */
    public Integer getFlags() {
        return flags;
    }
}
