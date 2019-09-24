/**
 * Copyright 2014-2017 www.hanxinbank.com All rights reserved.
 */
package com.zhouwei.core.common.mapper;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Consumer;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 *
 * @author Mark
 * @version 2013-11-15
 */
public class JsonMapper extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    private static JsonMapper mapper;

    public JsonMapper() {
        this(Include.NON_EMPTY);
    }

    public JsonMapper(Include include) {
        // 设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }
        // 允许单引号、允许不带引号的字段名称
        this.enableSimple();
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen,
                                  SerializerProvider provider) throws IOException,
                    JsonProcessingException {
                jgen.writeString("");
            }
        });
        // 进行HTML解码。
        this.registerModule(new SimpleModule().addSerializer(String.class, new JsonSerializer<String>() {
            @Override
            public void serialize(String value, JsonGenerator jgen,
                                  SerializerProvider provider) throws IOException,
                    JsonProcessingException {
                jgen.writeString(StringEscapeUtils.unescapeHtml4(value));
            }
        }));
        // 设置时区
        this.setTimeZone(TimeZone.getDefault());//getTimeZone("GMT+8:00")
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper getInstance() {
        if (mapper == null) {
            synchronized (JsonMapper.class) {
                if (null == mapper) {
                    mapper = new JsonMapper().enableSimple();
                }
            }
        }
        return mapper;
    }

    public static JsonMapper getInstance(Consumer<ObjectMapper> consumer) {
        JsonMapper mapper = new JsonMapper();
        if (Objects.nonNull(consumer)) {
            consumer.accept(mapper);
        }
        return mapper;
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper() {
        if (mapper == null) {
            synchronized (JsonMapper.class) {
                if (null == mapper) {
                    mapper = new JsonMapper(Include.NON_DEFAULT);
                }
            }
        }
        return mapper;
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object, Throwable... errors) {
        try {
            return this.writeValueAsString(object);
        } catch (Throwable e) {
            logger.error("write to json string error:" + object, e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     *
     * @see #fromJson(String, JavaType, Throwable...)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz, Throwable... errors) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return this.readValue(jsonString, clazz);
        } catch (Throwable e) {
            logger.error("parse json string error:" + jsonString, e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调用本函数.
     *
     * @see #createCollectionType(Class, Class...)
     */
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String jsonString, JavaType javaType, Throwable... errors) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return (T) this.readValue(jsonString, javaType);
        } catch (Throwable e) {
            logger.error("parse json string error:" + jsonString, e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * 構造泛型的Collection Type如:
     * ArrayList<MyBean>, 则调用constructCollectionType(ArrayList.class,MyBean.class)
     * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 當JSON裡只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
     */
    @SuppressWarnings("unchecked")
    public <T> T update(String jsonString, T object, Throwable... errors) {
        try {
            return (T) this.readerForUpdating(object).readValue(jsonString);
        } catch (Throwable e) {
            logger.error("update json string:" + jsonString + " to object:" + object + " error.", e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
        }
        return null;
    }

    /**
     * 輸出JSONP格式數據.
     */
    public String toJsonP(String functionName, Object object, Throwable... errors) {
        return toJson(new JSONPObject(functionName, object), errors);
    }

    /**
     * 設定是否使用Enum的toString函數來讀寫Enum,
     * 為False時時使用Enum的name()函數來讀寫Enum, 默認為False.
     * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
     */
    public JsonMapper enableEnumUseToString() {
        this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return this;
    }

    /**
     * 支持使用Jaxb的Annotation，使得POJO上的annotation不用与Jackson耦合。
     * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
     */
    public JsonMapper enableJaxbAnnotation() {
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        this.registerModule(module);
        return this;
    }

    /**
     * 允许单引号
     * 允许不带引号的字段名称
     */
    public JsonMapper enableSimple() {
        this.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        this.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return this;
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return this;
    }

    /**
     * 对象转换为JSON字符串
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            return null;
        } else {
            return JsonMapper.getInstance().toJson(object);
        }
    }

    /**
     * 对象转换为JSON字符串
     *
     * @param object
     * @param errors
     * @return
     */
    public static String toJsonString(Object object, Throwable... errors) {
        if (object == null) {
            return null;
        } else {
            return JsonMapper.getInstance().toJson(object, errors);
        }
    }

    /**
     * JSON字符串转换为对象
     *
     * @param jsonString
     * @param clazz
     * @param errors
     * @return
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz, Throwable... errors) {
        return JsonMapper.getInstance().fromJson(jsonString, clazz, errors);
    }

    /**
     * JSON字符串转换为对象
     *
     * @param jsonString
     * @param type
     * @param errors
     * @return
     */
    public static <T> T fromJsonString(String jsonString, TypeReference type, Throwable... errors) {
        try {
            return JsonMapper.getInstance().getMapper().readValue(jsonString, type);
        } catch (Throwable e) {
            logger.error("parse json string error:" + jsonString, e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * JSON字节流转换为对象
     *
     * @param input
     * @param clazz
     * @param errors
     * @param <T>
     * @return
     */
    public static <T> T fromInputStream(InputStream input, Class<T> clazz, Throwable... errors) {
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return JsonMapper.getInstance().getMapper().readValue(input, clazz);
        } catch (Throwable e) {
            logger.error("parse inputStream error:", e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * JSON字节流转换为对象
     *
     * @param input
     * @param type
     * @param errors
     * @param <T>
     * @return
     */
    public static <T> T fromInputStream(InputStream input, TypeReference type, Throwable... errors) {
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return JsonMapper.getInstance().getMapper().readValue(input, type);
        } catch (Throwable e) {
            logger.error("parse inputStream error:", e);
            if (null != errors && errors.length >= 1) {
                errors[0].addSuppressed(e);
                if (errors[0] instanceof RuntimeException) {
                    throw (RuntimeException) errors[0];
                } else {
                    throw new RuntimeException(errors[0]);
                }
            }
            return null;
        }
    }

    /**
     * 将json串解析为MAP(支持多层嵌套)
     *
     * @param msg
     * @return
     */
    public static Map<String, Object> parseJSON(String msg) {
        Map<String, Object> resultMap = Maps.newHashMap();
        return parseJSON2Map(msg, resultMap);
    }

    /**
     * 将json串解析为MAP(支持多层嵌套)
     *
     * @param msg
     * @param name
     * @return
     */
    public static Map<String, Object> parseJSONS(String msg, String name) {
        Map<String, Object> resultMap = Maps.newHashMap();
        List<Object> list = Lists.newArrayList();
        return parseJSON2Map(msg, resultMap, name, list);
    }

    /**
     * 将复杂json解析为扁平的map格式
     *
     * @param jsonStr json格式字符串
     * @param map     需要存入的map
     * @return 2018年4月19日
     * @author wangbin
     */
    private static Map<String, Object> parseJSON2Map(String jsonStr, Map<String, Object> map) {
        // 最外层解析
        JSONObject json = JSONObject.parseObject(jsonStr);
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是数组的话，继续解析
            if (v instanceof JSONObject) {
                JSONObject it = JSONObject.parseObject(String.valueOf(v));
                parseJSON2Map(String.valueOf(it), map);
            } else if (v instanceof List) {
                List<Object> list = (List<Object>) v;
                if (null != list && list.size() > 0) {
                    for (Object oj : list) {
                        parseJSON2Map(String.valueOf(oj), map);
                    }
                }

            } else {
                map.put(k.toString(), v);
            }
        }

        return map;
    }

    private static Map<String, Object> parseJSON2Map(String jsonStr, Map<String, Object> map, String name, List<Object> listResult) {
        // 最外层解析
        JSONObject json = JSONObject.parseObject(jsonStr);
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            if (!k.equals(name)) {
                // 如果内层还是数组的话，继续解析
                if (v instanceof JSONObject) {
                    JSONObject it = JSONObject.parseObject(String.valueOf(v));
                    parseJSON2Map(String.valueOf(it), map, name, listResult);
                } else if (v instanceof List) {
                    List<Object> list = (List<Object>) v;
                    if (null != list && list.size() > 0) {
                        for (Object oj : list) {
                            parseJSON2Map(String.valueOf(oj), map, name, listResult);
                        }
                    }

                } else {
                    map.put(k.toString(), v);
                }
            } else {
                listResult.add(v);
                continue;
            }
        }
        if (null != listResult && listResult.size() > 0) {
            map.put(name, listResult);
        } else {
            map.put(name, null);
        }

        return map;
    }
}
