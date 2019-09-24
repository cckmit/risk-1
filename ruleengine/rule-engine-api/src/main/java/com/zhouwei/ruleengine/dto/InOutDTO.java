package com.zhouwei.ruleengine.dto;

/**
 * Created by liuchy on 2018/1/26.
 */

public class InOutDTO {

    private String decisionResult = "Approved";



    public void approveDecision() {
        this.decisionResult = "Approved";
    }

    public void rejectDecision() {
        this.decisionResult = "Rejected";
    }

    public void reCheckDecision() {
        this.decisionResult = "ReChecked";
    }

    public String getDecisionResult() {
        return decisionResult;
    }
}
