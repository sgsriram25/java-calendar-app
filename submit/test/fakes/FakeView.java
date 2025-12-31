package fakes;

import java.time.LocalDate;
import java.time.LocalDateTime;

import controller.IFeatures;
import java.util.Map;
import model.ReadOnlyEvent;
import view.IView;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FakeView implementation that records all messages and errors passed to it.
 */
public class FakeView implements IView {

  private final List<String> messages = new ArrayList<>();
  private final List<String> errors = new ArrayList<>();


  @Override
  public void addFeatures(IFeatures features) {
    // No implementation needed for this fake view.
  }

  @Override
  public void displayMessage(String message) {
    messages.add(message);
  }

  @Override
  public void displayError(String errorMessage) {
    errors.add(errorMessage);
  }

  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) {
    displayMessage(
        "Calendar '" + calendarName + "' created successfully." + " Timezone: " + timeZone.getId());
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    displayMessage(
        "Calendar '" + calendarName + "' updated successfully: " + property + " changed to "
        + newValue + ".");
  }

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

  @Override
  public void createRecurringOccurrences(String name, LocalDateTime start, LocalDateTime end,
                                         List<Character> daysOfWeek, int occurrences) {
    displayMessage(
        "Recurring event '" + name + "' created for " + occurrences + " times on " + daysOfWeek
        + ".");
  }

  @Override
  public void createRecurringUntil(String name, LocalDateTime start, LocalDateTime end,
                                   List<Character> daysOfWeek, LocalDateTime until) {
    displayMessage(
        "Recurring event '" + name + "' created until " + until.toLocalDate() + " on " + daysOfWeek
        + ".");
  }

  @Override
  public void editSingle(String name, LocalDateTime start, LocalDateTime end, String property,
                         String newValue) {
    displayMessage(
        "Event '" + name + "' updated successfully: " + property + " changed to " + newValue + ".");
  }

  @Override
  public void editMultiple(String name, LocalDateTime start, String property, String newValue) {
    displayMessage(
        "All events named '" + name + "' starting from " + start + " updated successfully: "
        + property + " changed to " + newValue + ".");
  }

  @Override
  public void editAll(String name, String property, String newValue) {
    displayMessage(
        "All events named '" + name + "' updated successfully: " + property + " changed to "
        + newValue + ".");
  }

  @Override
  public void printEventsOnDate(LocalDate date, Optional<List<ReadOnlyEvent>> events) {
    if (events.isEmpty()) {
      displayMessage("No events on " + date);
    } else {
      displayMessage("Events on " + date + ":");
      displayEvents(events.get());
    }
  }

  @Override
  public void printEventsInRange(LocalDateTime start, LocalDateTime end,
                                 Optional<List<ReadOnlyEvent>> events) {
    if (events.isEmpty()) {
      displayMessage("No events from " + start + " to " + end);
    } else {
      displayMessage("Events from " + start + " to " + end + ":");
      displayEvents(events.get());
    }
  }

  @Override
  public void exportCalendar(String filepath) {
    displayMessage("Calendar exported successfully to " + filepath);
  }

  @Override
  public void importCalendar(String filepath) {
    displayMessage("Calendar imported successfully from " + filepath);
  }

  @Override
  public void showStatus(LocalDateTime dateTime, boolean isBusy) {
    if (isBusy) {
      displayMessage("User is busy at " + dateTime);
    } else {
      displayMessage("User is available at " + dateTime);
    }
  }

  @Override
  public void useCalendar(String calendarName) {
    displayMessage("Switched to calendar '" + calendarName + "'.");
  }

  @Override
  public void copySingleEvent(String curCalendar, String eventName, LocalDateTime sourceDateTime,
                              String targetCalendarName, LocalDateTime targetDateTime) {
    displayMessage(
        "Event '" + eventName + "' copied from " + sourceDateTime + " in " + curCalendar + " to "
        + targetDateTime + " in " + targetCalendarName + ".");

  }

  @Override
  public void copyEventsOnDate(String curCalendar, LocalDate sourceDate, String targetCalendarName,
                               LocalDate targetDate) {
    displayMessage(
        "Events on " + sourceDate + " copied from " + curCalendar + " to " + targetDate + " in "
        + targetCalendarName + ".");

  }

  @Override
  public void displayMetrics(Map<String, Object> metrics) {
    // handle javadoc error.

  }

  @Override
  public void copyEventsBetweenDates(String curCalendar, LocalDate sourceStartDate,
                                     LocalDate sourceEndDate, String targetCalendarName,
                                     LocalDate targetStartDate) {
    displayMessage(
        "Events from " + sourceStartDate + " to " + sourceEndDate + " copied from " + curCalendar
        + " to " + targetStartDate + " in " + targetCalendarName + ".");

  }

  /**
   * Returns all messages that have been displayed.
   *
   * @return a list of messages.
   */
  public List<String> getMessages() {
    return messages;
  }

  /**
   * Returns all errors that have been displayed.
   *
   * @return a list of error messages.
   */
  public List<String> getErrors() {
    return errors;
  }

  /**
   * Adds a list of events to the messages list.
   *
   * @param events a list of events to display.
   */
  public void displayEvents(List<ReadOnlyEvent> events) {
    for (ReadOnlyEvent event : events) {
      displayMessage(event.toString());
    }
  }
}