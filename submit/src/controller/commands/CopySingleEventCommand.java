package controller.commands;

import java.time.LocalDateTime;

import utils.CommandParser;
import model.ICalendarManager;
import view.IView;

/**
 * Command for copying a single event from the current calendar to a target calendar.
 * Expected format:
 * "copy event {@code <eventName>} on {@code <sourceDateTime>} --target {@code
 * <targetCalendarName>} to {@code <targetDateTime>}"
 */
public class CopySingleEventCommand implements ICommand {
  private String eventName;
  private LocalDateTime sourceDateTime;
  private String targetCalendarName;
  private LocalDateTime targetDateTime;

  /**
   * Creates a new CopySingleEventCommand.
   *
   * @param args the command arguments
   */
  public CopySingleEventCommand(String[] args) {
    parseCommand(args);
  }


  /**
   * Creates a new CopySingleEventCommand.
   */
  public CopySingleEventCommand(String eventName,
                                 LocalDateTime sourceDateTime,
                                 String targetCalendarName,
                                 LocalDateTime targetDateTime) {
    this.eventName = eventName;
    this.sourceDateTime = sourceDateTime;
    this.targetCalendarName = targetCalendarName;
    this.targetDateTime = targetDateTime;
  }

  /**
   * Executes the copy single event command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the event or target calendar is not found, or if conversion fails due
   *                   to conflicts in new calendar
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    manager.copySingleEvent(curCalendar, eventName, sourceDateTime, targetCalendarName,
                            targetDateTime);
    view.copySingleEvent(curCalendar, eventName, sourceDateTime, targetCalendarName,
                         targetDateTime);
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
        != 9) {
      throw new IllegalArgumentException("Invalid format for copy event command."
                                         + " Expected 9 parts, got "
                                         + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("copy")
        || !parts[1].equalsIgnoreCase("event")) {
      throw new IllegalArgumentException("Command must start with 'copy event'.");
    }
    eventName = parts[2];

    if (!parts[3].equalsIgnoreCase("on")) {
      throw new IllegalArgumentException("Expected 'on' keyword.");
    }
    sourceDateTime = CommandParser.safeParseDateTime(parts[4], "Error parsing source datetime");

    if (!parts[5].equalsIgnoreCase("--target")) {
      throw new IllegalArgumentException("Expected '--target' keyword.");
    }
    targetCalendarName = parts[6];

    if (!parts[7].equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' keyword.");
    }
    targetDateTime = CommandParser.safeParseDateTime(parts[8], "Error parsing target datetime");
  }
}
