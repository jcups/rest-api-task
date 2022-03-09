package ru.jcups.restapitask.controller.rest.api;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jcups.restapitask.model.Item;
import ru.jcups.restapitask.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

	private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	private final ItemService itemService;
	private final HttpHeaders headers;

	// TODO: 01.03.2022 Добавить страницу добавления товара доступную Модератору, Админу и Создателю

	@GetMapping("")
	public ResponseEntity<Page<Item>> getItems(Pageable pageable) {
		logger.info("ItemController.getItems");
		try {
			return new ResponseEntity<>(itemService.findAllAtPage(pageable.getPageNumber(), pageable.getPageSize()),
					headers, HttpStatus.OK);
		} catch (ObjectNotFoundException e) {
			logger.error("ItemController.getItems: ", e);
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> findById(@PathVariable long id) {
		logger.info("ItemController.findById");
		logger.info("findById() called with: id = [" + id + "]");
		try {
			return new ResponseEntity<>(itemService.getById(id),
					headers, HttpStatus.OK);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
}
