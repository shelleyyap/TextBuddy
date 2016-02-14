/**
 * This class is used to manipulate text in a file. Only add, delete, display
 * clear, search and sort commands are available in this version. The output will be of the
 * form of numerical pointers. 
 * 
 * Assumptions: 
 * 1. Adding the same content twice is currently allowed. 
 * 2. The file parameter given when running the program is strictly of the format 
 *    <file name>.txt. Other formats breaks the program.
 * 3. For the command display, if the file does not exist and the command is called,
 *    a new text file with the file name passed in will be created. Similarly for 
 *    the command clear and delete.
 * 4. If add, delete and search are used without specifying content, an error message is
 *    displayed.
 */
import java.util.ArrayList;
import java.util.Collections;
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
	
	private static final int CONTENT_ARRAY_SIZE = 2;
	private static final int CONTENT_ARRAY_COMMAND = 0;
	private static final int CONTENT_ARRAY_CONTENT = 1;
	
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

	private static void executeCommandsUntilExitCommand(String[] args)
			throws IOException {
		Scanner sc = new Scanner(System.in);
		printCommand();
		String[] cmdLine = splitCommand(sc.nextLine());
		while (!isExitCommand(getCommand(cmdLine))) {
			if (isDisplay(getCommand(cmdLine))){
				display(args);
			} else if (isClear(getCommand(cmdLine))) {
				clear(args);
			} else if (isSort(getCommand(cmdLine))) {
				sort(args);
			} else  if (isAdd(cmdLine)) {
				add(getContent(cmdLine), args);
			} else if (isDelete(cmdLine)) {
				delete(Integer.valueOf(getContent(cmdLine)), args);
			} else if (isSearch(cmdLine)){
				search(getContent(cmdLine), args);
			} else {
				printErrorMessageWithoutStopping();
			}
			printCommand();
			cmdLine = splitCommand(sc.nextLine());
		}
		sc.close();
	}
	
	private static String[] splitCommand(String cmdLine){
		int indexOfSpace = cmdLine.indexOf(" ");
		String[] cmdAndContent = new String[CONTENT_ARRAY_SIZE];
		if (indexOfSpace == -1){
			cmdAndContent[CONTENT_ARRAY_COMMAND] = cmdLine;
			cmdAndContent[CONTENT_ARRAY_CONTENT] = null;
		} else {
			cmdAndContent[CONTENT_ARRAY_COMMAND] = cmdLine.substring(0, indexOfSpace);
			cmdAndContent[CONTENT_ARRAY_CONTENT] = cmdLine.substring(indexOfSpace + 1);
		}
		return cmdAndContent;
	}

	private static String getCommand(String[] cmdAndContent){
		return cmdAndContent[CONTENT_ARRAY_COMMAND];
	}

	private static String getContent(String[] cmdAndContent){
		return cmdAndContent[CONTENT_ARRAY_CONTENT];
	}

	/** These are the set of boolean functions. */
	public static boolean isTxtFile(String[] args) {
		CharSequence txt = ".txt";
		return fileName(args).contains(txt);
	}

	public static boolean isExitCommand(String cmdLine) {
		return cmdLine.toLowerCase().equals("exit");
	}

	public static boolean isDisplay(String cmdLine) {
		return cmdLine.toLowerCase().equals("display");
	}

	private static boolean isAdd(String[] cmdLine) {
		if (getContent(cmdLine) == null){
			return false;
		} else {
			return getCommand(cmdLine).toLowerCase().equals("add");
		}
	}

	private static boolean isDelete(String[] cmdLine) {
		if (getContent(cmdLine) == null){
			return false;
		} else {
			return getCommand(cmdLine).toLowerCase().equals("delete");
		}
	}

	public static boolean isClear(String cmdLine) {
		return cmdLine.toLowerCase().equals("clear");
	}

	private static boolean isSearch(String[] cmdLine) {
		if (getContent(cmdLine) == null){
			return false;
		} else {
			return getCommand(cmdLine).toLowerCase().equals("search");
		}
	}
	
	private static boolean isSort(String cmdLine) {
		return cmdLine.toLowerCase().equals("sort");
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
				printOutput(line);
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
		File tempFile = new File("tempFile.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int pointer = 1;
		String currentLine;
		boolean editedFile = false;
		
		createEmptyFile(file, args);
		
		while ((currentLine = reader.readLine()) != null) {
			String pointNumber = currentLine.substring(0, currentLine.indexOf("."));
			String contentOfPoint = currentLine.substring(currentLine.indexOf(" "));
			
			if (Integer.valueOf(pointNumber) == pointToBeDeleted) {
				editedFile = true;
				printOutput(MESSAGE_DELETED, fileName(args), contentOfPoint);
				continue;
			} else if (!pointNumber.equals(pointer)){
				String pointerStr = (String.valueOf(pointer)).concat(".");
				currentLine = pointerStr.concat(contentOfPoint);
			}
			pointer++;
			writer.write(currentLine + System.getProperty("line.separator"));
		}
		
		writer.close();
		reader.close();
		
		filesCleaning(tempFile, editedFile, args);
	}
	
	public static void search(String content, String[] args) throws IOException{
		File file = new File(fileName(args));
		String currentLine;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		createEmptyFile(file, args);

		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.contains(content)){
				printOutput(currentLine);
			}
		}
		reader.close();
	}
	
	public static void sort(String[] args) throws IOException {
		ArrayList<String> fileContent = new ArrayList<String>();
		File file = new File(fileName(args));
		String currentLine;		
		File tempFile = new File("tempFile.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		createEmptyFile(file, args);
		
		while ((currentLine = reader.readLine()) != null) {
			String contentOfPoint = currentLine.substring(currentLine.indexOf(" "));
			fileContent.add(contentOfPoint);
		}
		
		Collections.sort(fileContent);
		
		writeToFile(writer, fileContent);
		
		writer.close();
		reader.close();

		renameFile(args);
		printOutput(MESSAGE_SORTED);
	}
	
	private static void createEmptyFile(File file, String[] args) throws IOException{
		if (file.createNewFile()) {
			printOutput(MESSAGE_CREATE_EMPTY_FILE, fileName(args));
		}
	}
	
	private static void renameFile(String[] args){
		File input = new File(fileName(args));
		input.delete();
		new File("tempFile.txt").renameTo(input);
	}
	
	private static void filesCleaning(File tempFile, boolean editedFile, String[] args){
		if (editedFile) {
			renameFile(args);
		} else {
			System.out.println(MESSAGE_DELETED_DOES_NOT_EXIST);
		}
		
		if (tempFile.exists()){
			tempFile.delete();
		}	
	}
	
	private static void writeToFile(BufferedWriter writer, ArrayList<String> fileContent) throws IOException{
		int iterator = 1;
		for(String line : fileContent){
			writer.write(iterator + "." + line + "\n");
			iterator++;
		}
	}
}
