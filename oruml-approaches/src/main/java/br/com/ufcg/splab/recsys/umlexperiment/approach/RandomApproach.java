package br.com.ufcg.splab.recsys.umlexperiment.approach;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.recommender.Approach;
import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;
import br.com.ufcg.splab.recsys.umlexperiment.approach.similarity.FakeSimilarity;

public class RandomApproach extends Approach
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
        .getLogger(RandomApproach.class);

    public RandomApproach()
    {
        super(new FakeSimilarity());
        LOGGER.debug(String.format(
            "Created a %s approach with the similarity method 'FakeSimilarity'",
            this.getClass().getSimpleName()));
    }

    public RandomApproach(SimilarityMethod similarityMethod)
    {
        super(new FakeSimilarity());
        LOGGER.debug(String.format(
            "Created a %s approach with the similarity method 'FakeSimilarity'",
            this.getClass().getSimpleName()));
    }

    @Override
    public Map<String, Double> getUserProfile() throws Exception
    {
        return this.userProfile;
    }

    // TODO: This approach runs unnecessary parent code. Refactor!
    @Override
    public List<SimilarityMapper> getOrderedItems() throws Exception
    {
        List<SimilarityMapper> values = super.getOrderedItems();
        Collections.shuffle(values);

        LOGGER.debug(String.format("%s approach: The ordered result is '%s'",
            this.getClass().getSimpleName(), values));

        return values;
    }
}
