package uk.co.alumeni.prism.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE)
public class PrismCannotApplyException extends RuntimeException {

    private static final long serialVersionUID = 6283126748436862418L;

}
