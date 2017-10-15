/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.config;

/**
 * 宝付支付基础配置
 * @author liusk
 * @version $Id: BaoFuConfig.java, v 0.1 2017/9/30 13:53 liusk Exp $
 */
public class BaoFuConfig {

    /** 商户号 */
    public static final String MEMBER_ID = "1134100";//正式：1134100---100024887

    /** 终端号 */
    public static final String TERMINAL_ID = "33481";//正式：33481---200001044

    /** 商户微信appid */
    public static final String WEIXIN_APP_ID = "";

    /** 商户私钥路径 */
    public static final String PRIVATE_KEY_CERT_NAME = "赶街私钥.pfx";//赶街私钥.pfx--bfkey_100024887@@200001044.pfx

    /** 商户私钥密码 */
    public static final String PRIVATE_KEY_CERT_PWD = "671218";//正式：671218---100024887_751511

    /** 宝付公钥路径 */
    public static final String PUBLIC_KEY_CERT_NAME = "宝付公钥.cer";//宝付公钥.cer--bfkey_100024887@@200001044.cer

    /** 返回数据类型 */
    public static final String DATA_TYPE = "json";

    /** 宝付支付回跳页面 */
    public static final String PAGE_URL = "https://www.baidu.com";

    /** 宝付支付回调地址 */
    public static final String RETURN_URL = "http://baofu.free.ngrok.cc/payment/baofuSdkPayNotify.do";

    /** 宝付支付回调地址:微信支付宝 */
    public static final String RETURN_URL_WXALI = "http://baofu.free.ngrok.cc/payment/baofuAliWechatNotify.do";

    /** 宝付版本号 */
    public static final String VERSION = "4.0.0.0";

    /** 字符集 1表示UTF-8 */
    public static final String CHARSET = "1";

    /** 语言类型 1表示中文 */
    public static final String LANGUAGE = "1";

    /** 证件类型 01表示身份证 */
    public static final String ID_CARD_TYPE = "01";

    /****************************************交易类型********************************************/
    /** 微信支付宝支付使用，值固定 */
    public static final String TXN_TYPE_WXALIPAY = "20199";
    /** 退款使用，值固定 */
    public static final String TXN_TYPE_REFUND   = "331";

    /*******************************************交易子类***************************/
    /** 支付子类，微信APP支付， 04 */
    public static final String TXN_SUB_TYPE_WECHAT       = "04";
    /** 支付子类，支付宝APP支付， 08 */
    public static final String TXN_SUB_TYPE_ALIPAY       = "08";
    /** 退款， 09 */
    public static final String TXN_SUB_TYPE_REFUND       = "09";
    /** 退款查询， 10 */
    public static final String TXN_SUB_TYPE_REFUND_QUERY = "10";

    /** 应答码：交易成功，代码 0000  */
    public static final String RESP_CODE_SUCCESS = "0000";

}
