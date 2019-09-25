package com.zhouwei.ruleengine.rpc.service;

import com.zhouwei.ruleengine.dto.InOutDTO;
import com.zhouwei.ruleengine.dto.UserDTO;
import com.zhouwei.ruleengine.rpc.RuleEngineRpcService;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service("ruleEngineRpcService")
public class RuleEngineRpcServiceImpl implements RuleEngineRpcService {

    /**
     * 测试方法
     */
    @Override
    public void hello() {
        System.out.println("hello rule engine!!");
    }

    @Override
    public String helloworldDrools(UserDTO user) {
        System.out.println("hello!!");
        InOutDTO inOutDTO = new InOutDTO();
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        //KieSession kieSession = kieContainer.newKieSession("helloWorldSession");
        KieSession kieSession = kieContainer.newKieSession("demo1Session");
        kieSession.setGlobal("inOutDTO", inOutDTO);
        //加入数据
        kieSession.insert(user);
        //执行规则
        kieSession.fireAllRules();
        kieSession.dispose();
        return inOutDTO.getDecisionResult();
    }
}
