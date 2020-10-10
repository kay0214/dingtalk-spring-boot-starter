/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.actuator;

import com.personal.dingtalk.utils.DingTalkUtil;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.Map;

/**
 * @author sunpeikai
 * @version DingTalkActuatorEndpoint, v0.1 2020/10/10 20:47
 * @description
 */
@WebEndpoint(id = "dingTalk")
public class DingTalkActuatorEndpoint {
    @ReadOperation
    public Map<String, Object> dingTalkActuator() {
        return DingTalkUtil.healthInfo();
    }
}
