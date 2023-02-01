package xyz.slkagura.common.interfaces;

/**
 * 无参、带返回、无异常 Callback
 *
 * @param <R> 返回类型
 */
public interface RCallback<R> {
    /**
     * 回调
     *
     * @return 返回
     */
    R call();
}
