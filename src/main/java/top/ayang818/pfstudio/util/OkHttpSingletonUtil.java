package top.ayang818.pfstudio.util;

import okhttp3.OkHttpClient;

public class OkHttpSingletonUtil {
    private static volatile OkHttpClient okHttpClient;

    private OkHttpSingletonUtil() {

    }

    // 使用双重校验锁实现okhttp的单例模式
    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (OkHttpSingletonUtil.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }
}
