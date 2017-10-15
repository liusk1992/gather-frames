/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common.models;

import java.io.Serializable;

/**
 * 宝付app支付发起支付时用到的参数集合
 * @author liusk
 * @version $Id: BaofuAppPayModel.java, v 0.1 2017年10月9日 下午1:59:11 liusk Exp $
 */
public class BaofuAppPayModel implements Serializable {

    /**  */
    private static final long serialVersionUID = 8003962488430921182L;

    /** 身份标识 */
    private String idCardNo;

    /** 持卡人姓名 */
    private String holderName;

    /** 订单号 */
    private String orderNo;

    /** 流水号 */
    private String transSerialNo;

    /** 总金额 */
    private String txnAmt;

    /** 商品名称 */
    private String commodityName;

    /** 商品个数 */
    private String commodityAmount;

    /** 附加信息 */
    private String additionalInfo;

    /** 请求方保留域，可不填  */
    private String reqReserved;

    /** 用户银行卡与宝付绑定的id */
    private String bindId;

    /** 用户id */
    private String userId;

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTransSerialNo() {
        return transSerialNo;
    }

    public void setTransSerialNo(String transSerialNo) {
        this.transSerialNo = transSerialNo;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityAmount() {
        return commodityAmount;
    }

    public void setCommodityAmount(String commodityAmount) {
        this.commodityAmount = commodityAmount;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getReqReserved() {
        return reqReserved;
    }

    public void setReqReserved(String reqReserved) {
        this.reqReserved = reqReserved;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
