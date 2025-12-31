package controller;

import controller.commands.CreateCalendarCommand;
import controller.commands.UseCalendarCommand;
import model.ICalendarManager;
import view.IView;

/**
 * GUIController class that implements the IController interface. It handles the interaction
 * between the model and the view in a GUI context. It initializes the calendar manager and view,
 * and sets up the initial state of the application. It uses addFeatures to add the features to the
 * view, which are call-back methods that lets the view immediately delegates to the controller
 * when it gets user input.
 */
public class GUIController implements IController {
  final ICalendarManager manager;
  final IView view;
  String curCalendar;


  /**
   * Constructor for GUIController. Takes in the model and the view.
   *
   * @param manager the model
   * @param view    the view
   */
  public GUIController(ICalendarManager manager, IView view) {
    this.manager = manager;
    this.view = view;
  }

  @Override
  public void run() {
    view.addFeatures(new Features(this));
    try {
      
      CreateCalendarCommand createCalendarCommand =
          new CreateCalendarCommand("default", java.time.ZoneId.systemDefault());
      createCalendarCommand.execute(manager, view, curCalendar);
      curCalendar = "default";
      UseCalendarCommand useCalendarCommand = new UseCalendarCommand("default");
      useCalendarCommand.execute(manager, view, curCalendar);
    } catch (Exception e) {
      view.displayError(e.getMessage());
    }
  }
}
