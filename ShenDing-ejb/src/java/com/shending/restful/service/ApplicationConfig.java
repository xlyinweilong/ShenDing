package com.shending.restful.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * 应用配置
 *
 * @author yin.weilong
 */
@javax.ws.rs.ApplicationPath("webservice")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.shending.restful.service.AdminREST.class);
        resources.add(com.shending.restful.service.ProductREST.class);
        resources.add(com.shending.restful.service.ConfigREST.class);
        resources.add(com.shending.restful.service.AladingWebREST.class);
    }

}
