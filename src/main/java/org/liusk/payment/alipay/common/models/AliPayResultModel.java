/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common.models;

import org.apache.commons.lang.StringUtils;
import org.liusk.payment.alipay.common.constants.AliPayResultConstants;

/**
 * 支付宝支付请求处理结果封装
 * @author liusk
 * @version $Id: AliPayResultModel.java, v 0.1 2017年9月29日 上午11:16:09 liusk Exp $
 */
public class AliPayResultModel {

    /** 返回代码 */
    private String code;

    /** 返回信息 */
    private String msg;

    /** 返回数据 */
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return StringUtils.equals(this.code, AliPayResultConstants.SUCCESS);
    }

}
