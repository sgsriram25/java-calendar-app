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
 * Command for creating a recurring event with a fixed end date.
 * Expected format:
 * create event {@code <eventName>} from {@code <startDateTime>} to {@code <endDateTime>}
 * repeats {@code <daysOfWeek>} until {@code <endDateString>}.
 * or
 * create event {@code <eventName>} on {@code <dateString>} repeats {@code <daysOfWeek>} until
 * {@code <endDateString>}.
 */
public class CreateRecurringEventUntilCommand implements ICommand {
  private String eventName;
  private LocalDateTime start;
  private LocalDateTime end;
  private List<Character> daysOfWeek;
  private LocalDateTime until;

  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments.
   */
  public CreateRecurringEventUntilCommand(String[] args) {
    try {
      parseCreateRecurringEventUntilCommand(args);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
          "Given command is too short and missing required parameters.");
    }
  }

  /**
   * Constructs the command with the specified parameters.
   */
  public CreateRecurringEventUntilCommand(String eventName,
                                           LocalDateTime start,
                                           LocalDateTime end,
                                           List<Character> daysOfWeek,
                                           LocalDateTime until) {
    this.eventName = eventName;
    this.start = start;
    this.end = end;
    this.daysOfWeek = daysOfWeek;
    this.until = until;
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

    // Delegate the creation to the model and then notify the view.
    manager.createRecurringEvent(curCalendar, eventName, start, end, daysOfWeek, until);
    view.createRecurringUntil(eventName, start, end, daysOfWeek, until);
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
  private void parseCreateRecurringEventUntilCommand(String[] parts) {
    int idx = 2;
    if (parts[idx].equalsIgnoreCase("--autoDecline")) {
      idx++;
    }
    eventName = parts[idx++];

    // Check for timed event ("from") versus all-day event ("on").
    if (parts[idx].equalsIgnoreCase("from")) {
      idx++;
      // Timed event: parse start and end datetime.
      start = safeParseDateTime(parts[idx++], "Error parsing start datetime");
      if (!parts[idx++].equalsIgnoreCase("to")) {
        throw new IllegalArgumentException("Expected 'to' keyword.");
      }
      end = safeParseDateTime(parts[idx++], "Error parsing end datetime");
    } else if (parts[idx].equalsIgnoreCase("on")) {
      idx++;
      // All-day event: parse date and set times accordingly.
      LocalDate date = safeParseDate(parts[idx++], "Error parsing date");
      start = date.atStartOfDay();
      end = date.atTime(23, 59, 59);
    } else {
      throw new IllegalArgumentException("Expected 'from' or 'on' keyword.");
    }

    if (!parts[idx++].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Expected 'repeats' keyword.");
    }
    daysOfWeek = parseWeekdays(parts[idx++]);
    if (!parts[idx++].equalsIgnoreCase("until")) {
      throw new IllegalArgumentException("Expected 'until' keyword.");
    }

    // For an all-day event, we expect a date; for a timed event, a datetime.
    if (start.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
        && end.toLocalTime().equals(java.time.LocalTime.of(23, 59, 59))) {
      // Likely an all-day event; parse the until date and set until to the end of that day.
      LocalDate untilDate = safeParseDate(parts[idx++], "Error parsing until date");
      until = untilDate.atTime(23, 59, 59);
    } else {
      // Timed event: parse until as a datetime.
      until = safeParseDateTime(parts[idx++], "Error parsing until datetime");
    }

    ensureNoExtraTokens(parts, idx);
  }

}
