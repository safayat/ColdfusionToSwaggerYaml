import java.util.ArrayList;
import java.util.List;

public class CfFunction{
    String name;
    String summary;
    String accessType;
    public List<CfArgument> cfArgumentList;

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    CfFunction(String name) {
        this.name = name;
        this.cfArgumentList = new ArrayList<>();
    }

    CfFunction() {
        this.cfArgumentList = new ArrayList<>();
    }

    public String getMethod() {
        return name.toLowerCase().startsWith("get")? "get" : "post";
    }


    public List<CfArgument> getCfArgumentList() {
        return cfArgumentList;
    }

    public void setCfArgumentList(List<CfArgument> cfArgumentList) {
        this.cfArgumentList = cfArgumentList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static String getSpaces(int c){
        StringBuilder stringBuilder = new StringBuilder();
        while (c-->0)stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    public  String getAsYamlString(int space){
        StringBuilder yamlStringBuilder = new StringBuilder();
        yamlStringBuilder.append(getSpaces(space++)).append("/").append(getName()).append("/:\n");
        yamlStringBuilder.append(getSpaces(space++)).append(getMethod()).append(":\n");
        yamlStringBuilder.append(getSpaces(space)).append("summary: " + Util.toQuote(getSummary())).append("\n");
        if(getCfArgumentList().size()>2){
            yamlStringBuilder.append(getSpaces(space++)).append("parameters:").append("\n");
            for(CfArgument cfArgument: getCfArgumentList()){
                if(!cfArgument.getName().equalsIgnoreCase("password")
                        && !cfArgument.getName().equalsIgnoreCase("email"))
                    yamlStringBuilder.append(cfArgument.getAsYamlString(space));
            }
        }
        yamlStringBuilder.append(getSpaces(--space)).append("responses:").append("\n");
        yamlStringBuilder.append(getSpaces(++space)).append("200:").append("\n");
        yamlStringBuilder.append(getSpaces(++space)).append("description: "+ "Success").append("\n");
        yamlStringBuilder.append("   security:\n" +
                "   - api_key: []\n").append("\n");

        return yamlStringBuilder.toString();
    }

}
