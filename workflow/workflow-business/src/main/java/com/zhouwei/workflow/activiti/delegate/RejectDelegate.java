package com.zhouwei.workflow.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * 拒绝的delegate
 * @author zhouwei
 * @create 2019-09-20
 */
@Component("rejectDelegate")
public class RejectDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("Unfortunately! You were rejected");
    }
}
