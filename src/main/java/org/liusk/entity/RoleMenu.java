/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

/**
 * 
 * @author liusk
 * @version $Id: RoleMenu.java, v 0.1 2017年10月28日 下午1:47:20 liusk Exp $
 */
public class RoleMenu extends BaseEntity {

    private Long roleId;

    private Long menuId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

}
