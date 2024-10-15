package com.kboticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class KboticketApplication {
	public static void main(String[] args) {

		SpringApplication.run(KboticketApplication.class, args);
	}
}
