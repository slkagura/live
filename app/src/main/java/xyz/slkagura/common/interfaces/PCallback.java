package xyz.slkagura.common.interfaces;

/**
 * 带参、无返回、无异常 Callback
 *
 * @param <P> 参数类型
 */
public interface PCallback<P> {
    /**
     * 回调
     *
     * @param param 参数
     */
    void call(P param);
}
