package com.sbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableScheduling
@EnableTransactionManagement
@EnableAutoConfiguration
@SpringBootConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({
		"com.sbapp.util",
		"com.sbapp.controller",
		"com.sbapp.logic",
		"com.sbapp.dao"
})

public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sbapp.controller"))
				.paths(PathSelectors.any())
				.build()
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Spring boot REST application ",
				"This is basic POC web application to show how some technologies work together.",
				"API TOS",
				"free to use, free to reproduce",
				new Contact("boris", "www.github.com", "boris.marn@gmail.com"),
				null,
				null);
	}

}
