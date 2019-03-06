package de.ts.stash.persistence;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.ts.stash.auth.user.RefreshToken;
import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Item;

@Configuration
@EnableJpaRepositories(basePackageClasses = { ItemRepository.class, UserRepository.class,
		RefreshTokenRepository.class })
@EntityScan(basePackageClasses = { ApplicationUser.class, Item.class, RefreshToken.class })
@PropertySource("classpath:/persistence.properties")
@EnableTransactionManagement
public class JpaConfig {

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(this.env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(this.env.getProperty("jdbc.url"));
		dataSource.setUsername(this.env.getProperty("jdbc.username"));
		dataSource.setPassword(this.env.getProperty("jdbc.password"));

		return dataSource;
	}

	// configure transactionManager

	// configure additional Hibernate Properties
}