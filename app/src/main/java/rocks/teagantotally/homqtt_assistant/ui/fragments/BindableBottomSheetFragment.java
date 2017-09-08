package rocks.teagantotally.homqtt_assistant.ui.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by teaganglenn on 7/10/17.
 */

public class BindableBottomSheetFragment
          extends BottomSheetDialogFragment {
    @LayoutRes int layoutResourceIdentifier;
    int bindingVariableIdentifier;
    Object viewModel;
    ViewDataBinding binding;

    /**
     * @return The layout resource identifier to inflate
     */
    public int getLayoutResourceIdentifier() {
        return layoutResourceIdentifier;
    }

    /**
     * Set the layout resource identifier to inflate
     *
     * @param layoutResourceIdentifier Layout resource identifier
     */
    public void setLayoutResourceIdentifier(int layoutResourceIdentifier) {
        this.layoutResourceIdentifier = layoutResourceIdentifier;
    }

    /**
     * @return The binding variable identifier to populate
     */
    public int getBindingVariableIdentifier() {
        return bindingVariableIdentifier;
    }

    /**
     * Set the binding variable identifier
     *
     * @param bindingVariableIdentifier The binding identifer to populate
     */
    public void setBindingVariableIdentifier(int bindingVariableIdentifier) {
        this.bindingVariableIdentifier = bindingVariableIdentifier;
    }

    /**
     * @return The view model
     */
    public Object getViewModel() {
        return viewModel;
    }

    /**
     * Set the view model
     *
     * @param viewModel View model
     */
    public void setViewModel(Object viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (layoutResourceIdentifier == 0) {
            throw new IllegalStateException("No layout resource identifier specified");
        }
        if (bindingVariableIdentifier == 0) {
            throw new IllegalStateException("No binding variable specified");
        }
        if (viewModel == null) {
            throw new IllegalStateException("View model cannot be null");
        }
        binding = DataBindingUtil.inflate(inflater,
                                          layoutResourceIdentifier,
                                          container,
                                          false);
        binding.setVariable(bindingVariableIdentifier,
                            viewModel);
        return binding.getRoot();
    }
}
