/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.dingtalk.sender;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.personal.dingtalk.properties.DingTalkBean;
import com.personal.dingtalk.utils.DingTalkUtil;
import com.personal.dingtalk.utils.SpringUtils;
import com.taobao.api.ApiException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author sunpeikai
 * @version DingTalkMessage, v0.1 2020/10/9 16:54
 * @description
 */
public class DingTalkMessage {

    /**
     * 消息类型
     * */
    private String msgtype;

    /**
     * 提醒谁看
     * */
    private At at;

    /**
     * 文本消息
     * */
    private Text text;

    /**
     * 链接消息
     * */
    private Link link;

    /**
     * markdown消息
     * */
    private Markdown markdown;

    /**
     * 跳转卡片
     * */
    private ActionCard actionCard;

    /**
     * 反馈卡片
     * */
    private FeedCard feedCard;

    /**
     * 建造者模式
     * */
    public static DingTalkMessage builder(){
        return new DingTalkMessage();
    }

    /**
     * 设置消息时的参数检查
     * */
    private void check(String msgtype){
        Assert.isTrue(this.msgtype == null || this.msgtype.length() == 0 || this.msgtype.equals(msgtype), "message type [" + this.msgtype + "],[" + msgtype + "] can not be built at the same time");
        // 设置消息类型
        this.msgtype = msgtype;
    }

    /**
     * 最后的参数检查
     * */
    private void check(){
        // msgtype非空检查
        Assert.isTrue(this.msgtype != null && this.msgtype.length() > 0,"there is no message to build");
        // 各个部分的非空检查
        switch (this.msgtype){
            case "text":
                this.text.check();
                Assert.isTrue(this.link == null && this.markdown == null && this.actionCard == null && this.feedCard == null,"only one message to build, but found many part");
                break;
            case "link":
                this.link.check();
                Assert.isTrue(this.text == null && this.markdown == null && this.actionCard == null && this.feedCard == null,"only one message to build, but found many part");
                break;
            case "markdown":
                this.markdown.check();
                Assert.isTrue(this.text == null && this.link == null && this.actionCard == null && this.feedCard == null,"only one message to build, but found many part");
                break;
            case "actionCard":
                this.actionCard.check();
                Assert.isTrue(this.text == null && this.link == null && this.markdown == null && this.feedCard == null,"only one message to build, but found many part");
                break;
            case "feedCard":
                this.feedCard.check();
                Assert.isTrue(this.text == null && this.link == null && this.markdown == null && this.actionCard == null,"only one message to build, but found many part");
                break;
        }
    }

    /**
     * 基础消息发送 - 发送到配置文件中设置的钉钉群
     * */
    public static int defaultTextMessageSend(String content){
        return builder().text().content(content).and().send();
    }

    /**
     * 基础消息发送 - 发送到配置文件中设置的钉钉群
     * */
    public static int defaultTextMessageSend(String content, String... atMobiles){
        return builder().at().at(atMobiles).and().text().content(content).and().send();
    }

    /**
     * 基础消息发送 - 发送到指定顶顶群 - 不带secret
     * */
    public static int defaultTextMessageSend(String content, String accessKey){
        return builder().text().content(content).and().send(accessKey);
    }

    /**
     * 基础消息发送 - 发送到指定顶顶群并at群成员 - 不带secret
     * */
    public static int defaultTextMessageSend(String content, String accessKey, String...atMobiles){
        return builder().at().at(atMobiles).and().text().content(content).and().send(accessKey);
    }

    /**
     * 基础消息发送 - 发送到指定顶顶群 - 带secret
     * */
    public static int defaultTextMessageSend(String content, String accessKey, String secret){
        return builder().text().content(content).and().send(accessKey, secret);
    }

    /**
     * 基础消息发送 - 发送到指定顶顶群并at群成员 - 带secret
     * */
    public static int defaultTextMessageSend(String content, String accessKey, String secret, String...atMobiles){
        return builder().at().at(atMobiles).and().text().content(content).and().send(accessKey, secret);
    }

    /**
     * 发送到配置文件中设置的钉钉群
     * */
    public int send(){
        DingTalkBean dingTalkBean = SpringUtils.getBean(DingTalkBean.class);
        int size = 0;
        if(dingTalkBean.getSize() > 0){
            for (DingTalkClient client : dingTalkBean.getDingTalkClients()) {
                size += send(client);
            }
        }
        return size;
    }

    /**
     * 发送到指定顶顶群 - 不带secret
     * */
    public int send(String accessKey){
        return send(accessKey, null);
    }

    public int send(Map<String, String> accessKeyAndSecret){
        int size = 0;
        for (Map.Entry<String, String> entry : accessKeyAndSecret.entrySet()) {
            String accessKey = entry.getKey();
            String secret = entry.getValue();
            size += send(accessKey, secret);
        }
        return size;
    }

    /**
     * 发送到指定顶顶群 - 带secret
     * */
    public int send(String accessKey, String secret){
        String url = "https://oapi.dingtalk.com/robot/send?access_token=";
        Assert.isTrue(accessKey != null && accessKey.length() > 0,"accessKey must not be empty");
        if(StringUtils.isEmpty(secret)){
            // 密钥为空 - 不需要签名
            return send(new DefaultDingTalkClient(url + accessKey));
        }else{
            // 密钥不为空 - 需要签名
            long timestamp = System.currentTimeMillis();
            return send(new DefaultDingTalkClient(url + accessKey
                    + "&timestamp=" + timestamp
                    + "&sign=" + DingTalkUtil.getSign(timestamp, secret)));
        }
    }

    /**
     * 发送到指定顶顶群 - 基础发送
     * */
    private int send(DingTalkClient client){
        // 参数检查
        this.check();
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        if(this.msgtype != null && this.msgtype.length() > 0){
            request.setMsgtype(this.msgtype);
        }
        if(this.at != null){
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            BeanUtils.copyProperties(this.at, at);
            request.setAt(at);
        }
        if(this.text != null){
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            BeanUtils.copyProperties(this.text, text);
            request.setText(text);
        }
        if(this.link != null){
            OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
            BeanUtils.copyProperties(this.link, link);
            request.setLink(link);
        }
        if(this.markdown != null){
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            BeanUtils.copyProperties(this.markdown, markdown);
            request.setMarkdown(markdown);
        }
        if(this.actionCard != null){
            OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
            BeanUtils.copyProperties(this.actionCard, actionCard);
            request.setActionCard(actionCard);
        }
        if(this.feedCard != null){
            OapiRobotSendRequest.Feedcard feedCard = new OapiRobotSendRequest.Feedcard();
            BeanUtils.copyProperties(this.feedCard, feedCard);
            request.setFeedCard(feedCard);
        }
        try{
            OapiRobotSendResponse response = client.execute(request);
            return (response != null && response.getErrcode() == 0L) ? 1 : 0;
        }catch (ApiException e){
            return 0;
        }

    }

    //**********          分类建造          **********//
    /**
     * 设置at
     * */
    public At at(){
        if(this.at == null){
            this.at = new At();
        }
        return this.at;
    }

    /**
     * 设置text文本
     * */
    public Text text(){
        // 检查消息类型是否冲突
        check("text");
        if(this.text == null){
            this.text = new Text();
        }
        return this.text;
    }

    /**
     * 设置链接
     * */
    public Link link(){
        // 检查消息类型是否冲突
        check("link");
        if(this.link == null){
            this.link = new Link();
        }
        return this.link;
    }

    /**
     * 设置链接
     * */
    public Markdown markdown(){
        // 检查消息类型是否冲突
        check("markdown");
        if(this.markdown == null){
            this.markdown = new Markdown();
        }
        return this.markdown;
    }

    /**
     * 设置链接
     * */
    public ActionCard actionCard(){
        // 检查消息类型是否冲突
        check("actionCard");
        if(this.actionCard == null){
            this.actionCard = new ActionCard();
        }
        return this.actionCard;
    }

    /**
     * 设置链接
     * */
    public FeedCard feedCard(){
        // 检查消息类型是否冲突
        check("feedCard");
        if(this.feedCard == null){
            this.feedCard = new FeedCard();
        }
        return this.feedCard;
    }


    //**********          基类          **********//
    private abstract class Message{
        public DingTalkMessage and(){
            return DingTalkMessage.this;
        }

        protected abstract void check();
    }
    //**********          提醒谁看          **********//
    public class At extends Message{
        private List<String> atMobiles;
        private boolean isAtAll = false;

        public List<String> getAtMobiles() {
            return this.atMobiles;
        }

        public boolean getIsAtAll() {
            return this.isAtAll;
        }

        /**
         * at一个手机号,满足condition才去执行add
         * */
        public At at(boolean condition, String atMobile){
            if(condition){
                return at(atMobile);
            }
            return this;
        }

        /**
         * at一个手机号
         * */
        public At at(String atMobile){
            // 初始化
            if(this.atMobiles == null){
                this.atMobiles = new ArrayList<>();
            }
            // 放入
            this.atMobiles.add(atMobile);
            return this;
        }

        /**
         * at多个手机号,满足condition才去执行add
         * */
        public At at(boolean condition, String... atMobiles){
            if(condition){
                return at(atMobiles);
            }
            return this;
        }

        /**
         * at多个手机号
         * */
        public At at(String... atMobiles){
            return at(Arrays.asList(atMobiles));
        }

        /**
         * at多个手机号,满足condition才去执行add
         * */
        public At at(boolean condition, Collection<String> atMobile){
            if(condition){
                return at(atMobile);
            }
            return this;
        }

        /**
         * at多个手机号
         * */
        public At at(Collection<String> atMobiles){
            // 初始化
            if(this.atMobiles == null){
                this.atMobiles = new ArrayList<>();
            }
            // 放入
            this.atMobiles.addAll(atMobiles);
            return this;
        }

        /**
         * at所有人
         * */
        public At atAll(){
            return atAll(true);
        }

        /**
         * at所有人
         * */
        public At atAll(boolean isAtAll){
            // 设置@所有人
            this.isAtAll = isAtAll;
            return this;
        }

        @Override
        protected void check() {
            // at不需要check
        }
    }

    //**********          文本消息          **********//
    public class Text extends Message{
        private String content;

        public String getContent() {
            return content;
        }

        public Text content(String content){
            this.content = content;
            return this;
        }

        public Text append(String content){
            this.content += content;
            return this;
        }

        @Override
        protected void check() {
            Assert.isTrue(this.content != null && this.content.length() > 0, "[text] content can not be empty");
        }
    }

    //**********          链接消息          **********//
    public class Link extends Message{
        private String messageUrl;
        private String picUrl;
        private String text;
        private String title;

        public String getMessageUrl() {
            return messageUrl;
        }

        public Link messageUrl(String messageUrl) {
            this.messageUrl = messageUrl;
            return this;
        }

        public Link appendMessageUrl(String messageUrl) {
            this.messageUrl += messageUrl;
            return this;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public Link picUrl(String picUrl) {
            this.picUrl = picUrl;
            return this;
        }

        public Link appendPicUrl(String picUrl) {
            this.picUrl += picUrl;
            return this;
        }

        public String getText() {
            return text;
        }

        public Link text(String text) {
            this.text = text;
            return this;
        }

        public Link appendText(String text) {
            this.text += text;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Link title(String title) {
            this.title = title;
            return this;
        }

        public Link appendTitle(String title) {
            this.title += title;
            return this;
        }

        @Override
        protected void check() {
            Assert.isTrue(this.messageUrl != null && this.messageUrl.length() > 0, "[link] messageUrl can not be empty");
            Assert.isTrue(this.text != null && this.text.length() > 0, "[link] text can not be empty");
            Assert.isTrue(this.title != null && this.title.length() > 0, "[link] title can not be empty");
        }
    }

    //**********          markdown消息          **********//
    public class Markdown extends Message{
        private String text;
        private String title;

        public String getText() {
            return this.text;
        }

        public Markdown text(String text) {
            this.text = text;
            return this;
        }

        public Markdown appendText(String text) {
            this.text += text;
            return this;
        }

        public String getTitle() {
            return this.title;
        }

        public Markdown title(String title) {
            this.title = title;
            return this;
        }

        public Markdown appendTitle(String title) {
            this.title += title;
            return this;
        }

        @Override
        protected void check() {
            Assert.isTrue(this.text != null && this.text.length() > 0, "[markdown] text can not be empty");
            Assert.isTrue(this.title != null && this.title.length() > 0, "[markdown] title can not be empty");
        }
    }

    //**********          跳转卡片          **********//
    public class ActionCard extends Message{
        private String btnOrientation;
        private List<Btns> btns;
        private String hideAvatar;
        private String singleTitle;
        private String singleURL;
        private String text;
        private String title;

        public String getBtnOrientation() {
            return this.btnOrientation;
        }

        /**
         * 按钮横向排列
         * */
        public ActionCard btnHorizontally() {
            this.btnOrientation = "1";
            return this;
        }

        /**
         * 按钮竖直排列
         * */
        public ActionCard btnVertically() {
            this.btnOrientation = "0";
            return this;
        }

        public List<Btns> getBtns() {
            return btns;
        }

        public ActionCard btn(String btnURL, String btnTitle) {
            if(this.btns == null){
                this.btns = new ArrayList<>();
            }
            this.btns.add(new Btns(btnURL, btnTitle));
            return this;
        }

        public ActionCard btn(Btns btn) {
            if(this.btns == null){
                this.btns = new ArrayList<>();
            }
            this.btns.add(btn);
            return this;
        }

        public ActionCard btns(Collection<Btns> btns) {
            if(this.btns == null){
                this.btns = new ArrayList<>();
            }
            this.btns.addAll(btns);
            return this;
        }

        public String getHideAvatar() {
            return this.hideAvatar;
        }

        public ActionCard hideAvatar(String hideAvatar) {
            this.hideAvatar = hideAvatar;
            return this;
        }

        public ActionCard appendHideAvatar(String hideAvatar) {
            this.hideAvatar += hideAvatar;
            return this;
        }

        public String getSingleTitle() {
            return this.singleTitle;
        }

        public ActionCard singleTitle(String singleTitle) {
            this.singleTitle = singleTitle;
            return this;
        }

        public ActionCard appendSingleTitle(String singleTitle) {
            this.singleTitle += singleTitle;
            return this;
        }

        public String getSingleURL() {
            return this.singleURL;
        }

        public ActionCard singleURL(String singleURL) {
            this.singleURL = singleURL;
            return this;
        }

        public ActionCard appendSingleURL(String singleURL) {
            this.singleURL += singleURL;
            return this;
        }

        public String getText() {
            return this.text;
        }

        public ActionCard text(String text) {
            this.text = text;
            return this;
        }

        public ActionCard appendText(String text) {
            this.text += text;
            return this;
        }

        public String getTitle() {
            return this.title;
        }

        public ActionCard title(String title) {
            this.title = title;
            return this;
        }

        public ActionCard appendTitle(String title) {
            this.title += title;
            return this;
        }

        @Override
        protected void check() {
            Assert.isTrue(this.text != null && this.text.length() > 0, "[actionCard] text can not be empty");
            Assert.isTrue(this.title != null && this.title.length() > 0, "[actionCard] title can not be empty");
            if(this.btns != null && this.btns.size() > 0){
                // 有定义按钮 - 独立跳转
                this.btns.forEach(btn -> {
                    Assert.isTrue(btn.actionURL != null && btn.actionURL.length() > 0, "[actionCard] button actionURL can not be empty");
                    Assert.isTrue(btn.title != null && btn.title.length() > 0, "[actionCard] button title can not be empty");
                });
            }else{
                // 没有定义按钮 - 整体跳转
                Assert.isTrue(this.singleTitle != null && this.singleTitle.length() > 0, "[actionCard] singleTitle can not be empty");
                Assert.isTrue(this.singleURL != null && this.singleURL.length() > 0, "[actionCard] singleURL can not be empty");
            }
        }
    }


    public static class Btns {
        private String actionURL;
        private String title;

        public Btns() {
        }

        public Btns(String actionURL, String title) {
            this.actionURL = actionURL;
            this.title = title;
        }

        public String getActionURL() {
            return this.actionURL;
        }

        public Btns actionURL(String actionURL) {
            this.actionURL = actionURL;
            return this;
        }

        public Btns appendActionURL(String actionURL) {
            this.actionURL += actionURL;
            return this;
        }

        public String getTitle() {
            return this.title;
        }

        public Btns title(String title) {
            this.title = title;
            return this;
        }

        public Btns appendTitle(String title) {
            this.title += title;
            return this;
        }
    }

    //**********          反馈卡片          **********//
    public class FeedCard extends Message{
        private List<Links> links;

        public List<Links> getLinks() {
            return this.links;
        }

        public FeedCard link(String linkMessageUrl, String linkPicUrl, String linkTitle) {
            if(this.links == null){
                this.links = new ArrayList<>();
            }
            this.links.add(new Links(linkMessageUrl, linkPicUrl, linkTitle));
            return this;
        }

        public FeedCard link(Links link) {
            if(this.links == null){
                this.links = new ArrayList<>();
            }
            this.links.add(link);
            return this;
        }

        public FeedCard links(Collection<Links> links) {
            if(this.links == null){
                this.links = new ArrayList<>();
            }
            this.links.addAll(links);
            return this;
        }

        @Override
        protected void check() {
            Assert.isTrue(this.links != null && this.links.size() > 0, "[feedCard] links can not be empty");
            this.links.forEach(link -> {
                Assert.isTrue(link.messageURL != null && link.messageURL.length() > 0, "[feedCard] messageURL can not be empty");
                Assert.isTrue(link.picURL != null && link.picURL.length() > 0, "[feedCard] picURL can not be empty");
                Assert.isTrue(link.title != null && link.title.length() > 0, "[feedCard] title can not be empty");
            });
        }
    }

    public static class Links {
        private String messageURL;
        private String picURL;
        private String title;

        public Links() {
        }

        public Links(String messageURL, String picURL, String title) {
            this.messageURL = messageURL;
            this.picURL = picURL;
            this.title = title;
        }

        public String getMessageURL() {
            return messageURL;
        }

        public Links messageURL(String messageURL) {
            this.messageURL = messageURL;
            return this;
        }

        public Links appendMessageURL(String messageURL) {
            this.messageURL += messageURL;
            return this;
        }

        public String getPicURL() {
            return picURL;
        }

        public Links picURL(String picURL) {
            this.picURL = picURL;
            return this;
        }

        public Links appendPicURL(String picURL) {
            this.picURL += picURL;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Links title(String title) {
            this.title = title;
            return this;
        }

        public Links appendTitle(String title) {
            this.title += title;
            return this;
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public At getAt() {
        return at;
    }

    public Text getText() {
        return text;
    }

    public Link getLink() {
        return link;
    }

    public Markdown getMarkdown() {
        return markdown;
    }

    public ActionCard getActionCard() {
        return actionCard;
    }

    public FeedCard getFeedCard() {
        return feedCard;
    }
}
