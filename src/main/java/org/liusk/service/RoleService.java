/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.service;

import java.util.ArrayList;
import java.util.List;

import org.liusk.entity.Role;
import org.springframework.stereotype.Service;

/**
 * 
 * @author liusk
 * @version $Id: RoleService.java, v 0.1 2017年10月28日 下午2:09:24 liusk Exp $
 */
@Service
public class RoleService {

    public List<Role> queryByUserId(String userId) {
        List<Role> roleList = new ArrayList<Role>();
        Role role = new Role();
        role.setRoleName("测试角色");
        role.setRoleId("abcdefg");
        roleList.add(role);
        return roleList;
    }

}
