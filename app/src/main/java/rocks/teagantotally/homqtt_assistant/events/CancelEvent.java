package rocks.teagantotally.homqtt_assistant.events;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by tglenn on 8/30/17.
 */

public class CancelEvent
          extends BaseEvent {
    private BaseEvent eventToCancel;

    public CancelEvent(@NonNull BaseEvent eventToCancel) {
        Objects.requireNonNull(eventToCancel,
                               "Event to cancel cannot be null");
        this.eventToCancel = eventToCancel;
    }

    /**
     * @return The event to cancel
     */
    public BaseEvent getEventToCancel() {
        return eventToCancel;
    }
}
