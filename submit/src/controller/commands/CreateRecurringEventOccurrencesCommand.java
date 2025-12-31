package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.ICalendarManager;
import view.IView;

import static utils.CommandParser.ensureNoExtraTokens;
import static utils.CommandParser.parseWeekdays;
import static utils.CommandParser.safeParseDate;
import static utils.CommandParser.safeParseDateTime;

/**
 * Command for creating a recurring event with a fixed number of occurrences.
 * Expected format:
 * create event {@code <eventName>} from {@code <startDateTime>} to {@code <endDateTime>}
 * repeats {@code <daysOfWeek>} for {@code <occurrences>} times
 * or
 * create event {@code <eventName>} on {@code <dateString>} repeats {@code <daysOfWeek>} for
 * {@code <occurrences>} times.
 */
public class CreateRecurringEventOccurrencesCommand implements ICommand {
  private String eventName;
  private LocalDateTime start;
  private LocalDateTime end;
  private List<Character> daysOfWeek;
  private int occurrences;

  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments.
   */
  public CreateRecurringEventOccurrencesCommand(String[] args) {
    try {
      parseCreateRecurringEventOccurrencesCommand(args);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
          "Given command is too short and missing required parameters.");
    }
  }

  /**
   * Constructs the command with the specified parameters.
   */
  public CreateRecurringEventOccurrencesCommand(String eventName,
                                                LocalDateTime start,
                                                LocalDateTime end,
                                                List<Character> daysOfWeek,
                                                int occurrences) {
    this.eventName = eventName;
    this.start = start;
    this.end = end;
    this.daysOfWeek = daysOfWeek;
    this.occurrences = occurrences;
  }

  /**
   * Executes the create recurring event command.
   *
   * @param manager     The calendar manager.
   * @param view        The view.
   * @param curCalendar The current calendar.
   * @return The name of the current calendar.
   * @throws Exception If the command execution fails.
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    manager.createRecurringEvent(curCalendar, eventName, start, end, daysOfWeek, occurrences);
    view.createRecurringOccurrences(eventName, start, end, daysOfWeek, occurrences);
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
   * Parses the create recurring event command.
   *
   * @param parts The tokenized command arguments.
   */
  private void parseCreateRecurringEventOccurrencesCommand(String[] parts) {
    int idx = 2;
    // Handle optional flag.
    if (parts[idx].equalsIgnoreCase("--autoDecline")) {
      idx++;
    }
    eventName = parts[idx++];

    // Check if the next token indicates a timed event ("from") or an all-day event ("on").
    if (parts[idx].equalsIgnoreCase("from")) {
      idx++;
      start = safeParseDateTime(parts[idx++], "Error parsing start datetime");
      if (!parts[idx++].equalsIgnoreCase("to")) {
        throw new IllegalArgumentException("Expected 'to' keyword.");
      }
      end = safeParseDateTime(parts[idx++], "Error parsing end datetime");
    } else if (parts[idx].equalsIgnoreCase("on")) {
      idx++;
      // Parse the date (assuming safeParseDate returns a LocalDate).
      LocalDate date = safeParseDate(parts[idx++], "Error parsing date");
      start = date.atStartOfDay();
      // Set end time as end of day (23:59:59).
      end = date.atTime(23, 59, 59);
    } else {
      throw new IllegalArgumentException("Expected 'from' or 'on' keyword.");
    }

    if (!parts[idx++].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Expected 'repeats' keyword.");
    }
    daysOfWeek = parseWeekdays(parts[idx++]);
    if (!parts[idx++].equalsIgnoreCase("for")) {
      throw new IllegalArgumentException("Expected 'for' keyword.");
    }
    try {
      occurrences = Integer.parseInt(parts[idx++]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid occurrence number: expecting an integer.");
    }
    if (!parts[idx++].equalsIgnoreCase("times")) {
      throw new IllegalArgumentException("Expected 'times' keyword.");
    }
    ensureNoExtraTokens(parts, idx);
  }

}
