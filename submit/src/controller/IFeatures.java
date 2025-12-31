package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import model.ReadOnlyEvent;

/**
 * The Features interface. It contains call-back methods that will be called by the view when the
 * user inputs some information. The view immediately delegates to the controller when it gets user
 * input. So the controller is still the one that handles the user input and coordinates between
 * the model and the view.
 */
public interface IFeatures {
  /**
   * This method is called when the user clicks the create calendar button in the GUI.
   * It creates a new calendar with the given name and timezone.
   *
   * @param calendarName the name of the calendar to create
   * @param timeZone     the timezone of the calendar
   */
  void createCalendar(String calendarName, ZoneId timeZone);

  void showDashboard(LocalDate startDate, LocalDate endDate);

  /**
   * This method is called when the user clicks the edit calendar button in the GUI.
   * It edits the properties of the specified calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit
   * @param newValue     the new value for the property
   */
  void editCalendar(String calendarName, String property, String newValue);

  /**
   * This method is called when the view simply wants to check if a calendar exists.
   *
   * @param calendarName the name of the calendar to detect
   */
  boolean detectCalendar(String calendarName);

  /**
   * This method is called when the user clicks a calendar in the GUI.
   * It sets the current calendar to the specified calendar.
   *
   * @param calendarName the name of the calendar to use
   */
  void useCalendar(String calendarName);

  /**
   * This is to fetch the events on a specific date for a specific calendar. This is used by
   * GUI View when they are rendering the day Buttons.
   *
   * @param calendar the name of the calendar
   * @param date     the date to show events for
   */
  Optional<List<ReadOnlyEvent>> onlyGetEvents(String calendar, LocalDate date);

  /**
   * This is to fetch the events on a specific date for the current calendar. This is used by
   * GUI View when they are rendering the day Buttons.
   *
   * @param date the date to show events for
   * @return the list of events on the date
   */
  Optional<List<ReadOnlyEvent>> onlyGetEvents(LocalDate date);

  /**
   * This method is called when the user actually clicks a dayButton. It uses the
   * PrintEventsOnDateCommand and not only fetch events using model but also calls the
   * PrintEventsOnDate method on the GUI view inside the command class.
   *
   * @param date the date to show events for
   */
  void showEvents(LocalDate date);

  /**
   * This method is called when the user clicks the add single event button in the GUI.
   *
   * @param name      the name of the event
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   */
  void addSingleEvent(String name, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * This method is called when the user clicks the add recurring event button in the GUI.
   *
   * @param name        the name of the event
   * @param startTime   the start time of the event
   * @param endTime     the end time of the event
   * @param daysOfWeek  the days of the week the event occurs on
   * @param endDateTime the end date of the event
   */
  void addRecurringEventEndDate(String name, LocalDateTime startTime, LocalDateTime endTime,
                                List<Character> daysOfWeek, LocalDateTime endDateTime);

  /**
   * This method is called when the user clicks the add recurring event button in the GUI.
   *
   * @param name        the name of the event
   * @param startTime   the start time of the event
   * @param endTime     the end time of the event
   * @param daysOfWeek  the days of the week the event occurs on
   * @param occurrences the number of occurrences of the event
   */
  void addRecurringEventOccurrences(String name, LocalDateTime startTime, LocalDateTime endTime,
                                    List<Character> daysOfWeek, int occurrences);

  /**
   * This method is called when the user clicks the edit single event button in the GUI.
   * It edits a single event in the current calendar.
   *
   * @param eventName the name of the event to edit
   * @param start     the start time of the event
   * @param end       the end time of the event
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  void editSingleEvent(String eventName, LocalDateTime start, LocalDateTime end, String property,
                       String newValue);

  /**
   * This method is called when the user clicks the edit multiple events button in the GUI.
   * It edits multiple events in the current calendar after a specific time with the name.
   *
   * @param eventName the name of the event to edit
   * @param start     the start time of the event
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  void editMultipleEventsFrom(String eventName, LocalDateTime start, String property,
                              String newValue);

  /**
   * This method is called when the user clicks the edit all events button in the GUI.
   * It edits all events in the current calendar with the name.
   *
   * @param eventName the name of the event to edit
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  void editAllEvents(String eventName, String property, String newValue);

  /**
   * This method is called when the user clicks the export calendar button in the GUI. This
   * exports the current calendar to a filePath that ends with csv.
   *
   * @param filePath the file path to export the calendar to
   */
  void exportCalendar(String filePath);

  /**
   * This method is called when the user clicks the import calendar button in the GUI. This imports
   * a csv file to the current calendar.
   *
   * @param calendarFilePath the file path to import the calendar from
   */
  void importCalendar(String calendarFilePath);
}
