package de.ts.stash.business.web;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.ts.stash.domain.Item;
import de.ts.stash.exception.ItemNotFoundException;
import de.ts.stash.exception.MismatchingIdException;
import de.ts.stash.persistence.ItemRepository;

@RestController
@RequestMapping("/v1/items")
public class ItemController {
	private final ItemRepository items;
	
	public ItemController(final ItemRepository Items) {
		this.items = Items;
	}

	@GetMapping("")
	public ResponseEntity<List<Item>> all() {
		return ok(this.items.findAll());
	}

	@DeleteMapping("/{id}")
	@ResponseBody
	public void delete(@PathVariable("id") final Long id) {
		final Item existed = this.items.findById(id).orElseThrow(() -> new ItemNotFoundException());
		this.items.delete(existed);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> get(@PathVariable("id") final Long id) {
		return ok(this.items.findById(id).orElseThrow(() -> new ItemNotFoundException()));
	}

	@PutMapping("")
	public ResponseEntity<Long> save(@RequestBody final Item form, final HttpServletRequest request) {
		final Item saved = this.items.save(form);
		return created(ServletUriComponentsBuilder.fromContextPath(request).path("/v1/items/{id}")
				.buildAndExpand(saved.getId()).toUri()).build();
	}

	@PostMapping("/{id}")
	@ResponseBody
	public void update(@PathVariable("id") final Long id, @RequestBody final Item form) {
		if (!form.isValid()) {
			throw new IllegalArgumentException("Invalid item");
		}
		final Item existed = this.items.findById(id).orElseThrow(() -> new ItemNotFoundException());
		if(!existed.getId().equals(id) || !id.equals(form.getId()))
		{
			throw new MismatchingIdException();
		}
		this.items.delete(existed);
		this.items.save(form);
		this.items.flush();
	}
}