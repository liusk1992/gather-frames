/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liusk.payment.alipay.common.constants.AliPayResultConstants;
import org.liusk.payment.alipay.common.enums.ProductCodeEnum;
import org.liusk.payment.alipay.common.models.AliPayResultModel;
import org.liusk.payment.alipay.common.utils.AliPayClientUtil;
import org.liusk.payment.alipay.common.utils.PayCommonUtil;
import org.liusk.payment.alipay.config.AlipayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;

/**
 * 支付宝支付相关请求实现
 * @author liusk
 * @version $Id: PayRequest.java, v 0.1 2017/9/26 16:08 liusk Exp $
 */
public class AliPayRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayRequest.class);

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
    public static String appPay(AlipayTradeAppPayModel model) {
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

        String orderString = null;
        try {
            orderString = client.sdkExecute(app_request).getBody();
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝调起app支付出错", e);
        }
        return orderString;
    }

    /**
     * 手机端h5支付
     * @param model
     * @return
     * @throws AlipayApiException
     */
    public static String hsPay(AlipayTradePagePayModel model) {
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

        String payRequestBody = null;
        try {
            payRequestBody = client.pageExecute(h5_request).getBody();
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝调起支付出错", e);
        }
        return payRequestBody;
    }

    /**
     * 支付宝回调处理
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static AliPayResultModel notify(HttpServletRequest request,
                                           HttpServletResponse response) {
        AliPayResultModel payModel = new AliPayResultModel();
        //获取支付宝POST过来反馈信息
        Map<String, String> params = PayCommonUtil.requestParamsToMap(request);

        //计算得出通知验证结果
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
            if (verify_result) {//验证成功 
                payModel.setCode(AliPayResultConstants.SUCCESS);

                //业务处理中拿到该数据进行处理
                payModel.setData(params);
                payModel.setMsg("支付宝支付回调处理验证成功，请根据交易状态trade_status处理业务逻辑");
            } else {//验证失败
                payModel.setCode(AliPayResultConstants.FAIL);
                payModel.setData(params);
                payModel.setMsg("支付宝支付回调处理验证失败");
            }
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝支付回调处理出错", e);
            payModel.setCode(AliPayResultConstants.FAIL);
            payModel.setData(params);
            payModel.setMsg("支付宝支付回调处理出错");
        }
        return payModel;
    }

    /**
     * 支付宝退款调用
     * @param model 经过封装的退款参数类
     * @return
     * @throws AlipayApiException
     */
    public static AliPayResultModel refund(AlipayTradeRefundModel model) {
        AliPayResultModel payModel = new AliPayResultModel();
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签
        AlipayClient client = AliPayClientUtil.createDefaultAlipayClient();
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizModel(model);
        try {
            AlipayTradeRefundResponse response = client.execute(request);
            if (response.isSuccess()) {
                payModel.setCode(AliPayResultConstants.SUCCESS);
                payModel.setMsg("退款成功");
                payModel.setData(response.getBody());
            } else {
                payModel.setCode(AliPayResultConstants.FAIL);
                payModel.setMsg("退款失败");
                payModel.setData(response.getBody());
            }
        } catch (AlipayApiException e) {
            LOGGER.error("退款出错", e);
            payModel.setCode(AliPayResultConstants.FAIL);
            payModel.setData(e.getErrMsg());
            payModel.setMsg("退款出错");
        }
        return payModel;
    }

}
