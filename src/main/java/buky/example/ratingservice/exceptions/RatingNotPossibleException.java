package buky.example.ratingservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RatingNotPossibleException extends RuntimeException{

    public RatingNotPossibleException(String msg) {
        super(msg);
    }

}
