package view;

import controller.IFeatures;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.ReadOnlyEvent;

/**
 * Concrete implementation of IView. This CLIView class displays messages and events in the console.
 * It is used by the Controller to display information to the user.
 */
public class CalendarCLIView implements IView {

  /**
   * Adds the features to the view.
   *
   * @param features The features to add.
   */
  @Override
  public void addFeatures(IFeatures features) {
    System.out.println("CalendarCLIView is ready to go!");
  }

  @Override
  public void displayMetrics(Map<String, Object> metrics) {

    displayMessage("Calendar Analytics Dashboard:");
    displayMessage("");

    int totalEvents = (int) metrics.get("totalEvents");
    if (totalEvents == 0) {
      displayMessage("No data to generate a dashboard");
      displayMessage("");
    }

    displayMessage("Total number of events: " + totalEvents);
    displayMessage("Events by weekdays: " + metrics.get("weekdayCount"));
    displayMessage("Events by name: " + metrics.get("eventNameCount"));
    displayMessage("Online events percentage: " + metrics.get("onlineEventsPercentage") + "%");

    Object busiestDay = metrics.get("busiestDay");
    String busiestDayDisplay = (busiestDay == null) ? "N/A" : busiestDay.toString();
    displayMessage("Busiest day(s): " + busiestDayDisplay);

    Object leastBusyDay = metrics.get("leastBusyDay");
    String leastBusyDayDisplay = (leastBusyDay == null) ? "N/A" : leastBusyDay.toString();
    displayMessage("Least busy day(s): " + leastBusyDayDisplay);

    displayMessage("Average events per day: " + metrics.get("averageEventsPerDay"));
  }

  /**
   * Displays a message to the user.
   *
   * @param message The message to display.
   */
  @Override
  public void displayMessage(String message) {
    System.out.println("\n" + message);
  }

  /**
   * Displays an error message to the user.
   *
   * @param errorMessage The error message to display.
   */
  @Override
  public void displayError(String errorMessage) {
    System.out.println("\nError: " + errorMessage);
  }

  /**
   * Displays a message to the user indicating that a calendar was created successfully.
   *
   * @param calendarName The name of the calendar that was created.
   * @param timeZone     The timezone of the calendar.
   */
  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) {
    displayMessage(
        "Calendar '" + calendarName + "' created successfully." + " Timezone: " + timeZone.getId());
  }

  /**
   * Displays a message to the user indicating that a calendar was edited successfully.
   *
   * @param calendarName The name of the calendar that was edited.
   * @param property     The property that was edited.
   * @param newValue     The new value of the property.
   */
  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    displayMessage(
        "Calendar '" + calendarName + "' updated successfully: " + property + " changed to "
            + newValue + ".");
  }

  /**
   * Displays a message to the user indicating that a calendar is being used.
   *
   * @param calendarName The name of the calendar that will be using.
   */
  @Override
  public void useCalendar(String calendarName) {
    displayMessage("Using calendar '" + calendarName + "'.");
  }

  /**
   * Displays a message to the user indicating that an event was copied successfully.
   *
   * @param curCalendar        The name of the calendar that the event is copied from.
   * @param eventName          The name of the event that is copied.
   * @param sourceDateTime     The date and time of the event that is copied.
   * @param targetCalendarName The name of the calendar that the event is copied to.
   * @param targetDateTime     The date and time of the event that is copied to.
   */
  @Override
  public void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
      String targetCalendarName, LocalDateTime targetDateTime) {
    displayMessage(
        "Event '" + eventName + "' copied from " + sourceDateTime + " in calendar '" + curCalendar
            + "' to " + targetDateTime + " in calendar '" + targetCalendarName + "'.");
  }

  /**
   * Displays a message to the user indicating that events were copied successfully.
   *
   * @param curCalendar        The name of the calendar that the events are copied from.
   * @param sourceDate         The date of the events that are copied.
   * @param targetCalendarName The name of the calendar that the events are copied to.
   * @param targetDate         The date of the events that are copied to.
   */
  @Override
  public void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
      LocalDate targetDate) {
    displayMessage(
        "Events on " + sourceDate + " copied from calendar '" + curCalendar + "' to " + targetDate
            + " in calendar '" + targetCalendarName + "'.");
  }

  /**
   * Displays a message to the user indicating that events were copied successfully.
   *
   * @param curCalendar        The name of the calendar that the events are copied from.
   * @param sourceStartDate    The start date of the interval.
   * @param sourceEndDate      The end date of the interval.
   * @param targetCalendarName The name of the calendar that the events are copied to.
   * @param targetStartDate    The start date of the interval in the target calendar.
   */
  @Override
  public void copyEventsBetweenDates(String curCalendar, LocalDate sourceStartDate,
      LocalDate sourceEndDate, String targetCalendarName,
      LocalDate targetStartDate) {
    displayMessage(
        "Events from " + sourceStartDate + " to " + sourceEndDate + " copied from calendar '"
            + curCalendar + "' to '" + targetCalendarName + "' starting on " + targetStartDate
            + ".");
  }

  /**
   * Displays a message to the user indicating that a single event was created successfully.
   *
   * @param name        The name of the event.
   * @param start       The start time of the event.
   * @param end         The end time of the event.
   * @param autoDecline Whether the event is set to auto-decline conflicting events.
   */
  @Override
  public void createSingle(String name, LocalDateTime start, LocalDateTime end,
      boolean autoDecline) {
    if (end != null) {
      displayMessage(
          "Single event '" + name + "' created successfully from " + start + " to " + end + ".");
    } else {
      displayMessage("Single all-day event '" + name + "' created successfully on " + start + ".");
    }
  }

  /**
   * Displays a message to the user indicating that a recurring event was created successfully.
   *
   * @param name        The name of the event.
   * @param start       The start time of the event.
   * @param end         The end time of the event.
   * @param daysOfWeek  The days of the week the event occurs.
   * @param occurrences The number of times the event repeats.
   */
  @Override
  public void createRecurringOccurrences(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, int occurrences) {
    displayMessage(
        "Recurring event '" + name + "' created for " + occurrences + " times on " + daysOfWeek
            + ".");
  }

  /**
   * Displays a message to the user indicating that a recurring event was created successfully.
   *
   * @param name       The name of the event.
   * @param start      The start time of the event.
   * @param end        The end time of the event.
   * @param daysOfWeek The days of the week the event occurs.
   * @param until      The end date of the event (inclusive).
   */
  @Override
  public void createRecurringUntil(String name, LocalDateTime start, LocalDateTime end,
      List<Character> daysOfWeek, LocalDateTime until) {
    displayMessage(
        "Recurring event '" + name + "' created until " + until.toLocalDate() + " on " + daysOfWeek
            + ".");
  }

  /**
   * Displays a message to the user indicating that a single event was edited successfully.
   *
   * @param name     The name of the event.
   * @param start    The start time of the event.
   * @param end      The end time of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  @Override
  public void editSingle(String name, LocalDateTime start, LocalDateTime end, String property,
      String newValue) {
    displayMessage(
        "Event '" + name + "' updated successfully: " + property + " changed to " + newValue + ".");
  }

  /**
   * Displays a message to the user indicating that multiple events were edited successfully.
   *
   * @param name     The name of the event.
   * @param start    The start time of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  @Override
  public void editMultiple(String name, LocalDateTime start, String property, String newValue) {
    displayMessage(
        "All events named '" + name + "' after " + start + " updated successfully: " + property
            + " changed to " + newValue + ".");
  }

  /**
   * Displays a message to the user indicating that all events with a given name were edited.
   *
   * @param name     The name of the event.
   * @param property The property that was edited.
   * @param newValue The new value of the property.
   */
  @Override
  public void editAll(String name, String property, String newValue) {
    displayMessage(
        "All events named '" + name + "' updated successfully: " + property + " changed to "
            + newValue + ".");
  }

  /**
   * Displays a message to the user indicating that an event was deleted successfully.
   *
   * @param date   The date to display events for.
   * @param events The list of events on the given date.
   */
  @Override
  public void printEventsOnDate(LocalDate date, Optional<List<ReadOnlyEvent>> events) {
    if (events.isEmpty() || events.get().isEmpty()) {
      displayMessage("No events on " + date);
    } else {
      displayMessage("Events on " + date + ":");
      displayEvents(events.get());
    }
  }

  /**
   * Displays a message to the user indicating that events were deleted successfully.
   *
   * @param start  The start of the range.
   * @param end    The end of the range.
   * @param events The list of events in the range.
   */
  @Override
  public void printEventsInRange(LocalDateTime start, LocalDateTime end,
      Optional<List<ReadOnlyEvent>> events) {
    if (events.isEmpty() || events.get().isEmpty()) {
      displayMessage("No events from " + start + " to " + end);
    } else {
      displayMessage("Events from " + start + " to " + end + ":");
      displayEvents(events.get());
    }
  }

  /**
   * Displays a message to the user indicating that the calendar was exported successfully.
   *
   * @param filepath The file path to exportCalendar the events to.
   */
  @Override
  public void exportCalendar(String filepath) {
    displayMessage("Calendar exported successfully to " + filepath);
  }

  @Override
  public void importCalendar(String filepath) {
    displayMessage("Calendar imported successfully from " + filepath);
  }

  /**
   * Displays a message to the user indicating that the calendar was imported successfully.
   *
   * @param dateTime The date and time to show the status for.
   * @param isBusy   Whether the user is busy at the given date and time.
   */
  @Override
  public void showStatus(LocalDateTime dateTime, boolean isBusy) {
    if (isBusy) {
      displayMessage("User is busy at " + dateTime);
    } else {
      displayMessage("User is available at " + dateTime);
    }
  }

  /**
   * Displays a list of events to the user.
   *
   * @param events The list of events to display.
   */
  private void displayEvents(List<ReadOnlyEvent> events) {
    for (ReadOnlyEvent e : events) {
      System.out.println(e);
    }
  }
}
