
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CfcToYamlBuilder {

    private Predicate<CfArgument> isPasswordArgument()
    {
        return p -> p.getName().toLowerCase().equalsIgnoreCase("password");
    }

    private Predicate<CfFunction> hasPasswordArgument()
    {
        return p -> p.getCfArgumentList().
                stream().
                    filter(isPasswordArgument()).
                      collect(Collectors.toList()).size()>0
                && p.getAccessType().equalsIgnoreCase("private") == false;
    }

    private String getSpaces(int c){
        StringBuilder stringBuilder = new StringBuilder();
        while (c-->0)stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private String createSwaggerInfoString(int space){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("info:\n")
            .append(getSpaces(++space)).append("description: Blogsoft Swagger api documentation\n")
            .append(getSpaces(space)).append("version: \"1.0.0\"\n")
            .append(getSpaces(space)).append("title: \"Blogsoft mobile Api\"\n");
        return stringBuilder.toString();
    }

    private String getPropertyFromFunction(String source, String name){
        Pattern namePattern = Pattern.compile("<cffunction .*?" + name +"=\"([A-Za-z\\d_ ]*)\"[^>]*?>");
        Matcher nameMatcher = namePattern.matcher(source);
        String value = "";
        if(nameMatcher.find()){
            value = nameMatcher.group(1);
        }
        return value;
    }

    private String getPropertyFromArgument(String source, String name){
        Pattern namePattern = Pattern.compile("<cfargument .*?" + name +"=\"([A-Za-z\\d_ ]*)\"[^/]*?/>");
        Matcher nameMatcher = namePattern.matcher(source);
        String value = "";
        if(nameMatcher.find()){
            value = nameMatcher.group(1);
        }
        return value;
    }

    private List<CfFunction> parseFunctions(String source){
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

        return cfFunctions.stream().filter(hasPasswordArgument()).collect(Collectors.toList());
    }

    public String getSecurityDefinitions(){
        return "securityDefinitions:\n" +
                "  api_key:\n" +
                "    type: \"apiKey\"\n" +
                "    name: \"access_token\"\n" +
                "    in: \"header\"\n";

    }

    public String cfcToYamlString(String cfcString){
        final StringBuilder yamlStringBuilder = new StringBuilder();
        yamlStringBuilder.append("swagger: \"2.0\"\n")
                .append(createSwaggerInfoString(0))
                .append("host: \"blogsoft.local\"\n")
                .append("basePath: \"/restapi\"\n")
                .append("paths:\n");

        parseFunctions(cfcString).forEach(cf -> yamlStringBuilder.append(cf.getAsYamlString(1)));
        yamlStringBuilder.append(getSecurityDefinitions());
        return yamlStringBuilder.toString();

    }

}