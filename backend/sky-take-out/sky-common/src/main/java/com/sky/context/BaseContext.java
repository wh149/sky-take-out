package com.sky.context;

public class BaseContext {
    // threadLocal
    // 内存泄漏 ？
    // 看你们都提出了会产生内存泄露问题，但是没人说明解决方法，这里帖个解决方法,重写拦截器的afterCompletion方法，并添加BaseContext.removeCurrentId()
    
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
