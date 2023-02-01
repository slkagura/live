package xyz.slkagura.common.interfaces;

/**
 * 带参、无返回、带异常 Callback
 *
 * @param <P> 参数类型
 * @param <E> 异常类型
 */
public interface PECallback<P, E extends Throwable> {
    /**
     * 回调
     *
     * @param param 参数
     * @throws E 异常
     */
    void call(P param) throws E;
}
