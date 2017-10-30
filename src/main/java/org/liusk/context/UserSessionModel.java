/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.context;

import java.io.Serializable;

/**
 * @author liusk
 * @version $Id: UserSessionModel.java, v 0.1 2017/9/19 19:47 liusk Exp $
 */
public class UserSessionModel implements Serializable {

    private static final long serialVersionUID = 6934643699591362316L;

    public static final String SESSION_USER_MARK = "session_user";

    private String userId;

    private String username;

    private String name;

    private Integer type;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
