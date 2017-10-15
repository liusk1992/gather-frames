/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common.models;

import java.io.Serializable;

/**
 * 宝付风控基本参数集合
 * @author liusk
 * @version $Id: BaofuBaseRiskModel.java, v 0.1 2017年10月9日 下午2:10:11 liusk Exp $
 */
public class BaofuBaseRiskModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 8242728022372141559L;

    /** 行业类目 */
    private String goodsCategory;

    /** 登录名 */
    private String userLoginId;

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(String userLoginId) {
        this.userLoginId = userLoginId;
    }

}
