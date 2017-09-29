/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.liusk.payment.alipay.common.AliPayRequest;
import org.liusk.payment.alipay.common.enums.TradeStatusEmun;
import org.liusk.payment.alipay.common.models.AliPayResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;

/**
 * 支付宝支付controller
 * @author liusk
 * @version $Id: AliPayController.java, v 0.1 2017年9月27日 下午2:27:20 liusk Exp $
 */
@Controller
@RequestMapping(value = "/aliPay")
public class AliPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayController.class);

    /**
     * 支付宝app支付
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/appPay")
    @ResponseBody
    public String appPay(HttpServletRequest request, HttpServletResponse response) {
        String orderNum = "", subject = "", money = "";
        String orderString = "";
        try {
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(orderNum);
            model.setSubject(subject);
            model.setTotalAmount(money);//单位元
            model.setBody("");
            model.setTimeoutExpress("2m");

            //调用支付宝支付请求
            orderString = AliPayRequest.appPay(model);
        } catch (Exception e) {
            LOGGER.error("支付宝支付失败，单号：{}", orderNum, e);
        }
        return orderString;
    }

    /**
     * 支付宝支付回调
     * @param request
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/notify")
    @ResponseBody
    public void notify(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        try {
            AliPayResultModel payResultModel = AliPayRequest.notify(request, response);

            //支付宝支付回调通知处理验证成功
            if (payResultModel.isSuccess()) {
                Map<String, String> params = (Map<String, String>) payResultModel.getData();
                String tradeStatus = params.get("trade_status");
                String orderNo = params.get("out_trade_no");

                //交易完成
                if (StringUtils.equals(tradeStatus, TradeStatusEmun.TRADE_FINISHED.getValue())) {
                    //TODO 交易完成之后，根据订单号验证订单是否处理，比较订单金额与回调参数金额是否一致
                    System.out.println("订单号:" + orderNo);
                } else
                    if (StringUtils.equals(tradeStatus, TradeStatusEmun.TRADE_SUCCESS.getValue())) {
                    //TODO 交易完成之后，根据订单号验证订单是否处理，比较订单金额与回调参数金额是否一致
                }
                response.getWriter().write("success");
            }
        } catch (Exception e) {
            LOGGER.error("支付宝支付回调处理出错", e);
        }
        response.getWriter().write("fail");
    }

    /**
     * 支付宝退款
     * @return
     */
    @RequestMapping(value = "/refund")
    @ResponseBody
    public String refund() {
        //商户系统订单号，退款金额，退款原因
        String orderNo = "", refundAmount = "", refundReason = "";

        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
        String outRequestNo = "";
        try {
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(orderNo);
            model.setTradeNo("");//支付宝交易号
            model.setRefundAmount(refundAmount);
            model.setRefundReason(refundReason);
            model.setOutRequestNo(outRequestNo);
            AliPayResultModel payResultModel = AliPayRequest.refund(model);
            if (payResultModel.isSuccess()) {
                //退款成功返回的数据
                String responseBody = (String) payResultModel.getData();
                System.out.println(responseBody);
                //TODO 退款成功业务处理
            } else {
                //TODO 退款失败业务处理
            }
        } catch (Exception e) {
            LOGGER.error("退款处理出错", e);
        }
        return "";
    }

}
