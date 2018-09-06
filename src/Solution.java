
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    public static Predicate<CfArgument> isPasswordArgument()
    {
        return p -> p.getName().toLowerCase().equalsIgnoreCase("password");
    }

    public static Predicate<CfFunction> hasPasswordArgument()
    {
        return p -> p.getCfArgumentList().
                stream().
                    filter(isPasswordArgument()).
                      collect(Collectors.toList()).size()>0
                && p.getAccessType().equalsIgnoreCase("private") == false;
    }

    public static String readFileAsString(String filePath){
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.forEach(l->stringBuilder.append(l));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static String getPropertyFromFunction(String source, String name){
        Pattern namePattern = Pattern.compile("<cffunction .*?" + name +"=\"([A-Za-z\\d_ ]*)\"[^>]*?>");
        Matcher nameMatcher = namePattern.matcher(source);
        String value = "";
        if(nameMatcher.find()){
            value = nameMatcher.group(1);
        }
        return value;
    }

    public static String getPropertyFromArgument(String source, String name){
        Pattern namePattern = Pattern.compile("<cfargument .*?" + name +"=\"([A-Za-z\\d_ ]*)\"[^/]*?/>");
        Matcher nameMatcher = namePattern.matcher(source);
        String value = "";
        if(nameMatcher.find()){
            value = nameMatcher.group(1);
        }
        return value;
    }

    public static List<CfFunction> getFunctions(String source, String exp){
        Pattern p = Pattern.compile("<cffunction .*?>.*?</cffunction>");
        Matcher m =  p.matcher(source);
        List<CfFunction> cfFunctions = new ArrayList<>();
        while (m.find()){
            String functionTag = m.group();
            String methodName = getPropertyFromFunction(functionTag, "name");
            Pattern argumentPattern = Pattern.compile("<cfargument .*?/>");
            Matcher argumentMatcher = argumentPattern.matcher(functionTag);
            List<CfArgument> cfArguments = new ArrayList<>();
            while (argumentMatcher.find()){
                String argumentString = argumentMatcher.group();
                String name = getPropertyFromArgument(argumentString, "name");
                String type = getPropertyFromArgument(argumentString, "type");
                String required = getPropertyFromArgument(argumentString, "required");
                String def = getPropertyFromArgument(argumentString, "default");
                String description = getPropertyFromArgument(argumentString, "hint");
                CfArgument cfArgument = new CfArgument(name,def,type,required, description);
                cfArguments.add(cfArgument);
            }

            CfFunction cfFunction = new CfFunction(methodName);
            cfFunction.setSummary(getPropertyFromFunction(functionTag,"hint"));
            cfFunction.setAccessType(getPropertyFromFunction(functionTag,"access"));
            cfFunction.setCfArgumentList(cfArguments);
            cfFunctions.add(cfFunction);
        }

        return cfFunctions;
    }

    public static String getSpaces(int c){
        StringBuilder stringBuilder = new StringBuilder();
        while (c-->0)stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    public static String createSwaggerInfoString(int space){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("info:\n")
            .append(getSpaces(++space)).append("description: Blogsoft Swagger api documentation\n")
            .append(getSpaces(space)).append("version: \"1.0.0\"\n")
            .append(getSpaces(space)).append("title: \"Blogsoft mobile Api\"\n");
        return stringBuilder.toString();
    }

    public static void appendYamlLine(StringBuilder yamlStringBuilder, int space, String line){
        yamlStringBuilder.append(getSpaces(space)).append(line).append("\n");
    }
    public static String createRestPathString(CfFunction cfFunction, int space){
        StringBuilder yamlStringBuilder = new StringBuilder();
        yamlStringBuilder.append(getSpaces(space)).append("/restapi/").append(cfFunction.getName()).append("/:\n");
        space = space+2;
        yamlStringBuilder.append(getSpaces(space)).append(cfFunction.getMethod()).append(":\n");
        space++;
        yamlStringBuilder.append(getSpaces(space)).append("summary: " + cfFunction.getSummary()).append("\n");
        yamlStringBuilder.append(getSpaces(space)).append("parameters:").append("\n");
        space++;
        for(CfArgument cfArgument: cfFunction.getCfArgumentList()){
            appendYamlLine(yamlStringBuilder, space, "- in: query");
            space = space+2;
            appendYamlLine(yamlStringBuilder, space, "name: " + cfArgument.getName());
            appendYamlLine(yamlStringBuilder, space, "type: " + cfArgument.getType());

/*
            yamlStringBuilder.append(getSpaces(space)).append("name: ").append(cfArgument.getName()).append("\n");
            yamlStringBuilder.append(getSpaces(space)).append("type: ").append(cfArgument.getType()).append("\n");
            yamlStringBuilder.append(getSpaces(space)).append("description: ").append(cfArgument.getDescription()).append("\n");
            yamlStringBuilder.append(getSpaces(space)).append("required: ").append(cfArgument.getRequired()).append("\n");
            yamlStringBuilder.append(getSpaces(space)).append("default: ").append(cfArgument.getDef()).append("\n");
*/
            space--;
            space--;
        }
        space--;
        yamlStringBuilder.append(getSpaces(space)).append("responses:").append("\n");
        space++;
        space++;
        yamlStringBuilder.append(getSpaces(space)).append("200:").append("\n");
        space++;
        yamlStringBuilder.append(getSpaces(space)).append("description: "+ "Success").append("\n");

        return yamlStringBuilder.toString();
    }

    public static void main(String[] args){

        String fileName = "/Users/cefalo/Desktop/project/blogsoft/cfc/Api2.cfc";
        String file = readFileAsString(fileName);
        System.out.println(file);
        List<CfFunction> cfFunctions =  getFunctions(file, "<cffunction .*>.*<\\/cffunction>");
        cfFunctions = cfFunctions.stream().filter(hasPasswordArgument()).collect(Collectors.toList());

        List<String> allMethodNames = cfFunctions.stream().map(cf ->cf.getName()).collect(Collectors.toList());
        System.out.println(allMethodNames);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("output.yml"));

            writer.write("swagger: \"2.0\"\n");
            writer.write(createSwaggerInfoString(0));
            writer.write("host: \"blogsoft.local\"\n");
            writer.write("basePath: \"\"\n");
            writer.write("paths:\n");

        for(CfFunction cfFunction : cfFunctions){
            try {
                writer.write(createRestPathString(cfFunction, 1));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}