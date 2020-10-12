/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.actuator;

import com.personal.dingtalk.properties.DingTalkBean;
import com.personal.dingtalk.utils.DingTalkUtil;
import com.personal.dingtalk.utils.SpringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * @author sunpeikai
 * @version DingTalkHealthIndicator, v0.1 2020/10/10 20:50
 * @description
 */
@Component("dingTalk")
public class DingTalkHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        if(SpringUtils.getBean(DingTalkBean.class).getSize() > 0){
            // 把redis系统信息放入
            DingTalkUtil.healthInfo().forEach(builder::withDetail);
            return builder.up().build();
        }else{
            return builder.down().build();
        }
    }
}
