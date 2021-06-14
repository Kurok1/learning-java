package indi.kurok1.database.controller;

import indi.kurok1.database.domain.Item;
import indi.kurok1.database.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 货品访问
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.13
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable("id") Long id) {
        return this.itemRepository.getById(id);
    }

    @GetMapping("/")
    public List<Item> findAll() {
        return this.itemRepository.findAll();
    }

    @PostMapping("/save")
    public Item save(@RequestBody Item user) {
        return this.itemRepository.insert(user);
    }

}
