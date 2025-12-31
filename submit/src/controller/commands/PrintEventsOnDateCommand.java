package controller.commands;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import model.ICalendarManager;
import model.ReadOnlyEvent;
import view.IView;

import static utils.CommandParser.safeParseDate;

/**
 * Command for printing events on a specific date.
 * Expected format: print events on {@code <dateString>}.
 */
public class PrintEventsOnDateCommand implements ICommand {
  private LocalDate date;

  /**
   * Creates a new PrintEventsOnDateCommand.
   *
   * @param args the command arguments
   */
  public PrintEventsOnDateCommand(String[] args) {
    parsePrintEventsOnDateCommand(args);
  }

  /**
   * Creates a new PrintEventsOnDateCommand.
   *
   * @param date the date to print events for
   */
  public PrintEventsOnDateCommand(LocalDate date) {
    this.date = date;
  }

  /**
   * Executes the print events on date command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the date is not found
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    Optional<List<ReadOnlyEvent>> events = manager.getEventsOnDate(curCalendar, date);
    view.printEventsOnDate(date, events);
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
   * Parses a complete print events on date command.
   *
   * @param parts the tokenized command string.
   */
  private void parsePrintEventsOnDateCommand(String[] parts) {
    if (parts.length
        != 4) {
      throw new IllegalArgumentException("Invalid format for printing events on a date."
                                         + " Expected "
                                         + "4 parts, got "
                                         + parts.length);
    }
    if (!parts[2].equalsIgnoreCase("on")) {
      throw new IllegalArgumentException("Expected 'on' keyword in print events on date command.");
    }
    String dateToken = parts[3];
    if (dateToken.contains("T")) {
      throw new IllegalArgumentException("Expected date only, without time.");
    } else {
      date = safeParseDate(dateToken, "Error parsing date in print events on date");
    }
  }
}
