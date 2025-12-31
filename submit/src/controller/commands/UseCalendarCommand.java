package controller.commands;

import model.ICalendarManager;
import view.IView;

/**
 * Command for using a specific calendar.
 * Expected format: use calendar --name {@code <name-of-calendar>}.
 */
public class UseCalendarCommand implements ICommand {
  private String calendarName;

  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments
   */
  public UseCalendarCommand(String[] args) {
    parseUseCalendarCommand(args);
  }

  /**
   * Constructs the command with the specified calendar name.
   */
  public UseCalendarCommand(String calendarName) {
    this.calendarName = calendarName;
  }

  /**
   * Executes the use calendar command.
   *
   * @param manager     the model
   * @param view        the view
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if there is no calendar with the given name
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    manager.detectCalendar(calendarName);
    view.useCalendar(calendarName);
    return calendarName;
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
   * Parses the complete use calendar command.
   *
   * @param parts the tokenized command string.
   */
  private void parseUseCalendarCommand(String[] parts) {
    // We expect exactly 4 tokens.
    if (parts.length
        != 4) {
      throw new IllegalArgumentException("Invalid format for use calendar command. "
                                         + "Expected 4 parts, got "
                                         + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("use")) {
      throw new IllegalArgumentException("Expected 'use' keyword.");
    }
    if (!parts[1].equalsIgnoreCase("calendar")) {
      throw new IllegalArgumentException("Expected 'calendar' keyword.");
    }
    if (!parts[2].equalsIgnoreCase("--name")) {
      throw new IllegalArgumentException("Expected '--name' keyword.");
    }
    calendarName = parts[3];
  }
}
