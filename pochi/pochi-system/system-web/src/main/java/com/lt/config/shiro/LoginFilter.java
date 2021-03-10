package com.lt.config.shiro;

import com.alibaba.fastjson.JSON;
import com.lt.enums.ResultEnums;
import com.lt.vo.Result;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 重写登录失效重定向
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/8 19:00
 * @Version 1.0
 */
public class LoginFilter extends UserFilter {

    /**
     * 这个方法用于处理未登录时页面重定向的逻辑
     * 因此，只要进入了这个方法，就意味着登录失效了。
     * 我们只需要在这个方法里。给前端返回一个登陆失败的状态即可
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        // 设置响应头是json
        response.setContentType("application/json; charset=utf-8");
        // 直接写回未登录的json报文
        response.getWriter().write(JSON.toJSONString(new Result<>(ResultEnums.NO_LOGIN)));
    }
}
