/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common.enums;

/**
 * 交易子类枚举
 * @author liusk
 * @version $Id: TxnSubTypeEnum.java, v 0.1 2017年10月9日 下午4:07:11 liusk Exp $
 */
public enum TxnSubTypeEnum {

    TRADE_STATUS("13", "交易状态查询"),

    WEIXIN_PAY("04", "微信app交易"),

    ALI_PAY("08", "支付宝app交易"),

    WEIXIN_SUB_ACCOUNT("14", "微信app交易分账");

    private String value;

    private String msg;

    private TxnSubTypeEnum(String value, String msg) {
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
