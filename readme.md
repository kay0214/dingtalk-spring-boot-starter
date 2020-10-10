# 钉钉机器人
## 提供日志appender和发送钉钉消息的功能
### 配置
log:  
  &emsp;enable: true  
  &emsp;#模式: all全部; normal普通; minimal极简; custom自定义(自定义模式时必须执行includes). 默认normal  
  &emsp;mode: normal  
  &emsp;#日志内容分隔符,默认英文逗号,  
  &emsp;split: ,  
  &emsp;#显示内容定义.CLASS:显示类名;
  METHOD:显示方法名;
  URI:显示请求路径;
  REQUEST_METHOD:请求方式GET,POST;
  IP:显示客户端IP;
  TIME:显示耗时;
  CONTENT_TYPE:显示请求类型;
  CONTENT_LENGTH:显示请求长度;
  HEADER:显示请求头;
  PARAM_ENCODE:显示请求参数字符集;
  PARAM:显示请求参数;
  RETURN:显示返回参数  
  &emsp;includes: CLASS,METHOD,URI