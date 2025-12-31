package utils;

import java.util.List;

/**
 * Interface for reading events from a file. Different implementations of this interface can read
 * data from different file types.
 */
public interface Reader {

  /**
   * Reads events from a file at the specified path.
   * @param filePath the path to the file.
   */
  List<EventCreationInfo> read(String filePath) throws Exception;
}
