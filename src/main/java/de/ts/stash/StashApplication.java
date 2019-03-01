package de.ts.stash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.ts.stash.util.TimeProvider;

@SpringBootApplication
public class StashApplication {

	public static void main(String[] args) {
		SpringApplication.run(StashApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public TimeProvider timeProvider()
	{
		return new TimeProvider();
	}

}