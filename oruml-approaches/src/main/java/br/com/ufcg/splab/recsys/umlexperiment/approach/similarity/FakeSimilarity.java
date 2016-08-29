package br.com.ufcg.splab.recsys.umlexperiment.approach.similarity;

import java.util.Map;

import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;

public class FakeSimilarity implements SimilarityMethod
{
    @Override
    public Double calculate(Map<String, Double> v1, Map<String, Double> v2)
        throws Exception
    {
        return 1d;
    }
}
