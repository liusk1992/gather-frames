/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.payment.alipay.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付相关工具方法
 * @author liusk
 * @version $Id: PayCommonUtil.java, v 0.1 2017年9月28日 下午7:46:33 liusk Exp $
 */
public class PayCommonUtil {

    /**
     * request请求中的参数转成map
     * @param request
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> requestParamsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                    : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

}
