package nebusers.controller;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.rest.webmvc.support.ExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public ResponseEntity<?> handleAnyException(Exception e) {
        return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
            ConversionFailedException.class })
    @ResponseBody
    public ResponseEntity handleMiscFailures(Throwable t) {
        return errorResponse(t, HttpStatus.BAD_REQUEST);
    }
 

    @ExceptionHandler({ ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class,
            DataIntegrityViolationException.class })
    @ResponseBody
    public ResponseEntity handleConflict(Exception ex) {
        return errorResponse(ex, HttpStatus.CONFLICT);
    }
 
    protected ResponseEntity<ExceptionMessage> errorResponse(Throwable throwable, HttpStatus status) {
        if (null != throwable) {
        	LOGGER.error("error caught: " + throwable.getMessage(), throwable);
            return response(new ExceptionMessage(throwable), status);
        } else {
        	LOGGER.error("unknown error caught in RESTController, {}", status);
            return response(null, status);
        }
    }
 
    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
    	LOGGER.debug("Responding with a status of {}", status);
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }
}
