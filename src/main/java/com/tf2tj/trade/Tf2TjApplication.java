package com.tf2tj.trade;

import com.tf2tj.trade.customs.Mediator;
import com.tf2tj.trade.stem.requests.LogInChecker;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Push
@Theme(variant = Lumo.DARK)
public class Tf2TjApplication implements AppShellConfigurator {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(Tf2TjApplication.class, args);

		LogInChecker checker = applicationContext.getBean(LogInChecker.class);
		checker.checkLogIns();

//		SteamProcessor steamProcessor = applicationContext.getBean(SteamProcessor.class);
//		steamProcessor.setUser();
//
//		Collection<Trade> trades = steamProcessor.getIncomingTrades(
//				new PriceScrap(50, 0, 0), new PriceScrap(50, 0, 0));
//		System.out.println();
//
//		ScrapTrade scr = applicationContext.getBean(ScrapTrade.class);
//		scr.testing();
//
//		System.out.println();

		Mediator mediator = applicationContext.getBean(Mediator.class);
		mediator.start();
	}

}
