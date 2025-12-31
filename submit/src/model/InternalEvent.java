package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import model.exceptions.InvalidEditException;

/**
 * Interface for internal events. Provides read-write access to event information. This interface is
 * internal to the model package, and is used by the model classes to manipulate event information.
 */
interface InternalEvent extends ReadOnlyEvent {

  /**
   * Sets the name of the event.
   *
   * @param name the name of the event
   */
  void setName(String name);

  /**
   * Sets the start time of the event.
   *
   * @param start the start time of the event
   * @throws InvalidEditException if the start time is invalid
   */
  void setStart(String start) throws InvalidEditException;

  /**
   * Sets the end time of the event.
   *
   * @param end the end time of the event
   * @throws InvalidEditException if the end time is invalid
   */
  void setEnd(String end) throws InvalidEditException;

  /**
   * Sets the description of the event.
   *
   * @param description the description of the event
   */
  void setDescription(String description);

  /**
   * Sets the location of the event.
   *
   * @param location the location of the event
   */
  void setLocation(String location);

  /**
   * Sets whether the event is public.
   *
   * @param isPublic whether the event is public
   * @throws InvalidEditException if the value is invalid
   */
  void setIsPublic(String isPublic) throws InvalidEditException;

  /**
   * Shifts the start time of the event.
   *
   * @param newStart the new start time of the event
   */
  void shiftStart(LocalDateTime newStart);

  /**
   * Check if the event overlaps with the given interval.
   */
  boolean intervalOverlap(LocalDateTime start, LocalDateTime end, LocalDateTime intervalStart,
                          LocalDateTime intervalEnd);

  /**
   * Copy the event. This is a factory method that creates another instance of the same
   * implementation class of the calling object.
   *
   * @return a copy of the event
   */
  InternalEvent copy();

  /**
   * Check if the event conflicts with another event.
   */
  boolean isConflictingWith(InternalEvent other);

  /**
   * Check if the event conflicts with a single event.
   */
  boolean isConflictingWithSingleEvent(SingleEvent other);

  /**
   * Check if the event conflicts with a recurring event.
   */
  boolean isConflictingWithRecurringEvent(RecurringEvent other);

  /**
   * Edit the property of the event.
   *
   * @param propertyName the name of the property
   * @param newValue     the new value of the property
   * @throws InvalidEditException if the edit is invalid
   */
  void editProperty(String propertyName, String newValue) throws InvalidEditException;

  /**
   * Creates new event(s) representing a modified occurrence without altering the original event.
   * <p>
   * For a single event, this method creates a copy, applies the specified property change, and
   * returns the updated copy.
   * For a recurring event, it splits the series by generating a single event for the modified
   * occurrence along with new recurring event series for the remaining occurrences, applying the
   * modification only to the new events.
   * In all cases, the original event remains completely unmodified. All modifications are applied
   * to the created copies.
   * </p>
   *
   * @param name         the name of the event to modify
   * @param start        the start time of the occurrence to be modified
   * @param end          the end time of the occurrence to be modified
   * @param propertyName the name of the property to change
   * @param newValue     the new value for the property
   * @return a list of event objects representing the modified occurrence(s)
   * @throws InvalidEditException if the modification parameters are invalid
   */
  List<InternalEvent> applyOccurrenceModification(String name, LocalDateTime start,
                                                  LocalDateTime end, String propertyName,
                                                  String newValue) throws InvalidEditException;


  /**
   * create new event(s) representing the modified series. For single event, it just goes ahead and
   * creates a copy and modifies the copy and return. For recurring event, it will split the series,
   * creating two new recurring events, apply the change to the second, and return the list of new
   * recurring events. In all cases, the original event remains completely unmodified.
   * All modifications are applied to the created copies.
   *
   * @param name the name of the event to modify
   * @param start the start time after which the modification should be applied
   * @param propertyName the name of the property to change
   * @param newValue the new value for the property
   * @return a list of event objects representing the modified series
   * @throws InvalidEditException if the modification parameters are invalid
   */
  List<InternalEvent> applySeriesModification(String name, LocalDateTime start, String propertyName,
                                              String newValue) throws InvalidEditException;

  /**
   * Check if the event contains an occurrence with the given name and start and end.
   * @param start the start time of the occurrence
   * @param end the end time of the occurrence
   * @return true if the event contains the occurrence, false otherwise
   */
  boolean containsOccurrence(LocalDateTime start, LocalDateTime end);

  /**
   * Check if the event contains an occurrence with the given name and start.
   * @param start the start time of the occurrence
   * @return true if the event contains the occurrence, false otherwise
   */
  boolean containsOccurrence(LocalDateTime start);

  /**
   * Check if the event contains an occurrence with the given name and start after the given time.
   * @param start the time threshold
   * @return true if the event contains the occurrence after the given time, false otherwise
   */
  boolean containsOccurrenceAfter(LocalDateTime start);

  /**
   * Get the event on the given date.
   * @param date the date to check
   * @return the event on the date, if it exists
   */
  Optional<List<InternalEvent>> getEventOnDate(LocalDate date);

  /**
   * Get the event in the given range.
   * @param start the start time of the range
   * @param end the end time of the range
   * @return the list of events in the range
   */
  Optional<List<InternalEvent>> getEventInRange(LocalDateTime start, LocalDateTime end);

  /**
   * Check if the event is busy at the given time.
   * @param time the time to check
   * @return true if the event is busy at the time, false otherwise
   */
  boolean isBusy(LocalDateTime time);

  /**
   * Flatten the event into a list of internal events.
   * @return the list of internal events
   */
  List<InternalEvent> flatten();
}
