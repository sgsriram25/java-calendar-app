package controller.commands;

import java.time.LocalDate;

import utils.CommandParser;
import model.ICalendarManager;
import view.IView;

/**
 * Command for copying all events on a specific day.
 * Expected format:
 * copy events on {@code <dateString>} --target {@code <targetCalendarName>} to {@code <dateString>}
 */
public class CopyEventsOnDateCommand implements ICommand {
  private LocalDate sourceDate;
  private String targetCalendarName;
  private LocalDate targetDate;

  /**
   * Creates a new CopyEventsOnDateCommand.
   *
   * @param args the command arguments
   */
  public CopyEventsOnDateCommand(String[] args) {
    parseCommand(args);
  }

  /**
   * Creates a new CopyEventsOnDateCommand.
   *
   * @param sourceDate        the source date
   * @param targetCalendarName the target calendar name
   * @param targetDate        the target date
   */
  public CopyEventsOnDateCommand(LocalDate sourceDate,
                                 String targetCalendarName,
                                 LocalDate targetDate) {
    this.sourceDate = sourceDate;
    this.targetCalendarName = targetCalendarName;
    this.targetDate = targetDate;
  }

  /**
   * Executes the copy events on command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if no events are found on the specified date
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    manager.copyEventsOnDate(curCalendar, sourceDate, targetCalendarName, targetDate);
    view.copyEventsOnDate(curCalendar, sourceDate, targetCalendarName, targetDate);
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
   * Parses the command arguments.
   *
   * @param parts the command arguments
   */
  private void parseCommand(String[] parts) {
    if (parts.length
        != 8) {
      throw new IllegalArgumentException("Invalid format for copy events on command."
                                         + " Expected 8 parts, got "
                                         + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("copy")
        || !parts[1].equalsIgnoreCase("events")) {
      throw new IllegalArgumentException("Command must start with 'copy events'.");
    }
    if (!parts[2].equalsIgnoreCase("on")) {
      throw new IllegalArgumentException("Expected 'on' keyword.");
    }
    sourceDate = CommandParser.safeParseDate(parts[3], "Error parsing source date");

    if (!parts[4].equalsIgnoreCase("--target")) {
      throw new IllegalArgumentException("Expected '--target' keyword.");
    }
    targetCalendarName = parts[5];

    if (!parts[6].equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' keyword.");
    }
    targetDate = CommandParser.safeParseDate(parts[7], "Error parsing target date");
  }
}
