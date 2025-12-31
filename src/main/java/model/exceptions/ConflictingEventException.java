package model.exceptions;

/**
 * Exception thrown when an event conflicts with an existing event.
 */
public class ConflictingEventException extends Exception {

  public ConflictingEventException(String message) {
    super(message);
  }
}
