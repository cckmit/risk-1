package com.zhouwei.ruleengine.dto;


import java.io.Serializable;

/**
 * 用户信息
 * @author zhouwei
 * @create 2019-09-20
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    String name;

    Integer age;

    public UserDTO() {
    }

    public UserDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
