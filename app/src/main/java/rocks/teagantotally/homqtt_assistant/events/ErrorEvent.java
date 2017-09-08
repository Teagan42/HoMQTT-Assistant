package rocks.teagantotally.homqtt_assistant.events;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by tglenn on 8/30/17.
 */

public class ErrorEvent
          extends BaseEvent {
    private Throwable throwable;

    public ErrorEvent(@NonNull Throwable throwable) {
        Objects.requireNonNull(throwable,
                               "Throwable cannot be null");
        this.throwable = throwable;
    }

    /**
     * @return The thrown object
     */
    public Throwable getThrowable() {
        return throwable;
    }
}
