package model.exceptions;

/**
 * Exception thrown when attempting to make an invalid edit to an event.
 */
public class InvalidEditException extends Exception {

  public InvalidEditException(String message) {
    super(message);
  }

}
