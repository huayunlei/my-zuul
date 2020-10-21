package com.ihomefnt.sky;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.ihomefnt.sky.filter.DidiFilterProcessor;
import com.ihomefnt.starter.semporna.EnableSemporna;
import com.netflix.zuul.FilterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
@EnableSemporna(redis = true)
@NacosPropertySource(dataId = "sky", autoRefreshed = true)
@NacosPropertySources({@NacosPropertySource(dataId = "sky", autoRefreshed = true),
        @NacosPropertySource(dataId = "app-monitor-config", groupId = "wcm", autoRefreshed = true)})
public class SkyApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkyApplication.class);

    /**
     * spring boot项目打包成war并在tomcat上运行的
     * 于web.xml的配置方式来启动spring上下文了，在Application类的同级添加一个SpringBootStartApplication类
     */
//	@Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(SkyApplication.class);
//    }
    public static void main(String[] args) {
        FilterProcessor.setProcessor(new DidiFilterProcessor());
        SpringApplication.run(SkyApplication.class, args);
        LOGGER.info("o2o gateway server start success , it will redirect the request uri to o2o server .");
    }
}
