package de.ts.stash.business;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.ts.stash.domain.Item;
import de.ts.stash.domain.Unit;
import de.ts.stash.persistence.ItemRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
	@Autowired
	ItemRepository itemRepo;

	@Override
	public void run(final String... args) throws Exception {
		log.debug("initializing item data...");

		final Item zucchini = Item.builder().amount(new BigDecimal(1)).name("Zucchini").unit(Unit.PIECE).build();
		final Item mehl = Item.builder().amount(new BigDecimal(1500)).name("Mehl").unit(Unit.GRAM).build();

		this.itemRepo.saveAll(Arrays.asList(zucchini, mehl));
		this.itemRepo.flush();

		log.debug("printing all item...");
		this.itemRepo.findAll().forEach(v -> log.debug(" Vehicle :" + v.toString()));
	}
}