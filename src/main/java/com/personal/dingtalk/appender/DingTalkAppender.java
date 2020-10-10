/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.personal.dingtalk.properties.DingTalkBean;
import com.personal.dingtalk.properties.DingTalkProperties;
import com.personal.dingtalk.sender.DingTalkMessage;
import com.personal.dingtalk.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sunpeikai
 * @version DingTalkAppender, v0.1 2020/10/9 14:45
 * @description
 */
public class DingTalkAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final Logger log = LoggerFactory.getLogger(DingTalkAppender.class);
    private DingTalkProperties dingTalkProperties;
    private DingTalkBean dingTalkBean;
    Layout<ILoggingEvent> layout;

    public DingTalkAppender() {
    }

    @Override
    protected void append(ILoggingEvent event) {
        try {
            if (event != null && this.isStarted()) {
                if (dingTalkProperties == null) {
                    dingTalkProperties = SpringUtils.getBean(DingTalkProperties.class);
                }
                if (dingTalkBean == null || dingTalkBean.getSize() == 0) {
                    dingTalkBean = SpringUtils.getBean(DingTalkBean.class);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("App[")
                        .append(dingTalkProperties.getAppName())
                        .append("] - Profile[")
                        .append(dingTalkProperties.getProfile())
                        .append("]")
                        .append(" - ")
                        .append(layout.doLayout(event));
                int notifySize = dingTalkProperties.getAtMobiles() != null && dingTalkProperties.getAtMobiles().length() > 0
                        ? DingTalkMessage.defaultTextMessageSend(stringBuilder.toString(), dingTalkProperties.getAtMobiles().split(","))
                        : DingTalkMessage.defaultTextMessageSend(stringBuilder.toString());
                log.debug("dingTalkAppender notify group size is [{}]", notifySize);
            }
        } catch (Exception e) {
            log.error("log append fail, exception[{}]", e.getMessage());
        }
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
}
