package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.exceptions.ConflictingEventException;
import model.exceptions.InvalidEventException;

/**
 * Interface for the calendar model, defining basic operations.
 */
interface ICalendar {

  /**
   * Gets the timezone of the calendar.
   */
  ZoneId getTimezone();


  Map<String, List<InternalEvent>> getEvents();

  /**
   * Sets the timezone of the calendar.
   *
   * @param timezone The timezone to set.
   */
  void setTimezone(ZoneId timezone);

  /**
   * Creates a new single event.
   *
   * @param name        The name of the event.
   * @param start       The start time of the event.
   * @param end         The end time of the event.
   * @throws Exception if the event cannot be created.
   */
  void createSingleEvent(String name, LocalDateTime start, LocalDateTime end) throws Exception;

  /**
   * Creates a single event that's a copy of an existing event but starts at newStart.
   * @param event the event to copy
   * @param newStart the new start time
   * @return the new event
   * @throws InvalidEventException if the event is invalid
   * @throws ConflictingEventException if the event conflicts with an existing event
   */
  InternalEvent createSingleEventCopy(InternalEvent event, LocalDateTime newStart) throws Exception;

  /**
   * Removes an event from the calendar.
   *
   * @param event The event to remove.
   */
  void removeEvent(InternalEvent event);


  /**
   * Creates a new recurring event, specifying the number of repetitions. Recurring events must
   * start and end within the same day.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param numRepeats The number of times the event repeats.
   * @throws Exception if the event cannot be created.
   */
  void createRecurringEvent(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, int numRepeats) throws Exception;

  /**
   * Creates a new recurring event,specifying the date on which the event stops repeating. Recurring
   * events must start and end within the same day. The until parameter is inclusive, and only
   * checks the date. If time is provided in the until parameter, it will be ignored.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param until      The end date of the event (inclusive).
   * @throws Exception if the event cannot be created.
   */
  void createRecurringEvent(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, LocalDateTime until) throws Exception;

  /**
   * Edits an existing event, changing the value of the specified property.
   *
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param end          The end time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception if the event cannot be edited.
   */
  void editSingleEvent(String eventName, LocalDateTime start, LocalDateTime end,
      String propertyName,
      String newValue)
      throws Exception;

  /**
   * Edits all events that have the specified event name, starting at a specific date (inclusive).
   * The event date time can be changed but the date must remain the same, otherwise an exception is
   * thrown.
   *
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception if the event cannot be edited.
   */
  void editMultipleEvents(String eventName, LocalDateTime start, String propertyName,
      String newValue)
      throws Exception;

  /**
   * Edits all events that have the specified event name.
   *
   * @param eventName    The name of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception if the event cannot be edited.
   */
  void editMultipleEvents(String eventName, String propertyName, String newValue) throws Exception;

  /**
   * Get all events on a given date.
   *
   * @param date The date of the event
   * @return A list of events on the given date
   */
  Optional<List<InternalEvent>> getEventsOnDate(LocalDate date);


  /**
   * Gets all events on a specific date. This is for copying event across calendar.
   *
   * @param name The name of the event
   * @param dateTime The date to check
   * @return The event on the specified date
   */
  Optional<InternalEvent> getEventOnTime(String name, LocalDateTime dateTime);

  /**
   * Gets all events in a specific date range, events that are only partly within the range will
   * also be included.
   *
   * @param start The start of the date range
   * @param end   The end of the date range
   * @return A list of event names
   */
  Optional<List<InternalEvent>> getEventsInRange(LocalDateTime start, LocalDateTime end);

  /**
   * Exports the calendar to a file in CSV format.
   *
   * @return The absolute path to the exported file
   * @throws Exception if the calendar cannot be exported
   */
  Optional<List<ReadOnlyEvent>> exportCalendar() throws Exception;

  /**
   * Checks if the calendar is already occupied by an event at a specific time.
   *
   * @param dateTime The time to check
   * @return True if the calendar is occupied, otherwise false
   */
  boolean isBusy(LocalDateTime dateTime);
}