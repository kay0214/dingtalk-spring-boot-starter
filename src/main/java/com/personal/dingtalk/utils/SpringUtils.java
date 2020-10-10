package com.personal.dingtalk.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Map;

/**
 * @author sunpeikai
 * @version BaseOssUtil, v0.1 2020/9/18 11:23
 * @description spring工具类 方便在非spring管理环境中获取bean
 */
public final class SpringUtils implements BeanFactoryPostProcessor {
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    /**
     * 名称获取bean
     * @param name bean的名称
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 类获取bean
     * @param clz 类
     * @return Object 一个以所给类注册的bean的实例
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return (T) beanFactory.getBean(clz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义,则返回true
     * @param name bean的名称
     * @return boolean
     * @throws BeansException
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype 如果与给定名字相应的bean定义没有被找到,将会抛出一个异常（NoSuchBeanDefinitionException）
     * @param name bean的名称
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * 获取bean的class类型
     * @param name bean的名称
     * @return 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名,则返回这些别名
     * @param name bean的名称
     * @return 注册对象的名称
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 返回子类实例
     * @param clz bean的类型
     * @return 子类实例
     * @throws NoSuchBeanDefinitionException
     */
    public static <T> Map<String, T> getBeanOfType(Class<T> clz){
        return beanFactory.getBeansOfType(clz);
    }
}
