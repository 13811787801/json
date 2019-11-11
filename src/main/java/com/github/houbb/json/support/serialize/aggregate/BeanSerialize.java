package com.github.houbb.json.support.serialize.aggregate;

import com.github.houbb.heaven.annotation.ThreadSafe;
import com.github.houbb.heaven.constant.PunctuationConst;
import com.github.houbb.heaven.reflect.meta.field.IFieldMeta;
import com.github.houbb.heaven.reflect.meta.field.impl.FieldMetas;
import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.json.api.ISerialize;
import com.github.houbb.json.bs.JsonBs;
import com.github.houbb.json.constant.JsonBeanConst;
import com.github.houbb.json.constant.JsonConst;

import java.lang.reflect.Member;
import java.util.List;

/**
 * 针对 Bean 的序列化
 *
 * @author binbin.hou
 * @since 0.0.7
 */
@ThreadSafe
public class BeanSerialize implements ISerialize {

    @Override
    public String serialize(Object o) {
        List<IFieldMeta> fieldList = buildFieldList(o);
        if(CollectionUtil.isEmpty(fieldList)) {
            return JsonBeanConst.NULL;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JsonBeanConst.START);
        List<String> fieldStrings = Guavas.newArrayList();

        for(IFieldMeta field : fieldList) {
           final Object object = field.getValue();
           if(ObjectUtil.isNull(object)) {
               continue;
           }
           final String fieldName = field.getName();
           final String fieldJson = JsonBs.serialize(fieldName);
           final String valueJson = JsonBs.serialize(object);
           fieldStrings.add(fieldJson+PunctuationConst.COLON+valueJson);
        }

        if(CollectionUtil.isNotEmpty(fieldStrings)) {
            stringBuilder.append(CollectionUtil.join(fieldStrings, PunctuationConst.COMMA));
        }

        stringBuilder.append(JsonBeanConst.END);
        return stringBuilder.toString();
    }

    /**
     * 构建字段列表信息
     * @param instance 实例信息
     * @return 结果列表
     * @since 0.1.4
     */
    @SuppressWarnings("unchecked")
    private List<IFieldMeta> buildFieldList(final Object instance) {
        final Class tClass = instance.getClass();

        // 代理类
        if(tClass.getName().startsWith(JsonConst.PROXY_CLASS_NAME)) {
            return FieldMetas.buildMethodsMetaList(tClass, instance);
        }

        // 默认基于字段
        return FieldMetas.buildFieldsMetaList(tClass, instance);
    }

}
