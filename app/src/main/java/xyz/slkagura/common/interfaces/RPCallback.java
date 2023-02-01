package xyz.slkagura.common.interfaces;

/**
 * 带参、带返回、无异常 Callback
 *
 * @param <P> 参数类型
 * @param <R> 返回类型
 */
public interface RPCallback<R, P> {
    /**
     * 回调
     *
     * @param param 参数
     * @return 返回
     */
    R call(P param);
}
