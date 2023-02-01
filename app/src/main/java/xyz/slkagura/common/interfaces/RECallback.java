package xyz.slkagura.common.interfaces;

/**
 * 无参、带返回、带异常 Callback
 *
 * @param <R> 返回类型
 * @param <E> 异常类型
 */
public interface RECallback<R, E extends Throwable> {
    /**
     * 回调
     *
     * @return 返回
     * @throws E 异常
     */
    R call() throws E;
}
