package com.tf2tj.trade;

import com.tf2tj.trade.stem.Mediator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Tf2TjApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(Tf2TjApplication.class, args);

		Mediator mediator = applicationContext.getBean(Mediator.class);
		mediator.start();
	}

}
