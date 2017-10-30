/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

/**
 * 
 * @author liusk
 * @version $Id: User.java, v 0.1 2017年10月28日 下午1:45:12 liusk Exp $
 */
public class User extends BaseEntity {

    private String userId;

    private String account;

    private String name;

    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
