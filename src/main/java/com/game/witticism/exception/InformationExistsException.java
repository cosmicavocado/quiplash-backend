package com.game.witticism.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Conflict - Error 409
@ResponseStatus(HttpStatus.CONFLICT)
public class InformationExistsException extends RuntimeException{
    public InformationExistsException(String message){
        super(message);
    }
}