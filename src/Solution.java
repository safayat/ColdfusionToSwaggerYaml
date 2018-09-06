import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {



    public static void main(String[] args){


        String fileName = "/Users/cefalo/Desktop/project/blogsoft/cfc/Api2.cfc";
        String file = FileManager.readFileAsString(fileName);
        System.out.println(file);

        CfcToYamlBuilder cfcToYamlBuilder = new CfcToYamlBuilder();
        FileManager.write("output.yml", cfcToYamlBuilder.cfcToYamlString(file));



    }
}