package view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controller.IFeatures;
import model.ReadOnlyEvent;

/**
 * Interface for all view implementations (CLI, GUI, etc.)
 */
public interface IView {

  void addFeatures(IFeatures features);

  /**
   * Displays a message to the user.
   *
   * @param message The message to display.
   */
  void displayMessage(String message);

  /**
   * Displays an error message to the user.
   *
   * @param errorMessage The error message to display.
   */
  void displayError(String errorMessage);


  /**
   * Displays a message to the user indicating that a calendar was created successfully.
   *
   * @param calendarName The name of the calendar that was created.
   * @param timeZone     The timezone of the calendar.
   */
  void createCalendar(String calendarName, ZoneId timeZone);

  /**
   * Displays a message to the user indicating that a calendar was edited successfully.
   *
   * @param calendarName The name of the calendar that was edited.
   * @param property     The property that was edited.
   * @param newValue     The new value of the property.
   */
  void editCalendar(String calendarName, String property, String newValue);


  /**
   * Handles the display for successful creation of a single event.
   *
   * @param name        The name of the event.
   * @param start       The start time of the event.
   * @param end         The end time of the event.
   * @param autoDecline Whether the event is set to auto-decline conflicting events.
   */
  void createSingle(String name, LocalDateTime start, LocalDateTime end, boolean autoDecline);

  /**
   * Handles the display for successful creation of a recurring event with a set number of
   * occurrences.
   *
   * @param name        The name of the event.
   * @param start       The start time of the event.
   * @param end         The end time of the event.
   * @param daysOfWeek  The days of the week the event occurs.
   * @param occurrences The number of times the event repeats.
   */
  void createRecurringOccurrences(String name, LocalDateTime start, LocalDateTime end,
                                  List<Character> daysOfWeek, int occurrences);

  /**
   * Handles the display for successful creation of a recurring event with an end date.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param until      The end date of the event (inclusive).
   */
  void createRecurringUntil(String name, LocalDateTime start, LocalDateTime end,
                            List<Character> daysOfWeek, LocalDateTime until);

  /**
   * Handles the display for successful editing of a single event.
   *
   * @param name     The name of the event.
   * @param start    The start time of the event.
   * @param end      The end time of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  void editSingle(String name, LocalDateTime start, LocalDateTime end, String property,
                  String newValue);

  /**
   * Handles the display for successful editing of multiple events.
   *
   * @param name     The name of the event.
   * @param start    The start time of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  void editMultiple(String name, LocalDateTime start, String property, String newValue);

  /**
   * Handles the display for successful editing of all events with a given name.
   *
   * @param name     The name of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  void editAll(String name, String property, String newValue);

  /**
   * Handles the display for events on a given date.
   *
   * @param date   The date to display events for.
   * @param events The list of events on the given date.
   */
  void printEventsOnDate(LocalDate date, Optional<List<ReadOnlyEvent>> events);

  /**
   * Handles the display for events in a given range.
   *
   * @param start  The start of the range.
   * @param end    The end of the range.
   * @param events The list of events in the range.
   */
  void printEventsInRange(LocalDateTime start, LocalDateTime end,
                          Optional<List<ReadOnlyEvent>> events);

  /**
   * Handles the display for the successful exportCalendar of the calendar.
   *
   * @param filepath The file path to exportCalendar the events to.
   */
  void exportCalendar(String filepath);


  /**
   * Handles the display for the successful import of the calendar.
   *
   * @param filepath The file path to import the events from.
   */
  void importCalendar(String filepath);

  /**
   * Handles the display for the successful import of the calendar.
   *
   * @param dateTime The date and time to show the status for.
   * @param isBusy   Whether the user is busy at the given date and time.
   */
  void showStatus(LocalDateTime dateTime, boolean isBusy);


  /**
   * Handles the display for the successful using of a calendar.
   *
   * @param calendarName The name of the calendar that will be using.
   */
  void useCalendar(String calendarName);

  /**
   * Handles the display for the successful copying of a single event.
   *
   * @param curCalendar        The name of the calendar that the event is copied from.
   * @param eventName          The name of the event that is copied.
   * @param sourceDateTime     The date and time of the event that is copied.
   * @param targetCalendarName The name of the calendar that the event is copied to.
   * @param targetDateTime     The date and time of the event that is copied to.
   */
  void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
                       String targetCalendarName, LocalDateTime targetDateTime);

  /**
   * Handles the display for the successful copying of all events on a specified date.
   *
   * @param curCalendar        The name of the calendar that the events are copied from.
   * @param sourceDate         The date of the events that are copied.
   * @param targetCalendarName The name of the calendar that the events are copied to.
   * @param targetDate         The date of the events that are copied to.
   */
  void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
                        LocalDate targetDate);

  /**
   * Displays the calendar metrics in the view.
   *
   * @param metrics A map containing various calendar metrics (total events,
   *                events by weekdays, etc.)
   */
  void displayMetrics(Map<String, Object> metrics);

  /**
   * Handles the display for the successful copying of all events in a specified date interval.
   *
   * @param curCalendar        The name of the calendar that the events are copied from.
   * @param sourceStartDate    The start date of the interval.
   * @param sourceEndDate      The end date of the interval.
   * @param targetCalendarName The name of the calendar that the events are copied to.
   * @param targetStartDate    The start date of the interval in the target calendar.
   */
  void copyEventsBetweenDates(String curCalendar, LocalDate sourceStartDate,
                              LocalDate sourceEndDate, String targetCalendarName,
                              LocalDate targetStartDate);
}
