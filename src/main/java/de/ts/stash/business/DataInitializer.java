package de.ts.stash.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.ts.stash.domain.ApplicationUser;
import de.ts.stash.domain.Item;
import de.ts.stash.domain.ItemDefinition;
import de.ts.stash.domain.Role;
import de.ts.stash.domain.Unit;
import de.ts.stash.persistence.ItemDefinitionRepository;
import de.ts.stash.persistence.ItemRepository;
import de.ts.stash.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
	@Autowired
	ItemRepository itemRepo;
	
	@Autowired
	ItemDefinitionRepository itemDefintionRepo;

	@Autowired
	UserService userService;

	@Override
	public void run(final String... args) throws Exception {
		initItemDefinitions();
		initItems();
		initUsers();
	}

	private void initUsers() {
		log.debug("initializing user data...");
		final List<Role> authorities = Arrays.asList(Role.values());
		userService.save(new ApplicationUser("Timo", "password1", authorities));
		userService.save(new ApplicationUser("Barbara", "password2", authorities));
	}

	private void initItemDefinitions() {
		log.debug("initializing item definition data...");

		final ItemDefinition zucchiniDef = ItemDefinition.builder().name("Zucchini").build();
		final ItemDefinition mehlDef = ItemDefinition.builder().name("Mehl").build();

		this.itemDefintionRepo.saveAll(Arrays.asList(zucchiniDef, mehlDef));
		this.itemDefintionRepo.flush();
	}
	private void initItems() {
		log.debug("initializing item data...");
		
		final ItemDefinition zucchiniDef = this.itemDefintionRepo.findByName("Zucchini");
		final ItemDefinition mehlDef =  this.itemDefintionRepo.findByName("Mehl");
		
		final Item zucchini = Item.builder().amount(new BigDecimal(2)).unit(Unit.PIECE).definition(zucchiniDef).build();
		final Item mehl = Item.builder().amount(new BigDecimal(500)).unit(Unit.GRAM).definition(mehlDef).build();

		this.itemRepo.saveAll(Arrays.asList(zucchini, mehl));
		this.itemRepo.flush();
	}
}