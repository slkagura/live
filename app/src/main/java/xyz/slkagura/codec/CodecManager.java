package xyz.slkagura.codec;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CodecManager {
    private static final ArrayBlockingQueue<Runnable> CODEC_RUNNABLE_QUEUE = new ArrayBlockingQueue<>(5);
    
    private static final ThreadPoolExecutor CODEC_THREAD_POOL = new ThreadPoolExecutor(0, 3, 60, TimeUnit.SECONDS, CODEC_RUNNABLE_QUEUE);
    
    public static void createCodec() {
    }
}
