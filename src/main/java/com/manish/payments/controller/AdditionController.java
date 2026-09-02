package com.manish.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AdditionController {
	
	@PostMapping("/add")
	public int add(int a, int b) {
		log.info("Adding {} and {}", a, b);
		return a + b;
	}
}