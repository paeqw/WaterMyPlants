package paeqw.app.models;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import paeqw.app.collections.SpaceManager;

public class SharedViewModel extends ViewModel {
    private SpaceManager spaceManager;

    public SpaceManager getSpaceManager() {
        return spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}