package com.clientes.configuration;

import java.util.Date;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class Quartz {

	@Scheduled(cron = "0 35 15 * * *")
    public void work() {
		Date fecha = new Date();
        System.out.println("A pasado un segundo "+fecha);
    }
	
}
