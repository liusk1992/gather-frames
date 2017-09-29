/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common.utils;

import org.liusk.payment.alipay.config.AlipayConfig;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

/**
 * AliPayClient工具类
 * @author liusk
 * @version $Id: AliPayClientUtil.java, v 0.1 2017/9/26 16:03 liusk Exp $
 */
public class AliPayClientUtil {

    /**
     * 获取默认的AliPayClient
     * @return
     */
    public static AlipayClient createDefaultAlipayClient() {
        return new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID,
            AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET,
            AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
    }

}
