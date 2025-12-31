package controller;

import controller.commands.ICommand;
import model.ICalendarManager;
import view.CalendarCLIView;
import view.CalendarGUIView;
import view.IView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Abstract controller providing shared functionality for all controllers. This class is mainly
 * responsible for the parsing of commands and delegating the execution of actions to the calendar.
 */
public abstract class AbstractTextController implements IController {

  protected final ICalendarManager manager;
  protected final IView view;
  protected final ICommandFactory factory;
  protected String curCalendar;

  /**
   * Constructs a new controller with the given calendar and view.
   *
   * @param manager The calendar model to use.
   * @param view     The view to use for displaying output.
   */
  public AbstractTextController(ICalendarManager manager, IView view) {
    this.manager = manager;
    this.view = view;
    this.curCalendar = null;
    this.factory = new CommandFactory();
  }

  /**
   * Processes the given command and executes the corresponding action. If the command is invalid,
   * an exception is thrown. In the body, the command is parsed and the appropriate action is taken,
   * such as creating an event, editing an event, or printing events. If the corresponding model
   * method throws an exception, we also throw an exception with the error message.
   *
   * @param command The command string entered by the user.
   */
  public void processCommand(String command) throws Exception {
    if (command == null || command.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty.");
    }

    command = command.trim();
    
    if (command.startsWith("show calendar dashboard from")) {
      handleCalendarDashboard(command);
    } else {
      
      ICommand cmd = factory.getCommand(command);

      
      if (cmd.requiresValidCurCalendar() && (curCalendar == null || curCalendar.isEmpty())) {
        throw new IllegalStateException("A valid calendar must be selected to execute "
            + "this command.");
      }

      
      curCalendar = cmd.execute(manager, view, curCalendar);
    }
  }

  /**
   * Handles the 'show calendar dashboard' command by parsing the date range and
   * fetching the metrics for the selected calendar.
   *
   * @param command The command string entered by the user.
   */
  private void handleCalendarDashboard(String command) {
    
    if (curCalendar == null) {
      view.displayError("No calendar selected. Please use a calendar first "
          + "(e.g., 'use calendar --name Work').");
      return;
    }

    
    String[] parts = command.split(" ");
    String startDateString = parts[4];
    String endDateString = parts[6];

    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate startDate = LocalDate.parse(startDateString, formatter);
    LocalDate endDate = LocalDate.parse(endDateString, formatter);

    
    if (startDate.isAfter(endDate)) {
      view.displayError("Invalid date range: start date cannot be after end date.");
      return;
    }

    try {
      
      Map<String, Object> metrics = manager.getCalendarMetrics(curCalendar, startDate, endDate);

      
      if (view instanceof CalendarCLIView) {
        CalendarCLIView cliView = (CalendarCLIView) view;
        cliView.displayMetrics(metrics);
      } else if (view instanceof CalendarGUIView) {
        CalendarGUIView guiView = (CalendarGUIView) view;
        guiView.displayMetrics(metrics);
      }
    } catch (Exception e) {
      view.displayError("Error fetching calendar dashboard: " + e.getMessage());
    }
  }
}
