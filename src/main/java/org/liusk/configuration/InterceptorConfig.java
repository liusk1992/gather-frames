/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.configuration;

import org.liusk.interceptor.CasInterceptor;
import org.liusk.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author liusk
 * @version $Id: InterceptorConfig.java, v 0.1 2017/9/19 20:05 liusk Exp $
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private CasInterceptor casInterceptor;

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(casInterceptor);
        //registry.addInterceptor(sessionInterceptor);
    }

}
