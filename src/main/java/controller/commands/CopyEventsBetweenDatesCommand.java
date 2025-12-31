package controller.commands;

import java.time.LocalDate;

import utils.CommandParser;
import model.ICalendarManager;
import view.IView;

/**
 * Command for copying all events within a specified date interval.
 * Expected format:
 * copy events between {@code <startDateString>} and {@code <endDateString>} --target
 * {@code <targetCalendarName>} to {@code <targetStartDateString>}
 */
public class CopyEventsBetweenDatesCommand implements ICommand {
  private LocalDate sourceStartDate;
  private LocalDate sourceEndDate;
  private String targetCalendarName;
  private LocalDate targetStartDate;

  /**
   * Creates a new CopyEventsBetweenDatesCommand.
   */
  public CopyEventsBetweenDatesCommand(String[] args) {
    parseCommand(args);
  }

  /**
   * Creates a new CopyEventsBetweenDatesCommand.
   */
  public CopyEventsBetweenDatesCommand(LocalDate sourceStartDate,
                                       LocalDate sourceEndDate,
                                       String targetCalendarName,
                                       LocalDate targetStartDate) {
    this.sourceStartDate = sourceStartDate;
    this.sourceEndDate = sourceEndDate;
    this.targetCalendarName = targetCalendarName;
    this.targetStartDate = targetStartDate;
  }



  /**
   * Executes the copy events between command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if no events are found in the specified date interval
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    manager.copyEventsBetweenDates(curCalendar, sourceStartDate, sourceEndDate, targetCalendarName,
                                   targetStartDate);
    view.copyEventsBetweenDates(curCalendar, sourceStartDate, sourceEndDate, targetCalendarName,
                                targetStartDate);
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
   * Parses the copy events between command into args.
   *
   * @param parts the command arguments
   */
  private void parseCommand(String[] parts) {
    if (parts.length
        != 10) {
      throw new IllegalArgumentException("Invalid format for copy events between command: "
                                         + "Expected 10 parts, got "
                                         + parts.length);
    }
    if (!parts[0].equalsIgnoreCase("copy")
        || !parts[1].equalsIgnoreCase("events")) {
      throw new IllegalArgumentException("Command must start with 'copy events'.");
    }

    if (!parts[2].equalsIgnoreCase("between")) {
      throw new IllegalArgumentException("Expected 'between' keyword.");
    }
    sourceStartDate = CommandParser.safeParseDate(parts[3], "Error parsing source start"
                                                            + " date");
    if (!parts[4].equalsIgnoreCase("and")) {
      throw new IllegalArgumentException("Expected 'and' keyword.");
    }
    sourceEndDate = CommandParser.safeParseDate(parts[5], "Error parsing source end date");
    if (!parts[6].equalsIgnoreCase("--target")) {
      throw new IllegalArgumentException("Expected '--target' keyword.");
    }
    targetCalendarName = parts[7];
    if (!parts[8].equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' keyword.");
    }
    targetStartDate = CommandParser.safeParseDate(parts[9], "Error parsing target start"
                                                            + " date");
  }
}
