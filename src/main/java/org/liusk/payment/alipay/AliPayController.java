/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liusk.payment.alipay.common.PayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.domain.AlipayTradeAppPayModel;

/**
 * 支付宝支付controller
 * @author liusk
 * @version $Id: AliPayController.java, v 0.1 2017年9月27日 下午2:27:20 liusk Exp $
 */
@Controller
@RequestMapping(value = "/aliPay")
public class AliPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayController.class);

    @RequestMapping(value = "/appPay")
    @ResponseBody
    public String appPay(HttpServletRequest request, HttpServletResponse response) {
        String orderNum = "", subject = "", money = "";
        try {
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(orderNum);
            model.setSubject(subject);
            model.setTotalAmount(money);//单位元
            model.setBody("");
            model.setTimeoutExpress("2m");
            String orderString = PayRequest.appPay(model);
            return orderString;
        } catch (Exception e) {
            LOGGER.error("支付宝支付失败，单号：{}", orderNum, e);
            return "";
        }
    }

}
