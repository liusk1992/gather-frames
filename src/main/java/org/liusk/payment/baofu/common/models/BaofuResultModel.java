/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.common.models;

import org.apache.commons.lang.StringUtils;
import org.liusk.payment.baofu.common.constants.BaofuResultConstant;

/**
 * 宝付的返回结果类
 * @author liusk
 * @version $Id: BfResultModel.java, v 0.1 2017/9/30 14:52 liusk Exp $
 */
public class BaofuResultModel<T> {

    private String code;

    private String msg;

    private T obj;

    public BaofuResultModel(String code, String msg, T obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    public BaofuResultModel(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getObj() {
        return obj;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public boolean isSuccess() {
        return StringUtils.equals(this.code, BaofuResultConstant.SUCCESS);
    }
}
