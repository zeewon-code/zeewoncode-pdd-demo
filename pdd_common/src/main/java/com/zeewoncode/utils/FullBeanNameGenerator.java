package com.zeewoncode.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
/**
 * 自定义 Spring Bean 命名生成器
 * 使用全限定类名（包名 + 类名）作为 Bean 名称，彻底防止多模块同名类冲突
 */
public class FullBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        // 1. 如果开发者手动指定了 @Service("xxx")，优先尊重开发者的手动命名
        if (definition instanceof AnnotatedBeanDefinition annotated) {
            String beanName = determineBeanNameFromAnnotation(annotated);
            if (beanName != null && !beanName.isEmpty()) {
                return beanName;
            }
        }

        // 2. 否则，使用全限定类名（如 com.xxx.a.controller.UserController）
        return definition.getBeanClassName();
    }
}
