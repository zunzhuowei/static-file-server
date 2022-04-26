package com.hbs.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zun.wei on 2022/3/7.
 */
@Component
public class ThymeleafUtils {


    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;


    public SpringTemplateEngine mySpringTemplateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setCacheable(false);
        stringTemplateResolver.setTemplateMode(TemplateMode.HTML);
        springTemplateEngine.setTemplateResolver(stringTemplateResolver);
        return springTemplateEngine;
    }

    public String template2Html(String templateContent) {
        return template2Html(templateContent, new HashMap<>());
    }

    public String template2Html(String templateContent, Map<String, Object> data) {
        WebContext webContext = new WebContext(getRequest(), getResponse(),
                getRequest().getServletContext(), getRequest().getLocale(), data);
        webContext.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, null));
        return mySpringTemplateEngine().process(templateContent, webContext);
    }

    public String templateName2Html(String templateName, Map<String, Object> data) {
        WebContext webContext = new WebContext(getRequest(), getResponse(),
                getRequest().getServletContext(), getRequest().getLocale(), data);
        webContext.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, null));
        String process = thymeleafViewResolver.getTemplateEngine().process(templateName, webContext);
        return process;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

}
