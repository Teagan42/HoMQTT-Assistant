package rocks.teagantotally.homqtt_assistant.events.notifications;

import android.support.annotation.NonNull;

import java.util.Objects;

import rocks.teagantotally.homqtt_assistant.events.BaseEvent;

/**
 * Created by tglenn on 8/30/17.
 */

public class NotificationEvent
          extends BaseEvent {
    private String title;
    private String message;

    NotificationEvent(@NonNull String message) {
        Objects.requireNonNull(message,
                               "Notification message is required");
        this.message = message;
    }

    NotificationEvent(@NonNull String title,
                      @NonNull String message) {
        Objects.requireNonNull(title,
                               "Notification title is required");
        Objects.requireNonNull(message,
                               "Notification message is required");
        this.message = message;
        this.title = title;
    }

    /**
     * @return The notification title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The notification message
     */
    public String getMessage() {
        return message;
    }
}
