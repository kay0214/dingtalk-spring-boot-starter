# 钉钉机器人
## 提供日志收集appender和发送钉钉消息的功能
### 配置
dingtalk:  
  &emsp;enable: true  
  &emsp;#app名称 - 提高辨识度  
  &emsp;app-name: test  
  &emsp;#配置文件 - 提高辨识度  
  &emsp;profile: dev  
  &emsp;#支持多个钉钉机器人  
  &emsp;robots[0]:  
    &emsp;&emsp;access-token: 58d2bfc3475de86cfed7f460a5a09ab48227d8ab16f0c60ed6f5c482255c2139  
    &emsp;&emsp;secret: SEC02e46392a956d26e501c8ac50f02e2eb31770ef7d43e89f1658cd812f4e959ca  
  &emsp;at-mobiles: 17685749636,15264225650