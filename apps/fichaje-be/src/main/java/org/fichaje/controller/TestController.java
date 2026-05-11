package org.fichaje.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.schedule.ScheduledTasks;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final ScheduledTasks st;

//	@ApiIgnore
	@GetMapping()
	public ResponseEntity<?> test() {
		st.analizarFichajes();
		return ResponseEntity.status(HttpStatus.OK).body("Test finalizado");

	}

}
