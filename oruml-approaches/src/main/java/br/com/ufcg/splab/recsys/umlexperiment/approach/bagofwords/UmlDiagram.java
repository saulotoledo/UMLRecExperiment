package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

public class UmlDiagram
{
    private String name;
    private String content;
    private String fileName;

    public static final String FIELD_NAME = "name";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENT = "content";

    public UmlDiagram()
    {
    }

    public UmlDiagram(String name, String content, String fileName)
    {
        this.setName(name);
        this.setContent(content);
        this.setFileName(fileName);
    }

    public String getName()
    {
        return this.name;
    }

    public UmlDiagram setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getContent()
    {
        return this.content;
    }

    public UmlDiagram setContent(String content)
    {
        this.content = content;
        return this;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public UmlDiagram setFileName(String fileName)
    {
        this.fileName = fileName;
        return this;
    }
}
