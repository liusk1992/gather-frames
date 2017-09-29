/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.weixinpay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.liusk.payment.weixinpay.common.WxPayRequest;
import org.liusk.payment.weixinpay.common.util.PayCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信支付H5处理方法
 * @author liusk
 * @version $Id: WeiXinH5PayController.java, v 0.1 2017年9月25日 下午2:38:47 liusk Exp $
 */
@Controller
@RequestMapping(value = "/WeiXinPay")
public class WeiXinAppPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeiXinAppPayController.class);

    /**
     * 微信统一下单（支付）
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/uniformorder", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uniformorder(HttpServletRequest request,
                               HttpServletResponse response) throws UnsupportedEncodingException {
        //TODO 根据订单号查询出订单相关信息
        String orderNum = "", money = "", subject = "", tradeType = "";
        try {
            JSONObject jsonObject = WxPayRequest.appUniformorder(request, orderNum, money, subject,
                tradeType);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            LOGGER.error("微信统一下单失败,订单号：{}", orderNum, e);
            JSONObject resultObj = new JSONObject();
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "FAIL");
            return resultObj.toJSONString();
        }

    }

    /**
     * 微信支付回调接口
     *
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    @ResponseBody
    public void notify(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        JSONObject resultObj = new JSONObject();
        try {
            JSONObject jsonObject = WxPayRequest.notify(request, response);
            if (StringUtils.equalsIgnoreCase(jsonObject.get("code").toString(), "SUCCESS")) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                String orderNum = (String) data.get("out_trade_no");
                //订单总价
                int totalFee = (Integer) data.get("total_fee");

                //TODO 根据订单编码到数据库查询订单信息，首先查看订单状态是否为已支付，如果已支付直接返回不做处理，
                //如果未支付，比较订单需要支付的金额是否等于totalFee，
                //如果等于totalFee,修改订单状态为支付成功，如果不等于totalFee说明数据经过篡改不做处理

                //返回给微信结果，不管成功失败，都要返回success：ok，这个只是作为收到微信回调的一个回应
                response.getWriter().write(PayCommonUtil.setXml("SUCCESS", "OK"));
            }
        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "FAIL");
            response.getWriter().write(PayCommonUtil.setXml("SUCCESS", "OK"));
        }
    }

    /**
     * 微信退款
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/refund", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String refund(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultObj = new JSONObject();
        String refundNo = "", orderNo = "", totalFee = "", refundFee = "";
        try {
            JSONObject jsonObject = WxPayRequest.refund(refundNo, orderNo, totalFee, refundFee);
            if (StringUtils.equalsIgnoreCase(jsonObject.get("code").toString(), "SUCCESS")) {
                //获取退款成功返回的数据
                JSONObject data = (JSONObject) jsonObject.get("data");

                String refundId = (String) data.get("refund_id");
                System.out.println(refundId);
                resultObj.put("code", "SUCCESS");
                resultObj.put("msg", "退款成功");
            } else {
                resultObj.put("code", "FAIL");
                resultObj.put("msg", "退款失败");
            }
        } catch (Exception e) {
            resultObj.put("msg", e.getMessage());
            resultObj.put("code", "FAIL");
        }
        return resultObj.toJSONString();
    }
}
