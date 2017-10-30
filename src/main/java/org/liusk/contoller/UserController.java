/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.contoller;

import java.util.HashMap;
import java.util.Map;

import org.liusk.entity.User;
import org.liusk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户控制器
 * @author liusk
 * @version $Id: UserController.java, v 0.1 2017年10月27日 下午2:12:10 liusk Exp $
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userSerivce;

    @RequestMapping(value = "/queryByUserId", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> queryUserById(String userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        User user = userSerivce.queryByUserId(userId);
        map.put("user", user);
        return map;
    }

}
