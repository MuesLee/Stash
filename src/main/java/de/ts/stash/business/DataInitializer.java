package de.ts.stash.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Item;
import de.ts.stash.domain.Role;
import de.ts.stash.domain.Unit;
import de.ts.stash.persistence.ItemRepository;
import de.ts.stash.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
	@Autowired
	ItemRepository itemRepo;

	@Autowired
	UserService userService;

	@Override
	public void run(final String... args) throws Exception {
		initItems();
		initUsers();
	}

	private void initUsers() {
		log.debug("initializing user data...");
		final List<Role> authorities = Arrays.asList(Role.values());
		userService.save(new ApplicationUser("Timo", "password1", authorities));
		userService.save(new ApplicationUser("Barbara", "password2", authorities));
	}

	private void initItems() {
		log.debug("initializing item data...");

		final Item zucchini = Item.builder().amount(new BigDecimal(1)).name("Zucchini").unit(Unit.PIECE).build();
		final Item mehl = Item.builder().amount(new BigDecimal(1500)).name("Mehl").unit(Unit.GRAM).build();

		this.itemRepo.saveAll(Arrays.asList(zucchini, mehl));
		this.itemRepo.flush();
	}
}