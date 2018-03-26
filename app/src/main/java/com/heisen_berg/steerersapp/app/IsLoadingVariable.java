package com.heisen_berg.steerersapp.app;

/**
 * Created by shikhar on 22/07/17.
 */

public class IsLoadingVariable {
    private boolean isLoading = false;
    private ChangeListener listener;

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
