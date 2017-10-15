/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common.enums;

/**
 * 宝付订单状态枚举
 * @author liusk
 * @version $Id: TradeStatusEnum.java, v 0.1 2017年10月12日 下午12:52:44 liusk Exp $
 */
public enum BaofuTradeStatusEnum {
    SUCCESS("1", "成功"),

    FAIL("2", "失败"),

    PROCESS("3", "处理中"),

    NOTPAY("4", "未支付");

    private String value;

    private String msg;

    private BaofuTradeStatusEnum(String value, String msg) {
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
