package controller.commands;

import java.time.ZoneId;

import model.ICalendarManager;
import view.IView;

/**
 * Command for creating a new calendar.
 * Expected format: create calendar {@code <calendarName>} --name {@code <calendarName>} --timezone
 * {@code <timezone>}
 */
public class CreateCalendarCommand implements ICommand {
  private String calendarName;
  private ZoneId timeZone;

  /**
   * Creates a new CreateCalendarCommand.
   *
   * @param args the command arguments
   */
  public CreateCalendarCommand(String[] args) {
    parseCommand(args);
  }

  /**
   * Creates a new CreateCalendarCommand.
   */
  public CreateCalendarCommand(String calendarName, ZoneId timeZone) {
    this.calendarName = calendarName;
    this.timeZone = timeZone;
  }

  /**
   * Executes the create calendar command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the calendar name is not unique or the timezone is invalid
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    manager.createCalendar(calendarName, timeZone);
    view.createCalendar(calendarName, timeZone);
    return curCalendar;
  }

  /**
   * Returns whether the command requires a valid current calendar.
   *
   * @return false
   */
  @Override
  public boolean requiresValidCurCalendar() {
    return false;
  }

  /**
   * Parses a complete create calendar command. Expected format: "create calendar {@code
   * <calendarName>}".
   *
   * @param parts the tokenized command string.
   */
  private void parseCommand(String[] parts) {
    if (parts.length != 6) {
      throw new IllegalArgumentException(
          "Invalid format for creating a calendar." + " Expected " + "6 parts, got "
          + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("create")) {
      throw new IllegalArgumentException("Expected 'create' keyword in create calendar command.");
    }
    if (!parts[1].equalsIgnoreCase("calendar")) {
      throw new IllegalArgumentException("Expected 'calendar' keyword in create calendar command.");
    }
    if (!parts[2].equalsIgnoreCase("--name")) {
      throw new IllegalArgumentException("Expected '--name' keyword in create calendar command.");
    }
    calendarName = parts[3];
    if (!parts[4].equalsIgnoreCase("--timezone")) {
      throw new IllegalArgumentException(
          "Expected '--timezone' keyword in create calendar " + "command.");
    }
    timeZone = ZoneId.of(parts[5]);
  }
}
