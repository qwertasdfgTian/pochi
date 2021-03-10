package com.lt.aop;

import com.alibaba.fastjson.JSON;
import com.lt.context.SystemContext;
import com.lt.enums.StateEnums;
import com.lt.pojo.SysLog;
import com.lt.utils.StringUtils;
import com.lt.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:54
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class RequestAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 不记录日志的接口
     */
    private static final String[] EXCLUDE_URLS = {"/sysLog/", "/upload/"};

    /**
     * 判断url是否需要放行
     *
     * @param url
     * @return
     */
    private boolean exclude(String url) {
        for (String excludeUrl : EXCLUDE_URLS) {
            if (url.contains(excludeUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 声明切点
     */
    @Pointcut("execution( * com.lt.controller..*(..))")
    public void logPointCut() {

    }

    /**
     * 前置通知
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        // 获取request
        HttpServletRequest request = attributes.getRequest();
        // 获取请求地址
        String uri = request.getRequestURI();
        // 记录日志
        // 日志输出基本信息
        if (!exclude(uri)) {
            log.info("请求地址：{}", uri);
            log.info("请求方式：{}", request.getMethod());
            // 获取请求IP
            String remoteIp = StringUtils.getRemoteIp(request);
            log.info("IP：{}", remoteIp);
            // 获取请求的controller
            String controllerName = joinPoint.getSignature().getDeclaringTypeName();
            log.info("方法：{}.{}", controllerName, joinPoint.getSignature().getName());
            // 记录参数
            Object[] args = joinPoint.getArgs();
            // 记录日志条件：参数不为空，并且第一个参数不是request也不是MultipartFile
            boolean logParamFlag = args != null && args.length > 0 && !(args[0] instanceof ServletRequest) && !(args[0] instanceof MultipartFile);
            SysLog sysLog = SystemContext.get().getSysLog();
            if (logParamFlag) {
                String param = JSON.toJSONString(args[0]);
                log.info("请求参数：{}", param);
                sysLog.setLogParams(param);
            }
            // 记录日志
            sysLog.setLogUrl(uri);
            sysLog.setLogStatus(StateEnums.REQUEST_SUCCESS.getCode());
            sysLog.setLogMethod(request.getMethod());
            sysLog.setLogIp(remoteIp);
            sysLog.setLogUa(request.getHeader("user-Agent"));
            sysLog.setLogController(controllerName);
        }
    }

    /**
     * 环绕通知
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        // 获取request
        HttpServletRequest request = attributes.getRequest();
        // 获取请求地址
        String uri = request.getRequestURI();
        // 记录方法执行时间
        long startTime = System.currentTimeMillis();
        Object ob = pjp.proceed();
        long time = System.currentTimeMillis() - startTime;
        log.info("方法执行耗时：{}", time);
        if (!exclude(uri)) {
            SysLog sysLog = SystemContext.get().getSysLog();
            sysLog.setLogTime(time);
            String result = JSON.toJSONString(ob);
            log.info("返回值：{}", result);
            sysLog.setLogResult(result);
            sysLogService.save(sysLog);
        }
        SystemContext.get().remove();
        return ob;
    }

    /**
     * 异常通知，发生异常走这里
     *
     * @param joinPoint
     * @param throwable
     */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "throwable")
    public void doException(JoinPoint joinPoint, Throwable throwable) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        // 获取request
        HttpServletRequest request = attributes.getRequest();
        // 获取请求地址
        String uri = request.getRequestURI();
        if (!exclude(uri)) {
            SysLog sysLog = SystemContext.get().getSysLog();
            sysLog.setLogStatus(StateEnums.REQUEST_ERROR.getCode());
            sysLog.setLogMessage(throwable.getMessage());
            sysLog.setLogTime(0L);
            sysLogService.save(sysLog);
        }
        SystemContext.get().remove();
    }

}
