/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.service;

import org.liusk.entity.User;
import org.springframework.stereotype.Service;

/**
 * 
 * @author liusk
 * @version $Id: UserService.java, v 0.1 2017年10月28日 下午1:43:30 liusk Exp $
 */
@Service
public class UserService {

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    public User queryByUserId(String userId) {
        User user = new User();
        user.setAccount("1001");
        user.setName("刘少康");
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        return user;
    }

    public User queryByAccount(String account) {
        User user = new User();
        user.setAccount("1001");
        user.setName("刘少康");
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        return user;
    }

}
