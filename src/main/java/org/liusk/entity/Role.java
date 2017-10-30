/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

/**
 * 
 * @author liusk
 * @version $Id: Role.java, v 0.1 2017年10月28日 下午1:45:20 liusk Exp $
 */
public class Role extends BaseEntity {

    private String roleId;

    private String roleName;

    private String remark;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
