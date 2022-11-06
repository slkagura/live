package xyz.slkagura.codec.bean;

public class Frame {
    public final byte[] mData;
    
    public final int mLength;
    
    public final long mPTS;
    
    public Frame(byte[] data) {
        mData = data;
        mLength = data.length;
        mPTS = System.currentTimeMillis();
    }
}
