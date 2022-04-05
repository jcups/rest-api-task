package ru.jcups.restapitask.controller;

import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.jcups.restapitask.exceptions.TokenNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerResolver {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException e){
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TokenNotFoundException.class)
	public ResponseEntity<?> handleTokenNotFoundException(TokenNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<?> handleObjectNotFoundException(ObjectNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
	}
}
