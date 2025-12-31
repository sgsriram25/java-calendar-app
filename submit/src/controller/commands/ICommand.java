package controller.commands;

import model.ICalendarManager;
import view.IView;

/**
 * Interface for all commands that can be issued by the user. Each command must implement the
 * execute method, which will be called by the controller, and coordinates between view and model
 * to fulfill the command.
 */
public interface ICommand {
  /**
   * Executes the command.
   * @param manager the model
   * @param view  the view
   * @param curCalendar the current active calendar
   * @return the name of the current active calendar
   * @throws Exception if the command can't be fulfilled
   */
  String execute(ICalendarManager manager, IView view, String curCalendar) throws Exception;

  /**
   * Returns whether the command requires a valid current calendar.
   * @return true if the command requires a valid current calendar, false otherwise
   */
  boolean requiresValidCurCalendar();
}