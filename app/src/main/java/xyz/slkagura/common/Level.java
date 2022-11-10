package xyz.slkagura.common;

import androidx.annotation.IntDef;

@IntDef({
    Level.Verbose,
    Level.Debug,
    Level.Info,
    Level.Warn,
    Level.Error
})
public @interface Level {
    int Verbose = 2;
    int Debug = 3;
    int Info = 4;
    int Warn = 5;
    int Error = 6;
}
