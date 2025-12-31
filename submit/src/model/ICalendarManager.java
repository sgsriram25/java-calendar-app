package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.exceptions.CalendarException;

/**
 * This interface represents the manager for the calendar application. It provides methods for
 * creating, editing of calendars, and delegates the event operations to each calendar.
 */
public interface ICalendarManager {

  /**
   * Creates a new calendar with a unique name and a valid IANA timezone.
   *
   * @param calendarName the unique name of the calendar
   * @param timezone     the IANA timezone string (e.g., "America/New_York")
   * @throws CalendarException if the name is not unique
   */
  void createCalendar(String calendarName, ZoneId timezone) throws CalendarException;

  /**
   * Edits a property of an existing calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit (e.g., "name" or "timezone")
   * @param newValue     the new value for the property
   * @throws CalendarException if the calendar does not exist, the property is invalid, or the
   *                           new value is not valid
   */
  void editCalendar(String calendarName, String property, String newValue) throws CalendarException;

  /**
   * Sets the active calendar context and returns the active calendar's timezone.
   *
   * @param calendarName the name of the calendar to set as active
   * @throws CalendarException if the calendar is not found
   */
  void detectCalendar(String calendarName) throws CalendarException;

  /**
   * Copies a single event from the current calendar to a target calendar.
   *
   * @param eventName          the name of the event to copy
   * @param sourceDateTime     the source event start date/time (with timezone info)
   * @param targetCalendarName the target calendar name
   * @param targetDateTime     the target start date/time for the event in the target calendar's
   *                           timezone
   * @throws CalendarException if the event or target calendar is not found, or if conversion fails
   */
  void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
                       String targetCalendarName, LocalDateTime targetDateTime)
      throws CalendarException;

  /**
   * Copies all events on a specified date from the current calendar to a target calendar.
   *
   * @param sourceDate         the date for which events should be copied (source calendar)
   * @param targetCalendarName the target calendar name
   * @param targetDate         the date in the target calendar where events will be copied to
   * @throws CalendarException if there is an issue with the source or target calendar or
   *                           conversion fails
   */
  void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
                        LocalDate targetDate) throws CalendarException;

  /**
   * Copies all events in the specified date interval from the current calendar to a target
   * calendar.
   *
   * @param startDate          the start date of the interval (inclusive)
   * @param endDate            the end date of the interval (inclusive)
   * @param targetCalendarName the target calendar name
   * @param targetStartDate    the start date for the interval in the target calendar
   * @throws CalendarException if there is an issue with the source or target calendar or
   *                           conversion fails
   */
  void copyEventsBetweenDates(String curCalendar, LocalDate startDate, LocalDate endDate,
                              String targetCalendarName, LocalDate targetStartDate)
      throws CalendarException;

  /**
   * Creates a new single event.
   *
   * @param name  The name of the event.
   * @param start The start time of the event.
   * @param end   The end time of the event.
   * @throws Exception if the event cannot be created.
   */
  void createSingleEvent(String calendarName, String name, LocalDateTime start, LocalDateTime end)
      throws Exception;

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
  void createRecurringEvent(String calendarName, String name, LocalDateTime start,
                            LocalDateTime end, List<Character> daysOfWeek, int numRepeats)
      throws Exception;

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
  void createRecurringEvent(String calendarName, String name, LocalDateTime start,
                            LocalDateTime end, List<Character> daysOfWeek, LocalDateTime until)
      throws Exception;

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
  void editSingleEvent(String calendarName, String eventName, LocalDateTime start,
                       LocalDateTime end, String propertyName, String newValue) throws Exception;

  /**
   * Edits all events that have the specified event name, whose start time is after a specific time.
   *
   * @param eventName    The name of the event to edit.
   * @param start        The start time of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception if the event cannot be edited.
   */
  void editMultipleEventsFrom(String calendarName, String eventName, LocalDateTime start,
                              String propertyName, String newValue) throws Exception;

  /**
   * Edits all events that have the specified event name.
   *
   * @param eventName    The name of the event to edit.
   * @param propertyName The name of the property to edit.
   * @param newValue     The new value to set.
   * @throws Exception if the event cannot be edited.
   */
  void editMultipleEvents(String calendarName, String eventName, String propertyName,
                          String newValue) throws Exception;

  /**
   * Gets all events on a specific date.
   *
   * @param date The date to get events for
   * @return A list of events
   */
  Optional<List<ReadOnlyEvent>> getEventsOnDate(String calendarName, LocalDate date);

  /**
   * Gets all events in a specific date range, events that are only partly within the range will
   * also be included.
   *
   * @param start The start of the date range
   * @param end   The end of the date range
   * @return A list of events
   */
  Optional<List<ReadOnlyEvent>> getEventsInRange(String calendarName, LocalDateTime start,
                                                 LocalDateTime end);

  /**
   * Returns all events in the corresponding calendar.
   *
   * @return a list of all events in the calendar
   */
  Optional<List<ReadOnlyEvent>> exportCalendar(String calendarName) throws Exception;

  /**
   * Checks if the calendar is already occupied by an event at a specific time.
   *
   * @param dateTime The time to check.
   * @return True if the calendar is occupied, otherwise false.
   */
  boolean isBusy(String calendarName, LocalDateTime dateTime);

  Map<String, Object> getCalendarMetrics(String calendarName, LocalDate startDate,
      LocalDate endDate);
}
