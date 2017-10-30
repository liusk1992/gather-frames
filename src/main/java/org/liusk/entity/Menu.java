/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

/**
 * 
 * @author liusk
 * @version $Id: Menu.java, v 0.1 2017年10月28日 下午1:46:55 liusk Exp $
 */
public class Menu extends BaseEntity {

    private String menuId;

    private String menuName;

    private String menuUrl;

    private Boolean isEnable;

    private String remark;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
