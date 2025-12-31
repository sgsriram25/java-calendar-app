package controller.commands;

import model.ICalendarManager;
import view.IView;

/**
 * Command for editing a calendar.
 * Expected format: edit calendar {@code <calendarName>} --property {@code <propertyName>}
 * {@code <newValue>}.
 */
public class EditCalendarCommand implements ICommand {
  private String calendarName;
  private String propertyName;
  private String newValue;

  /**
   * Creates a new EditCalendarCommand.
   *
   * @param args the command arguments
   */
  public EditCalendarCommand(String[] args) {
    parseEditCalendarCommand(args);
  }

  /**
   * Creates a new EditCalendarCommand.
   */
  public EditCalendarCommand(String calendarName, String propertyName, String newValue) {
    this.calendarName = calendarName;
    this.propertyName = propertyName;
    this.newValue = newValue;
  }

  /**
   * Executes the edit calendar command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the calendar or property is not found
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    manager.editCalendar(calendarName, propertyName, newValue);
    view.editCalendar(calendarName, propertyName, newValue);
    if (calendarName.equals(curCalendar) && propertyName.equals("name")) {
      return newValue;
    }
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
   * Parses a complete edit calendar command. Expected format: "edit calendar {@code <calendarName>}
   * --property {@code <propertyName>} {@code <newValue>}".
   *
   * @param parts the tokenized command string.
   */
  private void parseEditCalendarCommand(String[] parts) {
    if (parts.length != 7) {
      throw new IllegalArgumentException(
          "Invalid format for editing a calendar." + " Expected 7 " + "parts, got " + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("edit")) {
      throw new IllegalArgumentException("Expected 'edit' keyword in edit calendar command.");
    }
    if (!parts[1].equalsIgnoreCase("calendar")) {
      throw new IllegalArgumentException("Expected 'calendar' keyword in edit calendar command.");
    }
    if (!parts[2].equalsIgnoreCase("--name")) {
      throw new IllegalArgumentException("Expected '--name' keyword in edit calendar command.");
    }
    calendarName = parts[3];
    if (!parts[4].equalsIgnoreCase("--property")) {
      throw new IllegalArgumentException("Expected '--property' keyword in edit calendar command.");
    }
    propertyName = parts[5];
    newValue = parts[6];
  }
}
