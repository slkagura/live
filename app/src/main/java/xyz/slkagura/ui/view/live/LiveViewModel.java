package xyz.slkagura.ui.view.live;

import android.content.Context;
import android.view.TextureView;

import xyz.slkagura.common.base.BaseBindingViewModel;
import xyz.slkagura.common.base.BaseRepository;
import xyz.slkagura.live.LiveEngine;
import xyz.slkagura.live.tag.LiveState;

public class LiveViewModel extends BaseBindingViewModel {
    private final LiveEngine mLiveEngine = LiveEngine.create(0);
    
    @LiveState
    public int mPreState = LiveState.CREATED;
    
    @Override
    protected BaseRepository initDataSource(Context context) {
        return null;
    }
    
    public TextureView resumeLive() {
        TextureView textureView = null;
        if (!mLiveEngine.isInRoom() || mPreState == LiveState.PREVIEW) {
            mLiveEngine.join("room");
            mPreState = LiveState.JOINED;
            mLiveEngine.onRemoteOnline("remote");
            textureView = mLiveEngine.startRemoteView("remote");
            mLiveEngine.startLocalStream();
            mPreState = LiveState.STREAM;
        }
        return textureView;
    }
    
    public void pauseLive() {
        if (mPreState == LiveState.PREVIEW) {
            mLiveEngine.stopLocalView();
        } else if (mPreState == LiveState.STREAM) {
            mLiveEngine.stopLocalStream();
        }
    }
    
    public interface IHandler {
        void onLive();
        void onStopLive();
    }
}
