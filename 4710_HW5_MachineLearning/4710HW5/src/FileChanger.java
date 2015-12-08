import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FileChanger {

	public FileChanger() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
//		List<String> lines = Arrays.asList("The first line", "The second line");
		Path file = Paths.get("src/changed_census.train");
		Path file1 = Paths.get("src/changed_census.train.1000");
		Path file2 = Paths.get("src/changed_census.test");
//		Path file1 = Paths.get("src/changed_census.train.1000");
		try {
			List<String> information = Files.readAllLines(Paths.get("src/census.train"));
			List<String> updatedinfo = new ArrayList<String>();
			List<String> updatedinfo1 = new ArrayList<String>();
			List<String> updatedinfo2 = new ArrayList<String>();
			for (int i = 0; i < information.size(); i++) {
				String[] choices = information.get(i).split("\\s+");
				String newline = "";
				for(int j = 0; j < choices.length; j++) {
					if (j == 4 || j == 6 || j == 13) {
						newline += (choices[j] + " ");
					}
					else { }
				}
				newline = newline.trim();
				updatedinfo.add(newline);
				if (i < 1000) {
					updatedinfo1.add(newline);
				}
				else updatedinfo2.add(newline);
			}
			Files.write(file, updatedinfo, Charset.forName("UTF-8"));
			Files.write(file1, updatedinfo1, Charset.forName("UTF-8"));
			Files.write(file2, updatedinfo2, Charset.forName("UTF-8"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
