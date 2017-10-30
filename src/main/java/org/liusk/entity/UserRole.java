/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

/**
 * 
 * @author liusk
 * @version $Id: UserRole.java, v 0.1 2017年10月28日 下午1:47:11 liusk Exp $
 */
public class UserRole extends BaseEntity {

    private Long userId;

    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
