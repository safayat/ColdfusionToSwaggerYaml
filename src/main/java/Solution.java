import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public static void main(String[] args) throws Exception{


        String fileName = "/Users/cefalo/Desktop/project/blogsoft/cfc/Api2.cfc";
        String fileAsString = FileManager.readFileAsString(fileName);
        CfcToYamlBuilder cfcToYamlBuilder = new CfcToYamlBuilder();
        String yamlString = cfcToYamlBuilder.cfcToYamlString(fileAsString);
        FileManager.write("output.yml", yamlString);




    }
}


