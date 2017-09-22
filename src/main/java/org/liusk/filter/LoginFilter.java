/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author liusk
 * @version $Id: LoginFilter.java, v 0.1 2017/9/22 14:02 liusk Exp $
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String[] noNeedFilterUrls = {"/login"};

        //不需要拦截的请求直接通过


        HttpSession session = request.getSession();
    }

    @Override
    public void destroy() {

    }
}
