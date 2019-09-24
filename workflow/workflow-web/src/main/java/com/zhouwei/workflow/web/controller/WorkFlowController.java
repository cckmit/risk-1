package com.zhouwei.workflow.web.controller;

import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/workflow")
public class WorkFlowController {
    @Autowired
    private RuntimeService runtimeService;


    @ResponseBody
    @RequestMapping(value = "/hello")
    public Object hello(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<>();
        result.put("code","SUCCESS");
        result.put("msg","welcome to workflow");
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/demo1")
    public Object demo1(HttpServletRequest request) throws Exception {
        runtimeService.startProcessInstanceByKey("demo1");
        Map<String,String> result = new HashMap<>();
        result.put("code","SUCCESS");
        result.put("msg","start demo1");
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/demo2")
    public Object demo2(HttpServletRequest request) throws Exception {
        runtimeService.startProcessInstanceByKey("demo2");
        Map<String,String> result = new HashMap<>();
        result.put("code","SUCCESS");
        result.put("msg","start demo2");
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/demo3")
    public Object demo3(HttpServletRequest request) throws Exception {
        runtimeService.startProcessInstanceByKey("demo3");
        Map<String,String> result = new HashMap<>();
        result.put("code","SUCCESS");
        result.put("msg","start demo3");
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/demo4")
    public Object demo4(HttpServletRequest request) throws Exception {
        runtimeService.startProcessInstanceByKey("demo4");
        Map<String,String> result = new HashMap<>();
        result.put("code","SUCCESS");
        result.put("msg","start demo4");
        return result;
    }
}
