package br.com.ufcg.splab.recsys.umlexperiment.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SequenceConfigInfo implements ConfigInfo
{
    @Value("${sequence.method.contentBased.similarityClass}")
    protected String contentBasedSimilarityClass;

    @Value("${sequence.method.ontorec.mappingsFile}")
    protected String ontoRecMappingsFile;

    @Value("${sequence.method.ontorec.ontologyFile}")
    protected String ontoRecOntologyFile;

    @Value("${sequence.method.ontorec.approach}")
    protected String ontoRecApproach;

    @Value("${sequence.method.ontorec.lambdaValue}")
    protected boolean ontoRecLambdaValue;

    @Value("${sequence.method.ontorec.upsilonValue}")
    protected boolean ontoRecUpsilonValue;

    @Value("${sequence.method.ontorec.maxHeight}")
    protected int ontoRecMaxHeight;

    @Value("${sequence.method.ontorec.similarityClass}")
    protected String ontoRecSimilarityClass;

    @Override
    public String getContentBasedSimilarityClass()
    {
        return this.contentBasedSimilarityClass;
    }

    @Override
    public void setContentBasedSimilarityClass(
        String contentBasedSimilarityClass)
    {
        this.contentBasedSimilarityClass = contentBasedSimilarityClass;
    }

    @Override
    public String getOntoRecMappingsFile()
    {
        return this.ontoRecMappingsFile;
    }

    @Override
    public void setOntoRecMappingsFile(String ontoRecMappingsFile)
    {
        this.ontoRecMappingsFile = ontoRecMappingsFile;
    }

    @Override
    public String getOntoRecOntologyFile()
    {
        return this.ontoRecOntologyFile;
    }

    @Override
    public void setOntoRecOntologyFile(String ontoRecOntologyFile)
    {
        this.ontoRecOntologyFile = ontoRecOntologyFile;
    }

    @Override
    public String getOntoRecApproach()
    {
        return this.ontoRecApproach;
    }

    @Override
    public void setOntoRecApproach(String ontoRecApproach)
    {
        this.ontoRecApproach = ontoRecApproach;
    }

    @Override
    public boolean getOntoRecLambdaValue()
    {
        return this.ontoRecLambdaValue;
    }

    @Override
    public void setOntoRecLambdaValue(boolean ontoRecLambdaValue)
    {
        this.ontoRecLambdaValue = ontoRecLambdaValue;
    }

    @Override
    public boolean getOntoRecUpsilonValue()
    {
        return this.ontoRecUpsilonValue;
    }

    @Override
    public void setOntoRecUpsilonValue(boolean ontoRecUpsilonValue)
    {
        this.ontoRecUpsilonValue = ontoRecUpsilonValue;
    }

    @Override
    public int getOntoRecMaxHeight()
    {
        return this.ontoRecMaxHeight;
    }

    @Override
    public void setOntoRecMaxHeight(int ontoRecMaxHeight)
    {
        this.ontoRecMaxHeight = ontoRecMaxHeight;
    }

    @Override
    public String getOntoRecSimilarityClass()
    {
        return this.ontoRecSimilarityClass;
    }

    @Override
    public void setOntoRecSimilarityClass(String ontoRecSimilarityClass)
    {
        this.ontoRecSimilarityClass = ontoRecSimilarityClass;
    }
}
