/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.properties;

import com.dingtalk.api.DingTalkClient;

import java.util.List;

/**
 * @author sunpeikai
 * @version DingTalkClient, v0.1 2020/10/9 17:37
 * @description
 */
public class DingTalkBean {
    private int size;
    private List<DingTalkClient> dingTalkClients;

    public DingTalkBean(int size, List<DingTalkClient> dingTalkClients) {
        this.size = size;
        this.dingTalkClients = dingTalkClients;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DingTalkClient> getDingTalkClients() {
        return dingTalkClients;
    }

    public void setDingTalkClients(List<DingTalkClient> dingTalkClients) {
        this.dingTalkClients = dingTalkClients;
    }
}
