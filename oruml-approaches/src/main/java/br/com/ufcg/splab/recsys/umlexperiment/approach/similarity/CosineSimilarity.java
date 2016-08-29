package br.com.ufcg.splab.recsys.umlexperiment.approach.similarity;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;

public class CosineSimilarity implements SimilarityMethod
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
        .getLogger(CosineSimilarity.class);

    @Override
    public Double calculate(Map<String, Double> v1, Map<String, Double> v2)
        throws Exception
    {
        // The size of each vector must be lesser than Integer.MAX_VALUE
        // (this is the max value returned by the size() method):
        if (v1.size() != v2.size()) {
            throw new Exception(
                "The vectors must have the same size, but we received " + v1
                    + " and " + v2);
        }

        if ( !v1.keySet().containsAll(v2.keySet())) {
            throw new Exception(
                "The vectors must have the same keys, but we received " + v1
                    + " and " + v2);
        }

        Double sum1 = 0d;
        Double sum2 = 0d;
        Double sum3 = 0d;

        for (String key : v1.keySet()) {
            sum1 += v1.get(key) * v2.get(key);
            sum2 += Math.pow(v1.get(key), 2);
            sum3 += Math.pow(v2.get(key), 2);
        }

        Double result;
        if (sum2 == 0 || sum3 == 0) {
            result = 0d;
        } else {
            result = sum1 / (Math.sqrt(sum2) * Math.sqrt(sum3));
        }

        LOGGER.debug(String.format(
            "Calculated the value '%s' for the cosine similarity between the vectors '%s' and '%s'.",
            result.toString(), v1, v2));

        return result;
    }
}
