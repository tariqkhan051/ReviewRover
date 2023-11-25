package com.tariqkhan051.reviewrover.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tariqkhan051.reviewrover.DbHandler;
import com.tariqkhan051.reviewrover.sql.SQLCache.Queries;

@RestController
@RequestMapping("/api/test")
public class HelloController {

	@GetMapping("/hello")
	public String index() {
		try {
			DbHandler dbHandler = new DbHandler();
			dbHandler.Create(new String[] {
					Queries.CREATE_TABLE_SENSOR,
					Queries.CREATE_TABLE_SENSOR_DATA,
					Queries.CREATE_HYPER_TABLE_SENSOR_DATA
			});
			dbHandler.Insert(true);
			dbHandler.Execute();
		} finally {
			System.out.println("Db connection completed.");
		}
		return "Greetings from Spring Boot!";
	}
}