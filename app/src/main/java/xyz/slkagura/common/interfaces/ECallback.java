package xyz.slkagura.common.interfaces;

/**
 * 无参、无返回、带异常的 Callback
 *
 * @param <E> 异常类型
 */
public interface ECallback<E extends Throwable> {
    /**
     * 回调
     *
     * @throws E 异常
     */
    void call() throws E;
}
