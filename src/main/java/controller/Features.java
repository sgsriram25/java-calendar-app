package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controller.commands.CreateCalendarCommand;
import controller.commands.CreateRecurringEventOccurrencesCommand;
import controller.commands.CreateRecurringEventUntilCommand;
import controller.commands.CreateSingleEventCommand;
import controller.commands.EditCalendarCommand;
import controller.commands.EditMultipleEventsCommand;
import controller.commands.EditMultipleEventsFromDateCommand;
import controller.commands.EditSingleEventCommand;
import controller.commands.ExportCalendarCommand;
import controller.commands.ImportCalendarCommand;
import controller.commands.PrintEventsOnDateCommand;
import controller.commands.UseCalendarCommand;
import model.ReadOnlyEvent;

/**
 * This is to provide a set of callback methods to the GUI view class, so that as soon as the GUI
 * gets user input, it delegates the input to the call back methods provided by the controller.
 * This is a good practise to separate the GUI and the controller, so that the GUI can be only
 * responsible for displaying and getting user input, and the controller is responsible for
 * coordinating between view and model.
 */
public class Features implements IFeatures {

  private final GUIController controller;

  public Features(GUIController controller) {
    this.controller = controller;
  }

  /**
   * This method is called when the user clicks the create calendar button in the GUI.
   * It creates a new calendar with the given name and timezone.
   *
   * @param calendarName the name of the calendar to create
   * @param timeZone     the timezone of the calendar
   */
  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) {
    try {
      CreateCalendarCommand createCalendarCommand =
          new CreateCalendarCommand(calendarName, timeZone);
      createCalendarCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error creating calendar: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the edit calendar button in the GUI.
   * It edits the properties of the specified calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit
   * @param newValue     the new value for the property
   */
  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    try {
      EditCalendarCommand editCalendarCommand =
          new EditCalendarCommand(calendarName, property, newValue);
      controller.curCalendar =
          editCalendarCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error editing calendar: " + e.getMessage());
    }
  }

  /**
   * This method is called when the view simply wants to check if a calendar exists.
   *
   * @param calendarName the name of the calendar to detect
   */
  @Override
  public boolean detectCalendar(String calendarName) {
    try {
      controller.manager.detectCalendar(calendarName);
      return true;
    } catch (Exception e) {
      controller.view.displayError("Error finding calendar: " + e.getMessage());
      return false;
    }
  }

  /**
   * This method is called when the user clicks a calendar in the GUI.
   * It sets the current calendar to the specified calendar.
   *
   * @param calendarName the name of the calendar to use
   */
  @Override
  public void useCalendar(String calendarName) {
    try {
      UseCalendarCommand useCalendarCommand = new UseCalendarCommand(calendarName);
      controller.curCalendar =
          useCalendarCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error using calendar: " + e.getMessage());
    }
  }

  /**
   * This is to fetch the events on a specific date for a specific calendar. This is used by
   * GUI View when they are rendering the day Buttons.
   *
   * @param calendar the name of the calendar
   * @param date     the date to show events for
   */
  @Override
  public Optional<List<ReadOnlyEvent>> onlyGetEvents(String calendar, LocalDate date) {
    Optional<List<ReadOnlyEvent>> events = Optional.empty();
    try {
      events = controller.manager.getEventsOnDate(calendar, date);
    } catch (Exception e) {
      controller.view.displayError("Error getting events: " + e.getMessage());
    }
    return events;
  }

  /**
   * This is to fetch the events on a specific date for the current calendar. This is used by
   * GUI View when they are rendering the day Buttons.
   *
   * @param date the date to show events for
   * @return the list of events on the date
   */
  @Override
  public Optional<List<ReadOnlyEvent>> onlyGetEvents(LocalDate date) {
    Optional<List<ReadOnlyEvent>> events = Optional.empty();
    try {
      events = controller.manager.getEventsOnDate(controller.curCalendar, date);
    } catch (Exception e) {
      controller.view.displayError("Error getting events: " + e.getMessage());
    }
    return events;
  }

  /**
   * This method is called when the user actually clicks a dayButton. It uses the
   * PrintEventsOnDateCommand and not only fetch events using model but also calls the
   * PrintEventsOnDate method on the GUI view inside the command class.
   *
   * @param date the date to show events for
   */
  @Override
  public void showEvents(LocalDate date) {
    try {
      PrintEventsOnDateCommand printEventsOnDateCommand = new PrintEventsOnDateCommand(date);
      printEventsOnDateCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error showing events: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the add single event button in the GUI.
   * @param name the name of the event
   * @param startTime the start time of the event
   * @param endTime the end time of the event
   */
  @Override
  public void addSingleEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    try {
      CreateSingleEventCommand createSingleEventCommand =
          new CreateSingleEventCommand(name, startTime, endTime);
      createSingleEventCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error adding single event: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the add recurring event button in the GUI.
   * @param name the name of the event
   * @param startTime the start time of the event
   * @param endTime the end time of the event
   * @param daysOfWeek the days of the week the event occurs on
   * @param endDateTime the end date of the event
   */
  @Override
  public void addRecurringEventEndDate(String name, LocalDateTime startTime, LocalDateTime endTime,
      List<Character> daysOfWeek, LocalDateTime endDateTime) {
    try {
      CreateRecurringEventUntilCommand createRecurringEventCommand =
          new CreateRecurringEventUntilCommand(name, startTime, endTime, daysOfWeek, endDateTime);
      createRecurringEventCommand.execute(controller.manager, controller.view,
          controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error adding recurring event" + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the add recurring event button in the GUI.
   *
   * @param name the name of the event
   * @param startTime the start time of the event
   * @param endTime the end time of the event
   * @param daysOfWeek the days of the week the event occurs on
   * @param occurrences the number of occurrences of the event
   */
  @Override
  public void addRecurringEventOccurrences(String name, LocalDateTime startTime,
      LocalDateTime endTime, List<Character> daysOfWeek,
      int occurrences) {
    try {
      CreateRecurringEventOccurrencesCommand createRecurringEventCommand =
          new CreateRecurringEventOccurrencesCommand(name, startTime, endTime, daysOfWeek,
              occurrences);
      createRecurringEventCommand.execute(controller.manager, controller.view,
          controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error adding recurring event: " + e.getMessage());
    }
  }

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
  @Override
  public void editSingleEvent(String eventName, LocalDateTime start, LocalDateTime end,
      String property, String newValue) {
    try {
      EditSingleEventCommand editSingleEventCommand =
          new EditSingleEventCommand(eventName, start, end, property, newValue);
      editSingleEventCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error editing single event: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the edit multiple events button in the GUI.
   * It edits multiple events in the current calendar after a specific time with the name.
   *
   * @param eventName the name of the event to edit
   * @param start     the start time of the event
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  @Override
  public void editMultipleEventsFrom(String eventName, LocalDateTime start, String property,
      String newValue) {
    try {
      EditMultipleEventsFromDateCommand editMultipleEventsFromDateCommand =
          new EditMultipleEventsFromDateCommand(eventName, start, property, newValue);
      editMultipleEventsFromDateCommand.execute(controller.manager, controller.view,
          controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error editing multiple events: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the edit all events button in the GUI.
   * It edits all events in the current calendar with the name.
   *
   * @param eventName the name of the event to edit
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  @Override
  public void editAllEvents(String eventName, String property, String newValue) {
    try {
      EditMultipleEventsCommand editMultipleEventsCommand =
          new EditMultipleEventsCommand(eventName, property, newValue);
      editMultipleEventsCommand.execute(controller.manager, controller.view,
          controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error editing all events: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the export calendar button in the GUI. This
   * exports the current calendar to a filePath that ends with csv.
   *
   * @param filePath the file path to export the calendar to
   */
  @Override
  public void exportCalendar(String filePath) {
    try {
      ExportCalendarCommand exportCalendarCommand = new ExportCalendarCommand(filePath);
      exportCalendarCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error exporting calendar: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the show dashboard button in the GUI.
   * It displays the calendar metrics for the specified date range.
   *
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   */
  @Override
  public void showDashboard(LocalDate startDate, LocalDate endDate) {
    try {
      
      Map<String, Object> metrics = controller.manager.getCalendarMetrics(
          controller.curCalendar, startDate, endDate);
      
      controller.view.displayMetrics(metrics);
    } catch (Exception e) {
      controller.view.displayError("Error showing dashboard: " + e.getMessage());
    }
  }

  /**
   * This method is called when the user clicks the import calendar button in the GUI. This imports
   * a csv file to the current calendar.
   *
   * @param calendarFilePath the file path to import the calendar from
   */
  @Override
  public void importCalendar(String calendarFilePath) {
    try {
      ImportCalendarCommand importCalendarCommand = new ImportCalendarCommand(calendarFilePath);
      importCalendarCommand.execute(controller.manager, controller.view, controller.curCalendar);
    } catch (Exception e) {
      controller.view.displayError("Error importing calendar: " + e.getMessage());
    }
  }
}