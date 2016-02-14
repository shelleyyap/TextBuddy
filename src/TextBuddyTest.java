import static org.junit.Assert.*;
import org.junit.Test;

public class TextBuddyTest {
	@Test
	public void TextBuddyTesting(){
		String[] fileNama = new String[1];
		fileNama[0] = "file.txt";
		assertTrue(TextBuddy.isTxtFile(fileNama));
		assertTrue(TextBuddy.isExitCommand("exit"));
		assertEquals("file.txt", TextBuddy.fileName(fileNama));
		
		String[] cmdLine = new String[2];
		cmdLine[0] = "search";
		cmdLine[1] = null;
		assertFalse(TextBuddy.isSearch(cmdLine));
		
		cmdLine[1] = "random string";
		assertTrue(TextBuddy.isSearch(cmdLine));
	}
}
