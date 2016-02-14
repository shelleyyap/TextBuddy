
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
			stopWithErrorMessage("Please input text file.");
		}
	}

	public static void exitIfUnacceptableArguments(String[] args) {
		if (!isTxtFile(args)) {
			stopWithErrorMessage("Wrong file type, please input text files.");
			System.exit(0);
		}
	}

	public static void stopWithErrorMessage(String content) {
		System.out.println(content);
		System.exit(0);
	}

	public static void printWelcomeMessage(String[] args) {
		System.out.printf("Welcome to TextBuddy. %s is ready for use\n",
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
		System.out.print("command: ");
	}

	public static void printOutput(String content, String fileName) {
		System.out.printf(content.concat("\n"), fileName);
	}

	public static void printErrorMessageWithoutStopping() {
		System.out.println("Wrong command. Please re-enter command");
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
		if (file.createNewFile()) {
			printOutput("New empty file %s is created.", fileName(args));
		} else {
			Scanner sc = new Scanner(file);
			if (file.length() == 0) {
				printOutput("%s is empty", fileName(args));
			} else {
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					System.out.println(line);
				}
			}
			sc.close();
		}
	}

	public static void clear(String[] args) throws IOException {
		File file = new File(fileName(args));
		if (file.createNewFile()) {
			printOutput("New empty file %s is created.", fileName(args));
		} else {
			PrintWriter inFile = new PrintWriter(fileName(args));
			inFile.close();
			printOutput("all content deleted from %s", fileName(args));
		}
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
		System.out.printf("added to %s: \"%s\"\n", fileName(args), content);
	}

	public static void delete(int pointToBeDeleted, String[] args) throws IOException {
		File file = new File(fileName(args));
		
		if (file.createNewFile()) {
			printOutput("New empty file %s is created.", fileName(args));
		}
		
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
				System.out.println("Point to be deleted does not exist.");
				break;
			} else if (Integer.valueOf(pointNumber) == pointToBeDeleted) {
				deletedContent = contentOfPoint;
				editedFile = true;
				System.out.printf("deleted from %s: %s \n", fileName(args),
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
}
