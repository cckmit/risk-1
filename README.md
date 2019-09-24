# risk
activiti+drools+dubbo搭建的风控系统的demo


一、整体结构
workflow ： 基于activiti搭建的流程引擎
ruleengine ：基于drools搭建的规则引擎
kernel ：  框架包

二、启动说明
将workflow中的datasource.properties改为本地需要的数据库配置，需将zookeeper服务以以下格式配置到host中
ip1 zoo1
ip2 zoo2
ip3 zoo3

启动时先启动ruleengine，再启动workflow

整体的调用链为 外部业务只需要在workflow中启动工作流，workflow根据不同的工作流取外部数据及调用ruleengine的规则，目前为简化处理workflow对外提供的rest接口，workflow和engine内部的交互使用dubbo。如果有需要，交互方式可按需变更，例如采用消息队列等，为方便测试统一提供对外暴露的接口在WorkFlowController中

三、bpmn文件和drl文件配置说明
 bpmn文件放在 workflow   resource/bpmn 目录下，spring-activiti.xml已默认读取该目录下的工作流文件
 drl文件放在ruleengine   resource/rule 目录下，增加规则需要对应修改resource/META-INF/kmodule.xml
