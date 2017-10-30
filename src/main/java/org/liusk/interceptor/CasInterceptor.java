/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.liusk.context.UserSessionModel;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * cas拦截器：该拦截器的功能只是解析从cas服务器传回的用户信息存入session，不判断是否登陆跳转等问题
 * @author liusk
 * @version $Id: CasInterceptor.java, v 0.1 2017/9/6 19:53 liusk Exp $
 */
@Component
public class CasInterceptor implements HandlerInterceptor {

    /**
     * 请求处理之前调用，返回false表示取消当前请求
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object o) throws Exception {
        Assertion assertion = (Assertion) httpServletRequest.getSession()
            .getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        if (assertion != null) {
            AttributePrincipal principal = assertion.getPrincipal();
            Map<String, Object> map = principal.getAttributes();
            String uid = map.get("id") == null ? null : map.get("id").toString();
            String name = map.get("name") == null ? null : map.get("name").toString();
            HttpSession session = httpServletRequest.getSession();
            if (session.getAttribute(UserSessionModel.SESSION_USER_MARK) == null) {
                //设置session
                UserSessionModel bcsUser = new UserSessionModel();
                bcsUser.setUserId(uid);
                bcsUser.setName(name);

                //TODO
                //对用户信息的查询封装操作

                session.setAttribute(UserSessionModel.SESSION_USER_MARK, bcsUser);
            }
        }
        return true;
    }

    /**
     * 请求处理之后，视图渲染之前执行
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求处理之后和视图渲染之后再执行
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Object o,
                                Exception e) throws Exception {

    }
}
