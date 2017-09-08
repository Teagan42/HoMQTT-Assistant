package rocks.teagantotally.homqtt_assistant.events.notifications;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.Objects;

/**
 * Created by tglenn on 8/30/17.
 */

public class SnackbarNotificationEvent
          extends NotificationEvent {
    public enum Length {
        LONG,
        SHORT
    }

    private String actionText;
    private View.OnClickListener actionClickListener;
    private Length length;

    public SnackbarNotificationEvent(@NonNull String title) {
        super(title);
    }

    public SnackbarNotificationEvent(@NonNull String title,
                                     @NonNull Length length) {
        super(title);
        Objects.requireNonNull(length,
                               "Snackbar notification length cannot be null");
        this.length = length;
    }

    public SnackbarNotificationEvent(@NonNull String title,
                                     @NonNull Length length,
                                     String actionText,
                                     View.OnClickListener actionClickListener) {
        super(title);
        Objects.requireNonNull(length,
                               "Snackbar notification length cannot be null");
        this.length = length;
        this.actionText = actionText;
        this.actionClickListener = actionClickListener;
    }

    /**
     * @return The text for the snackbar action
     */
    public String getActionText() {
        return actionText;
    }

    /**
     * @return The click listener for the snackbar action
     */
    public View.OnClickListener getActionClickListener() {
        return actionClickListener;
    }

    /**
     * @return The duration to show the snackbar
     */
    public Length getLength() {
        return length;
    }
}
