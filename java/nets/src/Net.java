import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Net {
	public abstract double getOutput();

	public static boolean validateAUGt(String fileName) {
		Charset charset = Charset.forName("US-ASCII");
		Path file = Paths.get(fileName);
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			int lineNumber = 1;
			String[] lineSplit;
			int n = 0;
			while ((line = reader.readLine()) != null) {
				try {
					lineSplit = line.split(" ");
					switch (lineNumber) {
					case 1:
						if (!lineSplit[0].equals("net"))
							throw new RuntimeException();
						String[] size = lineSplit[1].split(",");
						if (!(Integer.valueOf(size[0]) > 0))
							throw new RuntimeException();
						n = Integer.valueOf(size[0]);
						if (!(Integer.valueOf(size[1]) > 0))
							throw new RuntimeException();
						if (!(size.length == 2)) {
							throw new RuntimeException();
						}
						break;
					case 2:
						if (!lineSplit[0].equals("train"))
							throw new RuntimeException();
						size = lineSplit[1].split(",");
						if (!(Integer.valueOf(size[0]) > 0))
							throw new RuntimeException();
						if (!(Integer.valueOf(size[1]) > 0))
							throw new RuntimeException();
						if (!(Double.valueOf(size[2]) > 0))
							throw new RuntimeException();
						if (!(Integer.valueOf(size[3]) > 0))
							throw new RuntimeException();
						if (!(Double.valueOf(size[4]) > 0))
							throw new RuntimeException();
						if (!(size.length == 5)) {
							throw new RuntimeException();
						}
						break;
					case 3:
						if (!lineSplit[0].equals("TITLES"))
							throw new RuntimeException();
						size = lineSplit[1].split(",");
						if (!(size.length == n))
							throw new RuntimeException();
						break;
					default:
						if (!(Double.valueOf(lineSplit[0]) != null))
							throw new RuntimeException();
						size = lineSplit[1].split(",");
						if (!(size.length == n))
							throw new RuntimeException();
						break;
					}
					lineNumber++;
				} catch (Exception e) {
					System.err.println("Validation failed at line: "
							+ lineNumber);
					return false;
				}
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			return false;
		}
		return true;
	}
}
