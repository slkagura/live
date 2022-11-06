package xyz.slkagura.live.interfaces;

public interface LiveEngineHandler {
    void onJoinRoom(String roomId);
    
    void onLeaveRoom();
    
    void onRemoteOnline(String userId);
    
    void onRemoteOffline(String userId);
}
