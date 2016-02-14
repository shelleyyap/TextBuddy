import static org.junit.Assert.*;
import org.junit.Test;

public class TextBuddyTest {
	@Test
	public void TextBuddyTesting(){
		String[] fileNama = new String[1];
		fileNama[0] = "file.txt";
		assertTrue(TextBuddy.isTxtFile(fileNama));
	}
}
