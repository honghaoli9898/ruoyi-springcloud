//package com.sdps.module.user.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import com.sdps.common.constant.SecurityConstants;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.*;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.ParameterType;
//import springfox.documentation.service.RequestParameter;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.Collections;
//import java.util.List;
//
//import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
//
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//public class SwaggerConfig {
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                // ① 用来创建该 API 的基本信息，展示在文档的页面中（自定义展示的信息）
//                .apiInfo(apiInfo())
//                // ② 设置扫描指定 package 包下的
//                .select()
//                .apis(basePackage("com.sdps"))
//                .paths(PathSelectors.any())
//                .build()
//                // ④ 全局参数（多租户 header）
//                .globalRequestParameters(globalRequestParameters());
//
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .description("Kinfe4j 集成测试文档")
//                .contact(new Contact("村雨遥", "https://cunyu.gitub.io/JavaPark", "747731461@qq.com"))
//                .version("v1.1.0")
//                .title("API测试文档")
//                .build();
//    }
//
//    private static List<RequestParameter> globalRequestParameters() {
//        RequestParameterBuilder tenantParameter = new RequestParameterBuilder()
//                .name(SecurityConstants.TENANT_HEADER).description("租户编号")
//                .in(ParameterType.HEADER).example(new ExampleBuilder().value(1L).build());
//        return Collections.singletonList(tenantParameter.build());
//    }
//
//}
