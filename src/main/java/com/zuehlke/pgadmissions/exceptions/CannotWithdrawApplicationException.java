package com.zuehlke.pgadmissions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="Application cannot be withdrawn.")
	public class CannotWithdrawApplicationException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2390352248154970804L;


}
