package n.poulsen.campuspaths.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when there is an internal server error, which can be sent to the UI
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerSideException extends Exception {

    /**
     * Creates a new ServerSideException
     *
     * @param message the message attached to the exception
     * @spec.effects constructs a new ServerSideException
     */
    public ServerSideException(String message) {
        super(message);
    }

}