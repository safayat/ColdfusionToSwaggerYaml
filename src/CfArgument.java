public class CfArgument{
    String name;
    String def;
    String type;
    String required;
    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    CfArgument() {
    }

    public CfArgument(String name, String def, String type, String required, String description) {
        this.name = name;
        this.def = def;
        this.type = type;
        this.required = required;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getType() {
        return type.isEmpty() ? "string" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequired() {
        return required.isEmpty() ? "false" : required;
    }

    public void setRequired(String required) {
        this.required = required;
    }


    private String getSpaces(int c){
        StringBuilder stringBuilder = new StringBuilder();
        while (c-->0)stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    public String getAsYamlString(int space){
        return  new StringBuilder()
        .append(getSpaces(space++)).append("- in: query").append(getName()).append("\n")
        .append(getSpaces(space)).append("name: ").append(getName()).append("\n")
        .append(getSpaces(space)).append("type: ").append(getType()).append("\n")
        .append(getSpaces(space)).append("description: ").append(getDescription()).append("\n")
        .append(getSpaces(space)).append("required: ").append(getRequired()).append("\n")
        .append(getSpaces(space)).append("default: ").append(getDef()).append("\n")
        .toString();
    }

}
