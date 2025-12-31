package controller.commands;

import java.util.List;
import java.util.Optional;

import model.ICalendarManager;
import model.ReadOnlyEvent;
import utils.CSVWriter;
import utils.Writer;
import view.IView;

/**
 * Command for exporting a calendar to a CSV file.
 * Expected format: exportCalendar cal {@code <filename>}.
 */
public class ExportCalendarCommand implements ICommand {
  private String filename;

  /**
   * Creates a new ExportCalendarCommand.
   *
   * @param args the command arguments
   */
  public ExportCalendarCommand(String[] args) {
    parseExportCalendarCommand(args);
  }

  /**
   * Creates a new ExportCalendarCommand.
   *
   * @param filename the name of the file to exportCalendar to
   */
  public ExportCalendarCommand(String filename) {
    this.filename = filename;
  }

  /**
   * Executes the exportCalendar calendar command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if no events are found in the calendar
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    Optional<List<ReadOnlyEvent>> flattenedEvents = manager.exportCalendar(curCalendar);
    if (flattenedEvents.isEmpty()) {
      throw new IllegalArgumentException("Failed to exportCalendar calendar. No events found.");
    }
    Writer curWriter = new CSVWriter();
    curWriter.write(filename, flattenedEvents.get());
    String currentDir = System.getProperty("user.dir");
    view.exportCalendar(currentDir + "\\" + filename);
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
   * Parses a complete exportCalendar calendar command.
   *
   * @param parts the tokenized command string.
   */
  private void parseExportCalendarCommand(String[] parts) {
    if (parts.length != 3) {
      throw new IllegalArgumentException(
          "Invalid format for exporting calendar." + " Expected 3 parts, got " + parts.length);
    }
    filename = parts[2];
  }
}
