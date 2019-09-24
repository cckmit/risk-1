package com.zhouwei.workflow.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * 通过的delegate
 * @author zhouwei
 * @create 2019-09-20
 */
@Component("approveDelegate")
public class ApproveDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("Congratulations! You passed.");
    }
}
