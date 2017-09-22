/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.contoller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liusk
 * @version $Id: IndexContoller.java, v 0.1 2017/9/6 19:52 liusk Exp $
 */
@Controller
@RequestMapping("/index")
public class IndexContoller {

    @RequestMapping(value = "/test",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String testIndex(){
        return "aaa";
    }

    /**
     * 系统中判断用户session为空时重定向的地址，这个地址会被cas拦截，
     * 如果未在cas中心服务器登录认证，会先跳转到cas服务器登陆，登陆成功后回调到该地址
     * @return
     */
    @RequestMapping(value = "/casLogin", method = { RequestMethod.GET, RequestMethod.POST })
    public String casLogin(String indexPath) {
        if (StringUtils.isBlank(indexPath)) {
            return "zh-CN/baseData/baseData.htm";
        }
        return indexPath;
    }

}
