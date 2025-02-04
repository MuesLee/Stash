package de.ts.stash.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.BAD_REQUEST, reason="Bad id")
public class MismatchingIdException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
