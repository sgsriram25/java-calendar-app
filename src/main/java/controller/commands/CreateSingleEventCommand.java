package controller.commands;

import java.time.LocalDateTime;

import model.ICalendarManager;
import view.IView;

import static utils.CommandParser.ensureNoExtraTokens;
import static utils.CommandParser.safeParseDate;
import static utils.CommandParser.safeParseDateTime;

/**
 * Command for creating a single event.
 * Expected format: "create event [--autoDecline] {@code <eventName>} from {@code <startDateTime>}
 * to {@code <endDateTime>}" or
 * "create event [--autoDecline] {@code <eventName>} on {@code <dateString>}".
 */
public class CreateSingleEventCommand implements ICommand {
  private String eventName;
  private LocalDateTime start;
  private LocalDateTime end;


  /**
   * Constructs the command with the tokenized command arguments.
   *
   * @param args The tokenized command arguments.
   */
  public CreateSingleEventCommand(String[] args) {
    try {
      parseCreateSingleEventCommand(args);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
          "Given command is too short and missing required parameters.");
    }
  }

  /**
   * Constructs the command with the specified parameters.
   */
  public CreateSingleEventCommand(String eventName, LocalDateTime start, LocalDateTime end) {
    this.eventName = eventName;
    this.start = start;
    this.end = end;
  }

  /**
   * Executes the create single event command.
   *
   * @param manager     The calendar manager.
   * @param view        The view.
   * @param curCalendar The current calendar.
   * @return The name of the current calendar.
   * @throws Exception If the command execution fails.
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    manager.createSingleEvent(curCalendar, eventName, start, end);
    view.createSingle(eventName, start, end, true);
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
   * @param parts The tokenized command arguments.
   */
  private void parseCreateSingleEventCommand(String[] parts) {
    int idx = 2;
    if (parts[idx].equalsIgnoreCase("--autoDecline")) {
      idx++;
    }
    eventName = parts[idx++];
    String indicator = parts[idx++];
    if (indicator.equalsIgnoreCase("from")) {
      start = safeParseDateTime(parts[idx++], "Error parsing start datetime");
      if (!parts[idx++].equalsIgnoreCase("to")) {
        throw new IllegalArgumentException("Expected 'to' keyword.");
      }
      end = safeParseDateTime(parts[idx++], "Error parsing end datetime");
    } else if (indicator.equalsIgnoreCase("on")) {
      String dateToken = parts[idx++];
      if (dateToken.contains("T")) {
        start = safeParseDateTime(dateToken, "Error parsing datetime");
      } else {
        start = safeParseDate(dateToken, "Error parsing date").atStartOfDay();
      }
      end = null;
    } else {
      throw new IllegalArgumentException("Expected 'from' or 'on' after event name.");
    }
    ensureNoExtraTokens(parts, idx);
  }


}
