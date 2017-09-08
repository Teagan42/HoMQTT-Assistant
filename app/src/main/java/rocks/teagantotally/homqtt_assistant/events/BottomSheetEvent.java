package rocks.teagantotally.homqtt_assistant.events;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by teaganglenn on 7/10/17.
 */

public class BottomSheetEvent
          extends BaseEvent {
    @LayoutRes int layoutResourceIdentifier;
    int bindingVariableIdentifier;
    Object viewModel;

    public BottomSheetEvent(@LayoutRes int layoutResourceIdentifier,
                            int bindingVariableIdentifier,
                            @NonNull Object viewModel) {
        Objects.requireNonNull(viewModel,
                               "View model cannot be null");
        this.layoutResourceIdentifier = layoutResourceIdentifier;
        this.bindingVariableIdentifier = bindingVariableIdentifier;
        this.viewModel = viewModel;
    }

    /**
     * @return The layout resource identifier to inflate
     */
    @LayoutRes
    public int getLayoutResourceIdentifier() {
        return layoutResourceIdentifier;
    }

    /**
     * @return The binding variable identifier to populate
     */
    public int getBindingVariableIdentifier() {
        return bindingVariableIdentifier;
    }

    /**
     * @return The view model to populate the view with
     */
    public Object getViewModel() {
        return viewModel;
    }
}
