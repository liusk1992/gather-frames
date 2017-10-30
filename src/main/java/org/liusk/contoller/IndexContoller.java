/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.contoller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.liusk.common.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liusk
 * @version $Id: IndexContoller.java, v 0.1 2017/9/6 19:52 liusk Exp $
 */
@Controller
@RequestMapping("/index")
public class IndexContoller {

    @RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public String testIndex() throws IOException {
        String path = WebUtil.getServerPath();
        String np = URLDecoder.decode(path, "UTF-8");
        File certs = new File(np + "certs/baofu/bfkey_100024887@@200001044.cer");
        BufferedReader br = new BufferedReader(new FileReader(certs));
        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("--------" + certs.length() + np + "---------");
        return "aaa";
    }

    /**
     * 系统中判断用户session为空时重定向的地址，这个地址会被cas拦截，
     * 如果未在cas中心服务器登录认证，会先跳转到cas服务器登陆，登陆成功后回调到该地址
     * @return
     */
    @RequestMapping(value = "/casLogin", method = { RequestMethod.GET, RequestMethod.POST })
    public String casLogin(String indexPath) {
        if (StringUtils.isBlank(indexPath)) {
            return "zh-CN/baseData/baseData.htm";
        }
        return indexPath;
    }

}
