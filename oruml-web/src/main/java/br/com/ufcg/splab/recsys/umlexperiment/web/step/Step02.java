package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.umlexperiment.web.experimenter.ClassExperimenter;
import br.com.ufcg.splab.recsys.umlexperiment.web.experimenter.Experimenter;
import br.com.ufcg.splab.recsys.umlexperiment.web.experimenter.SequenceExperimenter;

public class Step02 extends Step
{
    @Override
    public Integer getOrder()
    {
        return 2;
    }

    @Override
    protected void proccess() throws Exception
    {
        // JsonJsonParser parser = new JsonJsonParser();
        // List<Object> userSelections = parser
        // .parseList((String) this.requestData.get("userprofile"));

        String[] userSelections = (String[]) this.requestData
            .get("userprofile");

        Map<String, Double> userProfile = new HashMap<String, Double>();
        for (Object feature : userSelections) {
            userProfile.put(String.valueOf(feature), 1d);
        }

        String currentExperimentName = this.experimentInstance
            .getCurrentExperiment();
        Map<String, List<SimilarityMapper>> approachesResult = this
            .getExperimenterByName(currentExperimentName)
            .runApproaches(userProfile, this.filesBasePath,
                currentExperimentName, this.itemProfileRepository);

        this.experimentInstance.setUserProfile(userProfile);
        this.experimentInstance.setApproachesResult(approachesResult);
    }

    private Experimenter getExperimenterByName(String name) throws Exception
    {
        Experimenter experimenter;
        if (name.equals("class")) {
            experimenter = new ClassExperimenter(this.classConfigInfo);
        } else {
            experimenter = new SequenceExperimenter(this.sequenceConfigInfo);
        }

        return experimenter;
    }
}
