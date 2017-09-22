/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.interceptor;

import org.liusk.common.constants.SystemConfig;
import org.liusk.configuration.cas.SpringCasAutoconfig;
import org.liusk.context.UserSessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 该拦截器实现了系统的登陆拦截
 * 本系统中cas只拦截casLogin这个一个访问请求
 * 如果判断用户session不存在，那么重定向到casLogin，这样cas就可以拦截到这个请求，然后进入cas认证中心登陆
 * @author liusk
 * @version $Id: SessionInterceptor.java, v 0.1 2017/9/22 14:25 liusk Exp $
 */
@Component
public class SessionInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {


        //访问路径
        String urlPath = httpServletRequest.getRequestURI();
        //是否存在session用户,不存在session返回登录页面
        HttpSession session = httpServletRequest.getSession();
        if (session.getAttribute(UserSessionModel.SESSION_USER_STR) == null) {
            //获取系统完整路径
            String basePath = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":"
                    + httpServletRequest.getServerPort() + httpServletRequest.getContextPath() + "/";

            //重定向到登陆地址，登陆地址后面跟一个被访问的页面的路径，在登陆之后直接跳转到之前要访问的页面
            httpServletResponse.sendRedirect(basePath + SystemConfig.CAS_AUTH_FILTER_URL + "?indexPath=" + urlPath);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
