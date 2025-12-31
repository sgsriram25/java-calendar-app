package model.exceptions;

/**
 * Exception thrown when attempting to create an event that is invalid.
 */
public class InvalidEventException extends RuntimeException {

  public InvalidEventException(String message) {
    super(message);
  }

}
