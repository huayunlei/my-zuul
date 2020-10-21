package com.ihomefnt.sky.common.cache;

/**
 * @Description:
 * @Author hua
 * @Date 2020/1/15 3:46 下午
 */
public class RedisCacheUtil {

    /**生成缓存key（拼冒号）
     * @param namespace
     * @param keys
     * @return
     */
    public static String generateCacheKey(String namespace, Object... keys) {
        StringBuilder out = new StringBuilder();
        out.append(namespace);
        if (keys != null && keys.length > 0) {
            out.append(":");
            for (int i = 0; i < keys.length; i++) {
                out.append(keys[i]);
                if (i != keys.length - 1) {
                    out.append(":");
                }
            }
        }
        return out.toString();
    }

    /**生成不拼冒号的缓存key
     * @param namespace
     * @param keys
     * @return
     */
    public static String generateNoColonCacheKey(String namespace, Object... keys) {
        StringBuilder out = new StringBuilder();
        out.append(namespace);
        if (keys != null && keys.length > 0) {
            out.append(":");
            for (int i = 0; i < keys.length; i++) {
                out.append(keys[i]);
            }
        }
        return out.toString();
    }
}
