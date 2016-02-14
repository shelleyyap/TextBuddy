
/**
 * This class is used to manipulate text in a file. Only add, delete, display
 * and clear commands are available in this version. The output will be of the
 * form of numerical pointers. 
 * 
 * Assumptions: 
 * 1. Adding the same content twice is currently allowed. 
 * 2. The file parameter given when running the program is strictly of the format 
 *    <file name>.txt. Other formats breaks the program.
 * 3. For the command display, if the file does not exist and the command is called,
 *    a new text file with the file name passed in will be created. Similarly for 
 *    the command clear and delete.
 */
import java.util.Scanner;
import java.io.*;

public class TextBuddy {

	private static final String MESSAGE_CREATE_EMPTY_FILE = "New empty file %s is created.";
	private static final String MESSAGE_WRONG_FILE_TYPE = "Wrong file type, please input text files.";
	private static final String MESSAGE_INPUT_TEXT_FILE = "Please input text file.";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use \n";
	private static final String MESSAGE_COMMAND = "command: ";
	private static final String MESSAGE_WRONG_COMMAND = "Wrong command. Please re-enter command";
	private static final String MESSAGE_ADD = "added to %s: \"%s\"";
	private static final String MESSAGE_DELETED = "deleted from %s: %s";
	private static final String MESSAGE_DELETED_DOES_NOT_EXIST = "Point to be deleted does not exist.";
	private static final String MESSAGE_SORTED = "File content is sorted!";
	private static final String MESSAGE_DISPLAY_EMPTY = "%s is empty";
	private static final String MESSAGE_CLEARED = "all content deleted from %s";
	
	public static void main(String[] args) throws IOException {
		exitIfIncorrectArguments(args);
		printWelcomeMessage(args);
		executeCommandsUntilExitCommand(args);

	}

	public static void exitIfIncorrectArguments(String[] args) {
		exitIfNoArguments(args);
		exitIfUnacceptableArguments(args);
	}

	public static void exitIfNoArguments(String[] args) {
		if (args.length == 0 && args != null) {
			stopWithErrorMessage(MESSAGE_INPUT_TEXT_FILE);
		}
	}

	public static void exitIfUnacceptableArguments(String[] args) {
		if (!isTxtFile(args)) {
			stopWithErrorMessage(MESSAGE_WRONG_FILE_TYPE);
			System.exit(0);
		}
	}

	public static void stopWithErrorMessage(String content) {
		System.out.println(content);
		System.exit(0);
	}

	public static void printWelcomeMessage(String[] args) {
		System.out.printf(MESSAGE_WELCOME,
				fileName(args));
	}

	public static void executeCommandsUntilExitCommand(String[] args)
			throws IOException {
		Scanner sc = new Scanner(System.in);
		printCommand();
		String cmdLine = sc.nextLine();
		while (!isExitCommand(cmdLine)) {
			int indexOfSpace = cmdLine.indexOf(" ");
			if (isCommandWithoutContent(indexOfSpace)) {
				if (isDisplay(cmdLine.toLowerCase())) {
					display(args);
				} else if (isClear(cmdLine.toLowerCase())) {
					clear(args);
				} else {
					printErrorMessageWithoutStopping();
				}
			} else {
				String cmd = cmdLine.substring(0, indexOfSpace).toLowerCase();
				String content = cmdLine.substring(indexOfSpace + 1);
				if (isAdd(cmd)) {
					add(content, args);
				} else if (isDelete(cmd)) {
					delete(Integer.valueOf(content), args);
				} else {
					printErrorMessageWithoutStopping();
				}
			}
			printCommand();
			cmdLine = sc.nextLine();
		}
		sc.close();
	}

	/** These are the set of boolean functions. */
	public static boolean isTxtFile(String[] args) {
		CharSequence txt = ".txt";
		return fileName(args).contains(txt);
	}

	public static boolean isExitCommand(String cmdLine) {
		return cmdLine.equals("exit");
	}

	/** This checks if the command given is delete or clear.
	 * 
	 * @param indexOfSpace This parameter checks whether there is a space
	 * 					   after the command (delete or clear) is given.
	 */ 
	public static boolean isCommandWithoutContent(int indexOfSpace) {
		return indexOfSpace == -1;
	}

	public static boolean isDisplay(String cmdLine) {
		return cmdLine.equals("display");
	}

	public static boolean isAdd(String cmdLine) {
		return cmdLine.equals("add");
	}

	public static boolean isDelete(String cmdLine) {
		return cmdLine.equals("delete");
	}

	public static boolean isClear(String cmdLine) {
		return cmdLine.equals("clear");
	}

	/** These are the set of print functions */
	public static void printCommand() {
		System.out.print(MESSAGE_COMMAND);
	}

	public static void printOutput(String content, Object... variables){
		System.out.printf(content + "\n", variables);
	}

	public static void printErrorMessageWithoutStopping() {
		System.out.println(MESSAGE_WRONG_COMMAND);
	}

	/**
	 * This retrieves the name of the input text file as a string from the array
	 */
	public static String fileName(String[] args) {
		return args[0];
	}

	/**
	 * These are the command functions which will edit the text file based on
	 * the command and content passed in.
	 */
	public static void display(String[] args) throws IOException {
		File file = new File(fileName(args));
		createEmptyFile(file, args);

		Scanner sc = new Scanner(file);
		if (file.length() == 0) {
			printOutput(MESSAGE_DISPLAY_EMPTY, fileName(args));
		} else {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				System.out.println(line);
			}
		}
		sc.close();
	}

	public static void clear(String[] args) throws IOException {
		File file = new File(fileName(args));
		createEmptyFile(file, args);

		PrintWriter inFile = new PrintWriter(fileName(args));
		inFile.close();
		printOutput(MESSAGE_CLEARED, fileName(args));
	}

	public static void add(String content, String[] args) throws IOException {
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new FileWriter(fileName(args), true)));
		BufferedReader reader = new BufferedReader(
				new FileReader(fileName(args)));
		String currentLine;
		int pointer = 1;
		while ((currentLine = reader.readLine()) != null) {
			pointer++;
		}
		String pointerStr = ((String.valueOf(pointer)).concat(". ")).concat(content);
		writer.println(pointerStr); // for add
		reader.close();
		writer.close();
		printOutput(MESSAGE_ADD, fileName(args), content);
	}

	public static void delete(int pointToBeDeleted, String[] args) throws IOException {
		File file = new File(fileName(args));
		
		createEmptyFile(file, args);
		
		int pointer = 1;
		String currentLine;
		String deletedContent = null;
		boolean editedFile = false;
		
		File tempFile = new File("tempFile.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		while ((currentLine = reader.readLine()) != null) {
			String pointNumber = currentLine.substring(0, currentLine.indexOf("."));
			String contentOfPoint = currentLine.substring(currentLine.indexOf(" "));
			
			if (pointToBeDeleted > Integer.valueOf(pointNumber)) {
				printOutput(MESSAGE_DELETED_DOES_NOT_EXIST);
				break;
			} else if (Integer.valueOf(pointNumber) == pointToBeDeleted) {
				deletedContent = contentOfPoint;
				editedFile = true;
				printOutput(MESSAGE_DELETED, fileName(args),
						deletedContent);
				continue;
			} else if (pointNumber.equals(pointer)) {
				continue;
			} else {
				String pointerStr = (String.valueOf(pointer)).concat(".");
				currentLine = pointerStr.concat(contentOfPoint);
			}
			pointer++;
			writer.write(currentLine + System.getProperty("line.separator"));

		}
		writer.close();
		reader.close();
		
		if (editedFile) {
			writer.close();
			reader.close();
			File input = new File(fileName(args));
			input.delete();
			new File("tempFile.txt").renameTo(input);
		}
		if (tempFile.exists()){
			tempFile.delete();
		}
	}
	
	private static void createEmptyFile(File file, String[] args) throws IOException{
		if (file.createNewFile()) {
			printOutput(MESSAGE_CREATE_EMPTY_FILE, fileName(args));
		}
	}
}
