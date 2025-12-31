package controller.commands;

import java.util.List;

import model.ICalendarManager;
import utils.CSVReader;
import utils.EventCreationInfo;
import utils.Reader;

/**
 * Command for importing a calendar from a file.
 * Expected format: import {@code <calendarFilePath>}.
 */
public class ImportCalendarCommand implements ICommand {
  private final String calendarFilePath;

  public ImportCalendarCommand(String calendarFilePath) {
    this.calendarFilePath = calendarFilePath;
  }

  /**
   * Executes the import calendar command. It calls the CSVReader to read the calendar file
   * and get the events info from the file. Then it calls the model to create the events in the
   * calendar.
   * @param manager the model
   * @param view  the view
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the command can't be fulfilled
   */
  @Override
  public String execute(ICalendarManager manager, view.IView view, String curCalendar)
      throws Exception {
    if (calendarFilePath == null || calendarFilePath.isEmpty()) {
      throw new IllegalArgumentException("Calendar file path cannot be empty.");
    }
    Reader reader = new CSVReader();

    List<EventCreationInfo> importedEvents = reader.read(calendarFilePath);
    for (EventCreationInfo eventInfo : importedEvents) {
      manager.createSingleEvent(curCalendar, eventInfo.subject, eventInfo.startTime,
                                eventInfo.endTime);
      //Other infos could only be changed using the edit methods.
      manager.editSingleEvent(curCalendar, eventInfo.subject, eventInfo.startTime,
                              eventInfo.endTime, "description", eventInfo.description);
      manager.editSingleEvent(curCalendar, eventInfo.subject, eventInfo.startTime,
                              eventInfo.endTime, "location", eventInfo.location);
      manager.editSingleEvent(curCalendar, eventInfo.subject, eventInfo.startTime,
                              eventInfo.endTime, "public", eventInfo.isPrivate ? "false" : "true");
    }
    view.importCalendar(calendarFilePath);
    return curCalendar;
  }

  @Override
  public boolean requiresValidCurCalendar() {
    return true; // Assuming we need a valid current calendar to import
  }
}
