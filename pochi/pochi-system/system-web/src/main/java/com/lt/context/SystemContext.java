package com.lt.context;

import com.lt.pojo.SysLog;
import lombok.Data;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/8 14:05
 * @Version 1.0
 */
@Data
public class SystemContext {

    /**
     * 日志实体
     */
    private SysLog sysLog;

    /**
     * 本地线程上下文，存储线程工作内存中的变量
     */
    private static ThreadLocal<SystemContext> threadLocal = new ThreadLocal<>();

    /**
     * 获取当前线程上下文
     *
     * @return
     */
    public static SystemContext get() {
        if (threadLocal.get() == null) {
            SystemContext systemContext = new SystemContext();
            systemContext.setSysLog(new SysLog());
            threadLocal.set(systemContext);
        }
        return threadLocal.get();
    }

    public void remove() {
        threadLocal.remove();
    }

}
