package org.fichaje.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.schedule.ScheduledTasks;

@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	ScheduledTasks st;

//	@ApiIgnore
	@GetMapping()
	public ResponseEntity<?> test() {
		st.analizarFichajes();
		return ResponseEntity.status(HttpStatus.OK).body("Test finalizado");

	}

}
