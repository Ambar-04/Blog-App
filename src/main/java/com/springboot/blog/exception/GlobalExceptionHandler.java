package com.springboot.blog.exception;

import com.springboot.blog.payload.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// In order to customize our validation response we need to extend ResponseEntityExceptionHandler,
// and need to override a method(handleMethodArgumentNotValid) from this class
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // handle specific exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handlerResourceNotFoundException(ResourceNotFoundException exception,
                                                                         WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BlogAPIException.class)
    public ResponseEntity<ErrorDetails> handlerBlogAPIException(BlogAPIException exception,
                                                                         WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // global exceptions
    // Apart from above 2 kind of exception, if we get any other error, this will handle it
    // ResourceNotFoundException extends RuntimeException extends Exception, "Exception" class is a base class
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handlerGlobalException(Exception exception,
                                                                         WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        // get all the validation errors from MethodArgumentNotValidException instance i.e. "ex"
        // getAllErrors() returns list of erros
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String filedName = ((FieldError)error).getField();
            String errorDefaultMessage = error.getDefaultMessage();
            errors.put(filedName,errorDefaultMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // always follow above method for validation exception ,but....
    // without extending "ResponseEntityExceptionHandler" also we can do in this way like we did for other exceptionhandler
    // no need to create new exception class of "MethodArgumentNotValidException" as it's default in JAVA
    // return type is ResponseEntity<Object>, NOT ResponseEntity<ErrorDetails>

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
//                                                               WebRequest webRequest){
//        Map<String, String> errors = new HashMap<>();
//        // get all the validation errors from MethodArgumentNotValidException instance i.e. "ex"
//        // getAllErrors() returns list of erros
//        exception.getBindingResult().getAllErrors().forEach((error)->{
//            String filedName = ((FieldError)error).getField();
//            String errorDefaultMessage = error.getDefaultMessage();
//            errors.put(filedName,errorDefaultMessage);
//        });
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
    // Another method where return type is actually ResponseEntity<ErrorDetails>
// @ExceptionHandler(MethodArgumentNotValidException.class)
// public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
//                                                                     WebRequest webRequest) {
//     Map<String, String> errors = new HashMap<>();
//     e.getBindingResult().getAllErrors().forEach(
//             (error) -> {
//                 String fieldName = ((FieldError) error).getField();
//                 String message = error.getDefaultMessage();
//                 errors.put(fieldName, message);
//             }
//     );

//     ErrorDetails errorDetails =
//             new ErrorDetails(new Date(), errors.toString(), webRequest.getDescription(false));

//     return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
// }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handlerAccessDeniedException(AccessDeniedException exception,
                                                                WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception,
                                                                     WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
