/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author liusk
 * @version $Id: HttpUtil.java, v 0.1 2017/9/22 15:27 liusk Exp $
 */
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 发起https请求并获取结果 ,返回字符串
     * @param requestUrl 请求资源
     * @param method 请求方式（POST,GET）
     * @param output 提交的数据
     * @return 结果字符串
     */
    public static String httpRequest(String requestUrl,String method,String output){
        if(StringUtils.equals("GET",method.toUpperCase())){
            return httpGet(requestUrl);
        }
        return httpPost(requestUrl,output);
    }

    /**
     * post方式访问链接获取数据
     * @param requestUrl 请求资源
     * @param output 提交的数据
     * @return 结果字符串
     */
    public static String httpPost(String requestUrl , String output){
        StringBuffer buffer = new StringBuffer();
        try {
            //建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");

            if(output!=null){
                OutputStream out = connection.getOutputStream();
                out.write(output.getBytes("UTF-8"));
                out.close();
            }
            //流处理
            InputStream input = connection.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input,"UTF-8");
            BufferedReader reader = new BufferedReader(inputReader);
            String line;
            while((line=reader.readLine())!=null){
                buffer.append(line);
            }
            //关闭连接、释放资源
            reader.close();
            inputReader.close();
            input.close();
            input = null;
            connection.disconnect();
        } catch (Exception e) {
            logger.error("{}访问出错，参数：{}",requestUrl,output,e);
        }
        return buffer.toString();
    }

    /**
     * get方式访问链接获取数据
     * @param requestUrl 请求资源
     * @return 结果字符串
     */
    public static String httpGet(String requestUrl) {
        StringBuffer buffer = new StringBuffer();
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = "";
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
            br.close();
            conn.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
