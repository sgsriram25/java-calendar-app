package controller.commands;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import model.ICalendarManager;
import model.ReadOnlyEvent;
import view.IView;

import static utils.CommandParser.safeParseDateTime;

/**
 * Command for printing events in a range.
 * Expected format: print events from {@code <startDateTime>} to {@code <endDateTime>}.
 */
public class PrintEventsInRangeCommand implements ICommand {
  private LocalDateTime start;
  private LocalDateTime end;


  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments.
   */
  public PrintEventsInRangeCommand(String[] args) {
    parsePrintEventsInRangeCommand(args);
  }

  /**
   * Constructs the command with the specified parameters.
   */
  public PrintEventsInRangeCommand(LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Executes the print events in range command.
   *
   * @param manager     The calendar manager.
   * @param view        The view.
   * @param curCalendar The current calendar.
   * @return The name of the current calendar.
   * @throws Exception If the command execution fails.
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    Optional<List<ReadOnlyEvent>> events = manager.getEventsInRange(curCalendar, start, end);
    view.printEventsInRange(start, end, events);
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
   * Parses a complete print events in range command.
   *
   * @param parts the tokenized command string.
   */
  private void parsePrintEventsInRangeCommand(String[] parts) {
    if (parts.length != 6) {
      throw new IllegalArgumentException(
          "Invalid format for printing events in a range." + " Expected " + "6 parts, got "
          + parts.length);
    }
    if (!parts[2].equalsIgnoreCase("from")) {
      throw new IllegalArgumentException(
          "Expected 'from' keyword in print events in range command.");
    }
    if (!parts[4].equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' keyword in print events in range command.");
    }
    start = safeParseDateTime(parts[3],
                              "Error parsing " + "start datetime " + "in print events in range");
    end = safeParseDateTime(parts[5], "Error parsing end datetime in print events in range");

  }
}
