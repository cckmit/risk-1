package com.zhouwei.workflow.activiti.delegate;

import com.zhouwei.ruleengine.dto.UserDTO;
import com.zhouwei.ruleengine.rpc.RuleEngineRpcService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 调用规则引擎的delegate
 * @author zhouwei
 * @create 2019-09-20
 */
@Component(value = "ruleEngineDelegate")
public class RuleEngineDelegate implements JavaDelegate {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RuleEngineRpcService ruleEngineRpcService;


    @Override
    public void execute(DelegateExecution execution) {
        String result = ruleEngineRpcService.helloworldDrools((UserDTO)runtimeService.getVariable(execution.getId(),"user"));
        System.out.println("rule engine result === "+result);
        runtimeService.setVariable(execution.getId(),"ruleResult",result);
    }



}
