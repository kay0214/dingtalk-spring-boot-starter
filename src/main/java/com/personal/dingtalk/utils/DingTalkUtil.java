/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.utils;

import com.personal.dingtalk.properties.DingTalkBean;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunpeikai
 * @version DingTalkUtil, v0.1 2020/10/9 17:52
 * @description
 */
public class DingTalkUtil {
    private static final Logger log = LoggerFactory.getLogger(DingTalkUtil.class);

    /**
     * 签名
     * */
    public static String getSign(Long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.error("sign create fail, exception[{}]", e.getMessage());
        }
        return "";
    }

    public static Map<String, Object> healthInfo(){
        Map<String, Object> result = new HashMap<>();
        DingTalkBean dingTalkBean = SpringUtils.getBean(DingTalkBean.class);
        result.put("size",dingTalkBean.getSize());
        result.put("clients",dingTalkBean.getDingTalkClients());
        return result;
    }
}
