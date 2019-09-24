package com.zhouwei.ruleengine.rpc;

import com.zhouwei.ruleengine.dto.UserDTO;

/**
 * 规则引擎的rpc接口
 * @author zhouwei
 * @create 2019-09-20
 */
public interface RuleEngineRpcService {
    /**
     * 测试方法
     */
    void hello();

    /**
     * 调用hello world
     */
    String helloworldDrools(UserDTO user);
}
