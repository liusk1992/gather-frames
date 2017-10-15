/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.baofu.config;

/**
 * 宝付的交易接口地址
 * @author liusk
 * @version $Id: BfTransferUrls.java, v 0.1 2017年10月9日 上午11:48:35 liusk Exp $
 */
public class BaofuTransferUrls {

    /** 快捷支付sdk生成订单的接口地址 */
    public static final String SDK_PREORDER = "https://gw.baofoo.com/quickpay/sdk/prepareorder";

    /** 快捷支付交易查询接口地址 */
    public static final String SDK_QUERY = "https://gw.baofoo.com/quickpay/sdk/queryorder";

    /** 快捷支付绑定状态查询接口地址 */
    public static final String SDK_QUICK_PAY_BIND_QUERY = "https://gw.baofoo.com/quickpay/sdk/querybind";

    /** 宝付通过微信支付宝支付接口 */
    public static final String WX_ALI_PAY = "https://public.baofoo.com/platform/gateway/back";

    /** 宝付退款和退款查询接口地址（退款和退款查询是这一个接口） */
    public static final String REFUND = "https://tgw.baofoo.com/cutpayment/api/backTransRequest";

}
