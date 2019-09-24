package com.zhouwei.workflow.activiti.delegate;

import com.zhouwei.ruleengine.dto.UserDTO;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 查找用户数据的delegate
 * @author zhouwei
 * @create 2019-09-20
 */
@Component("queryUserDelegate")
public class QueryUserDelegate implements JavaDelegate {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("enter queryUserDelegate! welcome!");
        System.out.println("here you can query data you want!!");
        runtimeService.setVariable(execution.getId(), "user",new UserDTO("test",20));
    }
}
