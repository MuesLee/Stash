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
import de.ts.stash.persistence.ItemRepository;

@RestController
@RequestMapping("/v1/items")
public class ItemController {
    private ItemRepository items;
    public ItemController(ItemRepository Items) {
        this.items = Items;
    }

    @GetMapping("")
    public ResponseEntity<List<Item>> all() {
        return ok(this.items.findAll());
    }
    @PostMapping("")
    public ResponseEntity<Long> save(@RequestBody Item form, HttpServletRequest request) {
        Item saved = this.items.save(Item.builder().name(form.getName()).build());
        return created(
            ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/v1/items/{id}")
                .buildAndExpand(saved.getId())
                .toUri())
            .build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Item> get(@PathVariable("id") Long id) {
        return ok(this.items.findById(id).orElseThrow(() -> new ItemNotFoundException()));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public void update(@PathVariable("id") Long id, @RequestBody Item form) {
        Item existed = this.items.findById(id).orElseThrow(() -> new ItemNotFoundException());
        existed.setName(form.getName());
        this.items.save(existed);
    }
    @DeleteMapping("/{id}")
    @ResponseBody
    public void delete(@PathVariable("id") Long id) {
        Item existed = this.items.findById(id).orElseThrow(() -> new ItemNotFoundException());
        this.items.delete(existed);
    }
}