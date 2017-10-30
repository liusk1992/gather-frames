/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.entity;

import java.util.Date;

/**
 * 
 * @author liusk
 * @version $Id: BaseEntity.java, v 0.1 2017年10月28日 下午1:55:12 liusk Exp $
 */
public class BaseEntity {
    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
