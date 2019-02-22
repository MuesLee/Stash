package de.ts.stash.business;

import static org.springframework.http.ResponseEntity.notFound;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import de.ts.stash.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler(value = {ItemNotFoundException.class})
    public ResponseEntity<Void> vehicleNotFound(ItemNotFoundException ex, WebRequest request) {
        log.debug("handling ItemNotFoundException...");
        return notFound().build();
    }
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<Void> usernameNotFound(UsernameNotFoundException ex, WebRequest request) {
    	log.debug("handling UsernameNotFoundException...");
    	return notFound().build();
    }
}