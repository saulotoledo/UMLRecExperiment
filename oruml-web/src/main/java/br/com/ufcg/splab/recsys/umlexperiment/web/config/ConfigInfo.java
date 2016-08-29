package br.com.ufcg.splab.recsys.umlexperiment.web.config;

public interface ConfigInfo
{
    public abstract String getContentBasedSimilarityClass();

    public void setContentBasedSimilarityClass(
        String contentBasedSimilarityClass);

    public String getOntoRecMappingsFile();

    public void setOntoRecMappingsFile(String ontoRecMappingsFile);

    public String getOntoRecOntologyFile();

    public void setOntoRecOntologyFile(String ontoRecOntologyFile);

    public String getOntoRecApproach();

    public void setOntoRecApproach(String ontoRecApproach);

    public boolean getOntoRecLambdaValue();

    public void setOntoRecLambdaValue(boolean ontoRecLambdaValue);

    public boolean getOntoRecUpsilonValue();

    public void setOntoRecUpsilonValue(boolean ontoRecUpsilonValue);

    public int getOntoRecMaxHeight();

    public void setOntoRecMaxHeight(int ontoRecMaxHeight);

    public String getOntoRecSimilarityClass();

    public void setOntoRecSimilarityClass(String ontoRecSimilarityClass);
}
