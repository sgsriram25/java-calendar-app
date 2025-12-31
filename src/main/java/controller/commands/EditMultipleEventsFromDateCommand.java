package controller.commands;

import java.time.LocalDateTime;

import model.ICalendarManager;
import view.IView;

import static utils.CommandParser.safeParseDateTime;

/**
 * Command for editing multiple events from a specific date.
 * Expected format: "edit events {@code <property>} {@code <eventName>} from {@code
 * <startDateTime>} with {@code <newValue>}"
 */
public class EditMultipleEventsFromDateCommand implements ICommand {
  private String eventName;
  private LocalDateTime start;
  private String property;
  private String newValue;

  public EditMultipleEventsFromDateCommand(String[] args) {
    parseEditMultipleEventsFromDateCommand(args);
  }

  /**
   * Creates a new EditMultipleEventsFromDateCommand.
   *
   * @param eventName the name of the event
   * @param start     the start date and time
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  public EditMultipleEventsFromDateCommand(String eventName,
                                            LocalDateTime start,
                                            String property,
                                            String newValue) {
    this.eventName = eventName;
    this.start = start;
    this.property = property;
    this.newValue = newValue;
  }

  /**
   * Executes the edit multiple events from date command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the event or property is not found
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {


    manager.editMultipleEventsFrom(curCalendar, eventName, start, property, newValue);
    view.editMultiple(eventName, start, property, newValue);
    return curCalendar;
  }

  /**
   * Returns whether the command requires a valid current calendar.
   *
   * @return true
   */
  @Override
  public boolean requiresValidCurCalendar() {
    return true;
  }

  /**
   * Parses a complete edit multiple events from date command.
   *
   * @param parts the tokenized command string.
   */
  private void parseEditMultipleEventsFromDateCommand(String[] parts) {
    if (parts.length
        != 8) {
      throw new IllegalArgumentException("Invalid format for editing multiple events from a date"
                                         + "."
                                         + " Expected 8 parts, got "
                                         + parts.length);
    }
    if (!parts[4].equalsIgnoreCase("from")) {
      throw new IllegalArgumentException(
          "Expected 'from' keyword in edit multiple events from date command.");
    }
    if (!parts[6].equalsIgnoreCase("with")) {
      throw new IllegalArgumentException(
          "Expected 'with' keyword in edit multiple events from date command.");
    }
    property = parts[2];
    eventName = parts[3];
    start = safeParseDateTime(parts[5],
                              "Error parsing start datetime in edit multiple events from date");
    newValue = parts[7];
  }
}
