package model;

import java.time.LocalDateTime;

/**
 * Public interface that provides read-only access to event information for clients outside the
 * model package. This interface is intended for views (e.g., GUI, console) to retrieve event
 * properties for display purposes. An internal interface InternalEvent within the model package
 * offers read-write access to event data.
 * This separation adheres to the Interface Segregation Principle by exposing only the necessary
 * functionality.
 */
public interface ReadOnlyEvent {

  /**
   * Returns the name of the event.
   *
   * @return the name of the event
   */
  String getName();

  /**
   * Returns the start time of the event.
   *
   * @return the start time of the event
   */
  LocalDateTime getStart();

  /**
   * Returns the end time of the event.
   *
   * @return the end time of the event
   */
  LocalDateTime getEnd();

  /**
   * returns if it is an all-day event.
   *
   * @return true if the event is all-day
   */
  boolean isAllDay();

  /**
   * Returns the description of the event.
   *
   * @return the description of the event
   */
  String getDescription();

  /**
   * Returns the location of the event.
   *
   * @return the location of the event
   */
  String getLocation();

  /**
   * Returns whether the event is public.
   *
   * @return whether the event is public
   */
  boolean isPublic();


}
