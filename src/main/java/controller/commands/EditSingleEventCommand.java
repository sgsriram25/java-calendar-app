package controller.commands;

import java.time.LocalDateTime;

import static utils.CommandParser.safeParseDateTime;

import model.ICalendarManager;
import view.IView;

/**
 * Command for editing a single event.
 * Expected format: edit event {@code <property>} {@code <eventName>} from {@code <startDateTime>}
 * to {@code <endDateTime>} with {@code <newValue>}.
 */
public class EditSingleEventCommand implements ICommand {
  private String eventName;
  private LocalDateTime start;
  private LocalDateTime end;
  private String property;
  private String newValue;

  /**
   * Creates a new EditSingleEventCommand.
   *
   * @param args the command arguments
   */
  public EditSingleEventCommand(String[] args) {
    parseEditSingleEventCommand(args);
  }

  /**
   * Creates a new EditSingleEventCommand.
   *
   * @param eventName the name of the event
   * @param start     the start date and time
   * @param end       the end date and time
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  public EditSingleEventCommand(String eventName, LocalDateTime start, LocalDateTime end,
                                String property, String newValue) {
    this.eventName = eventName;
    this.start = start;
    this.end = end;
    this.property = property;
    this.newValue = newValue;
  }

  /**
   * Executes the edit single event command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the event or property is not found
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {


    manager.editSingleEvent(curCalendar, eventName, start, end, property, newValue);
    view.editSingle(eventName, start, end, property, newValue);
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
   * Parses a complete edit single event command.
   *
   * @param parts the tokenized command string.
   */
  private void parseEditSingleEventCommand(String[] parts) {
    if (parts.length != 10) {
      throw new IllegalArgumentException(
          "Invalid format for editing a single event." + " Expected " + "10 parts, got "
          + parts.length);
    }
    if (!parts[4].equalsIgnoreCase("from")) {
      throw new IllegalArgumentException("Expected 'from' keyword in edit single event command.");
    }
    if (!parts[6].equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' keyword in edit single event command.");
    }
    if (!parts[8].equalsIgnoreCase("with")) {
      throw new IllegalArgumentException("Expected 'with' keyword in edit single event command.");
    }
    property = parts[2];
    eventName = parts[3];
    start = safeParseDateTime(parts[5], "Error parsing start datetime in " + "edit event");
    end = safeParseDateTime(parts[7], "Error parsing end datetime in " + "edit event");
    newValue = parts[9];

  }
}
