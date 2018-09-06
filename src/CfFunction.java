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

}
