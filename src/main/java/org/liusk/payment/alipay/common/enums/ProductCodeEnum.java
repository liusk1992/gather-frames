/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common.enums;

/**
 * 支付宝销售产品码枚举
 * @author liusk
 * @version $Id: ProductCodeEnum.java, v 0.1 2017年9月28日 下午6:02:07 liusk Exp $
 */
public enum ProductCodeEnum {

    APP("QUICK_MSECURITY_PAY", "app支付"),

    MOBILEWEB("QUICK_WAP_WAY", "手机网站支付"),

    COMPUTERWEB("FAST_INSTANT_TRADE_PAY", "电脑网站支付"),

    SCAN("FACE_TO_FACE_PAYMENT", "扫码支付");

    private String value;

    private String msg;

    private ProductCodeEnum(String value, String msg) {
        this.value = value;
        this.msg = msg;
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
