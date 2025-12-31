package controller.commands;

import java.time.LocalDateTime;

import model.ICalendarManager;
import view.IView;

import static utils.CommandParser.safeParseDateTime;

/**
 * Command for showing the status of a specific date and time.
 * Expected format: show status on {@code <dateTimeString>}.
 */
public class ShowStatusCommand implements ICommand {
  private LocalDateTime dateTime;

  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments
   */
  public ShowStatusCommand(String[] args) {
    parseShowStatusCommand(args);
  }

  /**
   * Constructs the command with the specified date and time.
   */
  public ShowStatusCommand(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /**
   * Executes the show status command.
   *
   * @param manager     The calendar manager
   * @param view        The view
   * @param curCalendar The current active calendar
   * @return The name of the current calendar
   * @throws Exception If the command execution fails
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {
    boolean isBusy = manager.isBusy(curCalendar, dateTime);
    view.showStatus(dateTime, isBusy);
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
   * Parses a complete show status command.
   *
   * @param parts The tokenized command string
   */
  private void parseShowStatusCommand(String[] parts) {
    if (parts.length
        != 4) {
      throw new IllegalArgumentException("Invalid format for showing status."
                                         + " Expected "
                                         + "4 parts, got "
                                         + parts.length);
    }
    if (!parts[2].equalsIgnoreCase("on")) {
      throw new IllegalArgumentException("Expected 'on' keyword in show status command.");
    }
    dateTime = safeParseDateTime(parts[3], "Error parsing datetime in show status");
  }
}
