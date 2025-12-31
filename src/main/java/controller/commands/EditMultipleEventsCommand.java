package controller.commands;

import model.ICalendarManager;
import view.IView;

/**
 * Command for editing multiple events.
 * Expected format: "edit events {@code <property>} {@code <eventName>} {@code <newValue>}"
 */
public class EditMultipleEventsCommand implements ICommand {
  private String eventName;
  private String property;
  private String newValue;

  /**
   * Creates a new EditMultipleEventsCommand.
   *
   * @param args the command arguments
   */
  public EditMultipleEventsCommand(String[] args) {
    parseEditMultipleEventsCommand(args);
  }

  /**
   * Creates a new EditMultipleEventsCommand.
   *
   * @param eventName the name of the event
   * @param property  the property to edit
   * @param newValue  the new value for the property
   */
  public EditMultipleEventsCommand(String eventName, String property, String newValue) {
    this.eventName = eventName;
    this.property = property;
    this.newValue = newValue;
  }

  /**
   * Executes the edit multiple events command.
   *
   * @param manager     the calendar manager class
   * @param view        the view class
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the event or property is not found
   */
  @Override
  public String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception {

    manager.editMultipleEvents(curCalendar, eventName, property, newValue);
    view.editAll(eventName, property, newValue);
    return curCalendar;
  }

  @Override
  public boolean requiresValidCurCalendar() {
    return true;
  }


  /**
   * Parses a complete edit multiple events command. Expected format: "edit events {@code
   * <property>}
   * {@code <eventName>} {@code <newValue>}".
   *
   * @param parts the tokenized command string.
   */
  private void parseEditMultipleEventsCommand(String[] parts) {
    if (parts.length
        != 5) {
      throw new IllegalArgumentException("Invalid format for editing multiple events."
                                         + " Expected 5 parts, got "
                                         + parts.length);
    }
    property = parts[2];
    eventName = parts[3];
    newValue = parts[4];
  }
}
