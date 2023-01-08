package xyz.slkagura.log;

import android.util.Log;

import androidx.annotation.IntDef;

@IntDef({ Priority.VERBOSE, Priority.DEBUG, Priority.INFO, Priority.WARN, Priority.ERROR, Priority.ASSET })
public @interface Priority {
    int VERBOSE = Log.VERBOSE;
    
    int DEBUG = Log.DEBUG;
    
    int INFO = Log.INFO;
    
    int WARN = Log.WARN;
    
    int ERROR = Log.ERROR;
    
    int ASSET = Log.ASSERT;
}
