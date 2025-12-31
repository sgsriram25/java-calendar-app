package controller;

/**
 * Interface for controllers handling user input and interaction.
 */
public interface IController {

  /**
   * Starts execution in the appropriate mode.
   * This method should be called after the controller is created.
   * The models and views will be called to perform the necessary operations.
   */
  public void run();
}
