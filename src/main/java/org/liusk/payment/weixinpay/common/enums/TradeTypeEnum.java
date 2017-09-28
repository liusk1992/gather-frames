/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.weixinpay.common.enums;

/**
 * 微信支付的交易类型
 * @author liusk
 * @version $Id: TradeTypeEnum.java, v 0.1 2017年9月28日 下午4:48:14 liusk Exp $
 */
public enum TradeTypeEnum {

    APP("APP"), H5("MWEB"), JSAPI("JSAPI");

    private String value;

    TradeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
