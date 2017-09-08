package rocks.teagantotally.homqtt_assistant.events.notifications;

import android.support.annotation.NonNull;

/**
 * Created by tglenn on 8/30/17.
 */

public class ProgressDialogNotificationEvent
          extends NotificationEvent {
    public ProgressDialogNotificationEvent(@NonNull String message) {
        super(message);
    }
}
