package controller;

import controller.commands.ICommand;

/**
 * Factory interface for storing Command maps mapping command pattern to command classes.
 */
public interface ICommandFactory {

  /**
   * Returns a Command based on the full command string and its tokenized arguments.
   *
   * @param fullCommand The complete command string as entered by the user.
   * @return A Command instance corresponding to the matched key.
   * @throws IllegalArgumentException if no matching command is found.
   */
  ICommand getCommand(String fullCommand);
}
