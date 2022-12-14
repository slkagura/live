package xyz.slkagura.live.tag;

import androidx.annotation.IntDef;

@IntDef({
    LiveState.NULL,
    LiveState.CREATED,
    LiveState.JOINED,
    LiveState.PREVIEW,
    LiveState.STREAM
})
public @interface LiveState {
    int NULL = -1;
    
    int CREATED = 0;
    
    int JOINED = 1;
    
    int PREVIEW = 2;
    
    int STREAM = 3;
}
