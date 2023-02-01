package xyz.slkagura.common.interfaces;

/**
 * 带参、带返回、带异常 Callback
 *
 * @param <P> 参数类型
 * @param <R> 返回类型
 * @param <E> 异常类型
 */
public interface RPECallback<R, P, E extends Throwable> {
    /**
     * 回调
     *
     * @param param 参数
     * @return 返回
     * @throws E 异常
     */
    R call(P param) throws E;
}
