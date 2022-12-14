package xyz.slkagura.live.bean;

import android.view.TextureView;

public class LiveUser {
    private String mUserId;
    
    private String mRoomId;
    
    private boolean mIsPublisher;
    
    private TextureView mPreview;
    
    public LiveUser(String userId) {
        mUserId = userId;
        mIsPublisher = false;
    }
    
    public String getUserId() {
        return mUserId;
    }
    
    public void setUserId(String userId) {
        mUserId = userId;
    }
    
    public String getRoomId() {
        return mRoomId;
    }
    
    public void setRoomId(String roomId) {
        mRoomId = roomId;
    }
    
    public boolean isPublisher() {
        return mIsPublisher;
    }
    
    public void setPublisher(boolean publisher) {
        mIsPublisher = publisher;
    }
    
    public TextureView getPreview() {
        return mPreview;
    }
    
    public void setPreview(TextureView preview) {
        mPreview = preview;
    }
}
