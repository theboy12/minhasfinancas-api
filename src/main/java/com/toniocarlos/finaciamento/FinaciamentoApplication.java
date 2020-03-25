package com.toniocarlos.finaciamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinaciamentoApplication {

	public void testandoLiveReload() {
		System.out.println("Testando live reload");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(FinaciamentoApplication.class, args);
	}
	

}
