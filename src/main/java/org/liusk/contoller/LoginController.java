/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.contoller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.liusk.common.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author liusk
 * @version $Id: LoginController.java, v 0.1 2017年10月27日 下午2:24:33 liusk Exp $
 */
@Controller
public class LoginController {

    // 登录
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, String account, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        UsernamePasswordToken token = new UsernamePasswordToken(account, password,
            WebUtil.getHost(request));
        token.setRememberMe(true);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        if (subject.isAuthenticated()) {
            map.put("msg", "登陆成功");
            return map;
        }
        map.put("msg", "登录失败");
        return map;
    }

}
