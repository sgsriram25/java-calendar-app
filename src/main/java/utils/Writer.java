package utils;

import java.util.List;
import model.ReadOnlyEvent;

/**
 * Interface for writing data to a file. Different implementations of this interface can write data
 * to different file types.
 */
public interface Writer {
  /**
   * Writes the given data to a file at the specified path.
   *
   * @param filePath the path to the file.
   * @param events   the data to write.
   */
  void write(String filePath, List<ReadOnlyEvent> events);
}

