/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 
 * @author liusk
 * @version $Id: ApplicationStartup.java, v 0.1 2017年10月30日 下午2:04:23 liusk Exp $
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    /** 
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("项目启动成功");
    }

}
