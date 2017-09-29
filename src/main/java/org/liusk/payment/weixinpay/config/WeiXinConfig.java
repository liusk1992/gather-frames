/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.weixinpay.config;

/**
 * 微信基础配置信息
 * @author liusk
 * @version $Id: WeiXinConfig.java, v 0.1 2017年9月25日 下午3:19:27 liusk Exp $
 */
public class WeiXinConfig {

    /** 微信商户号 */
    public static final String MCHID = "********";

    /** 微信开放平台审核通过的应用APPID */
    public static final String APPID = "********";

    /** 微信APIKEY */
    public static final String APIKEY = "************";

    /** 证书路径 */
    public static final String CERTPATH = "D:/apiclient_cert.p12";

    /** 微信支付回调地址 */
    public static final String notify_url = "http://www.odc.com/wxPay/notify.do";
}
