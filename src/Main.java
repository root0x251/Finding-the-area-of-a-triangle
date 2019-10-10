import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * @author Vyacheslav Alekseevich
 * @date 09/10/2019
 */

public class Main {
    public static void main(String[] args) throws IOException {
        SearchInFile searchInFile = new SearchInFile();
        Scanner scanner = new Scanner(System.in);
        BufferedReader file = null;
        int counter = 3;

        // check have we file or not
        System.out.print("Enter path to the file: ");
        String fileLocation = scanner.nextLine();
        while (true) {
            try {
                file = new BufferedReader(new FileReader(fileLocation));
                file.close();
                break;
            } catch (FileNotFoundException e) {
                if (counter == 0) {
                    System.out.println("It's not funny, bye");
                    System.exit(0);
                } else {
                    scanner = new Scanner(System.in);
                    System.out.print("File not found, try again (count of try " + counter + "): ");
                    fileLocation = scanner.nextLine();
                    counter--;
                }
            }
        }

        // pass the file name for parsing
        searchInFile.setFileName(fileLocation);

        // create new thread
        searchInFile.start();

        // animation
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println();

        System.out.println("The search began!");

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println();

        // the way for a creating file
        System.out.print("Enter path to the file: ");
        String destinationFile = scanner.nextLine();

        scanner.close();

        // waiting for a thread to close
        try {
            searchInFile.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // this is our max area string
        String[] stringWithMaxVal = searchInFile.getMaxString();
        BigInteger countOfCoordinateString = BigInteger.valueOf(searchInFile.getCountOfStringCoordinate() + searchInFile.getCountOfError());

        // write to file
        try (PrintWriter out = new PrintWriter(destinationFile)) {
            for (String s : stringWithMaxVal) {
                out.print(s + " ");
            }
            out.println();
            out.println();
            out.println("Area of triangle: " + searchInFile.getMaxAreaVal());
            out.println();
            out.println("Count of string coordinate: " + countOfCoordinateString);
            out.println("Count of error: " + searchInFile.getCountOfError());
            out.println();
            out.println();
            out.println("Time to duration: " + searchInFile.getTimeToDuration() + " sec");
        }

        System.out.println();

        System.out.println("The file was created, all information is in the file that is" + destinationFile + " goodbye");
    }

}

// create a new Thread to parse file
class SearchInFile extends Thread {
    private String fileName;            // set file name
    private String[] maxString;         // coordinate of triangle
    private float maxAreaVal = 0;       // max area of triangle
    private int countOfError = 0;
    private float timeToDuration = 0;
    private int countOfStringCoordinate = 0;

    String[] getMaxString() {
        return maxString;
    }

    int getCountOfError() {
        return countOfError;
    }

    float getTimeToDuration() {
        return timeToDuration;
    }

    int getCountOfStringCoordinate() {
        return countOfStringCoordinate;
    }

    public float getMaxAreaVal() {
        return maxAreaVal;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        try {
            readFromFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeToDuration = (float) (System.nanoTime() - startTime) / 1_000_000_000;
    }

    // parse file
    private void readFromFile(String fileName) throws IOException {
        try (Stream<String> line = Files.lines(Paths.get(fileName))) {
            line.forEach(this::checkForError);
        }
    }

    // check for error
    private void checkForError(String string) {
        if (isEmptyString(string) && isNormalCountCoordinatePoint(string) && isEmptyFirstChar(string)) {
            String[] splitStr = string.split(" ");
            if (splitStr.length == 6) {
                findMax(splitStr);
            }
        }
    }

    private boolean isEmptyString(String line) {
        countOfError++;
        return !line.equals("") && !line.equals(" ");
    }

    private boolean isEmptyFirstChar(String line) {
        countOfError++;
        return line.charAt(0) != ' ';
    }

    private boolean isNormalCountCoordinatePoint(String line) {
        countOfError++;
        return !line.contains(",") && !line.contains(".");
    }

    // get max val
    private void findMax(String[] strings) {
        countOfStringCoordinate++;
        int x1, y1, x2, y2, x3, y3;
        x1 = Integer.parseInt(strings[0]);
        y1 = Integer.parseInt(strings[1]);
        x2 = Integer.parseInt(strings[2]);
        y2 = Integer.parseInt(strings[3]);
        x3 = Integer.parseInt(strings[4]);
        y3 = Integer.parseInt(strings[5]);

        float area = (float) 0.5 * ((x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3));

        if (area < 0) {
            area = -area;
        }

        if (maxAreaVal < area) {
            maxAreaVal = area;
            maxString = strings;
        }
    }
}