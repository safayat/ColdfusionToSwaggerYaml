
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileManager {


    public static String readFileAsString(String filePath){
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.forEach(l->stringBuilder.append(l));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void write(String filePath, String yamlString){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));

            writer.write(yamlString);


            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}