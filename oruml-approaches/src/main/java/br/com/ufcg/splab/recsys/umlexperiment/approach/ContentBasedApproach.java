package br.com.ufcg.splab.recsys.umlexperiment.approach;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.recommender.Approach;
import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;

public class ContentBasedApproach extends Approach
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
        .getLogger(ContentBasedApproach.class);

    public ContentBasedApproach(SimilarityMethod similarityMethod)
    {
        super(similarityMethod);
        LOGGER.debug(String.format(
            "Created a %s approach with the similarity method '%s'",
            this.getClass().getSimpleName(),
            similarityMethod.getClass().getSimpleName()));
    }

    @Override
    public Map<String, Double> getUserProfile() throws Exception
    {
        return this.userProfile;
    }

    @Override
    public List<SimilarityMapper> getOrderedItems() throws Exception
    {
        for (String key : this.itemsProfiles.get(0).keySet()) {
            if ( !key.contains(":ID") && !this.userProfile.containsKey(key)) {
                this.userProfile.put(key, 0d);
            }
        }

        List<SimilarityMapper> values = super.getOrderedItems();

        LOGGER.debug(String.format("%s approach: The ordered result is '%s'",
            this.getClass().getSimpleName(), values));

        return values;
    }
}
