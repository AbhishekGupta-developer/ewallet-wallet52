package com.myorganisation.ewallet.wallet52;

import com.myorganisation.ewallet.wallet52.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Wallet52Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Wallet52Application.class, args);
	}

	@Autowired
	WalletService service;

	@Override
	public void run(String... args) throws Exception {

	}
}
