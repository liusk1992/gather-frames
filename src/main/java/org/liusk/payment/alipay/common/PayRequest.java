/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liusk.payment.alipay.common.constants.AlipayConfig;
import org.liusk.payment.alipay.common.enums.ProductCodeEnum;
import org.liusk.payment.alipay.common.utils.AliPayClientUtil;
import org.liusk.payment.alipay.common.utils.PayCommonUtil;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;

/**
 * 支付宝支付相关请求实现
 * @author liusk
 * @version $Id: PayRequest.java, v 0.1 2017/9/26 16:08 liusk Exp $
 */
public class PayRequest {

    /**
     * 支付宝app发起支付获取orderString
     * @param model 业务参数封装类
     *              AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
     *              model.setOutTradeNo(out_trade_no);
     *              model.setSubject(subject);
     *              model.setTotalAmount(total_amount);
     *              model.setBody(body);
     *              model.setTimeoutExpress(timeout_express);
     *              model.setProductCode(product_code);
     * @return app端需要使用的orderString
     * @throws AlipayApiException 
     */
    public static String appPay(AlipayTradeAppPayModel model) throws AlipayApiException {
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签
        AlipayClient client = AliPayClientUtil.createDefaultAlipayClient();
        //app支付的productCode
        model.setProductCode(ProductCodeEnum.APP.getValue());
        //app支付的request
        AlipayTradeAppPayRequest app_request = new AlipayTradeAppPayRequest();
        // 封装请求支付信息
        app_request.setBizModel(model);
        // 设置异步通知地址
        app_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        app_request.setReturnUrl(AlipayConfig.return_url);

        String orderString = client.sdkExecute(app_request).getBody();
        return orderString;
    }

    /**
     * 手机端h5支付
     * @param model
     * @return
     * @throws AlipayApiException
     */
    public static String hsPay(AlipayTradePagePayModel model) throws AlipayApiException {
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签
        AlipayClient client = AliPayClientUtil.createDefaultAlipayClient();
        //h5支付的productCode
        model.setProductCode(ProductCodeEnum.MOBILEWEB.getValue());
        //app支付的request
        AlipayTradePagePayRequest h5_request = new AlipayTradePagePayRequest();
        // 封装请求支付信息
        h5_request.setBizModel(model);
        // 设置异步通知地址
        h5_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        h5_request.setReturnUrl(AlipayConfig.return_url);
        String payRequestBody = client.pageExecute(h5_request).getBody();
        return payRequestBody;
    }

    /**
     * 支付宝app发起支付获取orderString
     * @param bizContent 业务参数json字符串
     * @return app端需要使用的orderString
     * @throws AlipayApiException 
     */
    public static String pay(String bizContent) throws AlipayApiException {
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签
        AlipayClient client = AliPayClientUtil.createDefaultAlipayClient();

        //app支付的request
        AlipayTradeAppPayRequest app_request = new AlipayTradeAppPayRequest();
        //设置业务参数
        app_request.setBizContent(bizContent);
        // 设置异步通知地址
        app_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        app_request.setReturnUrl(AlipayConfig.return_url);

        String orderString = client.sdkExecute(app_request).getBody();
        return orderString;
    }

    /**
     * 支付宝回调处理
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException 
     */
    @SuppressWarnings("rawtypes")
    public static String notify(HttpServletRequest request, HttpServletResponse response) {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = PayCommonUtil.requestParamsToMap(request);
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表
        //商户订单号
        String out_trade_no = params.get("out_trade_no");
        //支付宝交易号
        String trade_no = params.get("trade_no");
        //交易状态
        String trade_status = params.get("trade_status");

        //计算得出通知验证结果
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (verify_result) {//验证成功 
            if ("TRADE_FINISHED".equals(trade_status)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if ("TRADE_SUCCESS".equals(trade_status)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            }

        } else {//验证失败
        }
        return null;
    }

}
