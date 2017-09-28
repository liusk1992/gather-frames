/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common.enums;

/**
 * 支付宝交易状态枚举
 * @author liusk
 * @version $Id: TradeStatusEmun.java, v 0.1 2017年9月28日 下午5:49:21 liusk Exp $
 */
public enum TradeStatusEmun {

    TRADE_FINISHED("TRADE_FINISHED", "交易完成"),

    TRADE_SUCCESS("TRADE_SUCCESS", "支付成功"),

    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建   "),

    TRADE_CLOSED("TRADE_CLOSED", "交易关闭  ");

    private String value;

    private String msg;

    TradeStatusEmun(String value, String msg) {
        this.msg = msg;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
