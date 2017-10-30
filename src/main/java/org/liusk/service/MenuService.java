/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.service;

import java.util.ArrayList;
import java.util.List;

import org.liusk.entity.Menu;
import org.springframework.stereotype.Service;

/**
 * 
 * @author liusk
 * @version $Id: MenuService.java, v 0.1 2017年10月28日 下午2:09:36 liusk Exp $
 */
@Service
public class MenuService {

    public List<Menu> queryByRoleId(String roleId) {
        List<Menu> menuList = new ArrayList<Menu>();
        Menu menu = new Menu();
        menu.setMenuName("用户管理");
        menu.setMenuUrl("/user/get");
        menuList.add(menu);

        return menuList;
    }

    public List<Menu> queryByRoleIds(String... roleId) {
        queryByRoleId(roleId[0]);
        return null;
    }

    public List<Menu> queryByUserId(String userId) {
        List<Menu> menuList = new ArrayList<Menu>();
        Menu menu = new Menu();
        menu.setMenuName("用户管理");
        menu.setMenuUrl("/user/queryByUserId");
        menu.setMenuId("2001");
        menuList.add(menu);

        return menuList;
    }

}
