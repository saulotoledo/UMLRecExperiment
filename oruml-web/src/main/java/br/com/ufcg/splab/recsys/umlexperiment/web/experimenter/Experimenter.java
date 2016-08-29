package br.com.ufcg.splab.recsys.umlexperiment.web.experimenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.boot.json.JsonJsonParser;

import br.com.ufcg.splab.recsys.ontorec.recommender.MappingsProcessor;
import br.com.ufcg.splab.recsys.ontorec.recommender.OntoRecApproach;
import br.com.ufcg.splab.recsys.ontorec.weighting.BFSPathNodeWeightingApproach;
import br.com.ufcg.splab.recsys.ontorec.weighting.NodeWeightingApproach;
import br.com.ufcg.splab.recsys.orumlcombinations.utils.FileMappingsProcessor;
import br.com.ufcg.splab.recsys.recommender.Approach;
import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;
import br.com.ufcg.splab.recsys.umlexperiment.approach.BagOfWordsApproach;
import br.com.ufcg.splab.recsys.umlexperiment.approach.ContentBasedApproach;
import br.com.ufcg.splab.recsys.umlexperiment.approach.RandomApproach;
import br.com.ufcg.splab.recsys.umlexperiment.web.config.ConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ItemProfile;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ItemProfileRepository;

public abstract class Experimenter implements Serializable {

    protected static final long serialVersionUID = 7356380693327553274L;

    // protected Approach approach;

    protected String contentBasedSimilarityClass;

    protected String ontoRecMappingsFile;

    protected String ontoRecOntologyFile;

    protected String ontoRecApproach;

    protected boolean ontoRecLambdaValue;

    protected boolean ontoRecUpsilonValue;

    protected int ontoRecMaxHeight;

    protected String ontoRecSimilarityClass;

    public Experimenter(ConfigInfo configInfo) throws Exception {
        this.contentBasedSimilarityClass = configInfo.getContentBasedSimilarityClass();
        this.ontoRecMappingsFile = configInfo.getOntoRecMappingsFile();
        this.ontoRecOntologyFile = configInfo.getOntoRecOntologyFile();
        this.ontoRecApproach = configInfo.getOntoRecApproach();
        this.ontoRecLambdaValue = configInfo.getOntoRecLambdaValue();
        this.ontoRecUpsilonValue = configInfo.getOntoRecUpsilonValue();
        this.ontoRecMaxHeight = configInfo.getOntoRecMaxHeight();
        this.ontoRecSimilarityClass = configInfo.getOntoRecSimilarityClass();

        // this.approach = this.sortApproach();
    }

    protected String sortValue(String[] items) {

        Random randomGenerator = new Random();
        return items[randomGenerator.nextInt(items.length)];
    }

    /*
     * protected Approach sortApproach() throws Exception { String
     * sortedApproach = this.sortValue( new String[] { "random", "contentBased",
     * "bagOfWords", "ontoRec" }); Approach approach; if
     * (sortedApproach.equals("random")) { approach = this.getRandomApproach();
     * } else if (sortedApproach.equals("contentBased")) { approach =
     * this.getContentBasedApproach(); } else if
     * (sortedApproach.equals("bagOfWords")) { approach =
     * this.getBagOfWordsApproach(); } else { approach =
     * this.getOntoRecApproach(); } return approach; }
     */

    protected Approach getRandomApproach() {

        return new RandomApproach();
    }

    protected Approach getContentBasedApproach() throws Exception {

        Class<?> c = Class.forName(
                "br.com.ufcg.splab.recsys.umlexperiment.approach.similarity." + this.contentBasedSimilarityClass);
        SimilarityMethod sm = (SimilarityMethod) c.newInstance();

        return new ContentBasedApproach(sm);
    }

    protected Approach getBagOfWordsApproach() throws Exception {

        return new BagOfWordsApproach();
    }

    protected Approach getOntoRecApproach() throws Exception {

        Class<?> similarityClass = Class
                .forName("br.com.ufcg.splab.recsys.umlexperiment.approach.similarity." + this.ontoRecSimilarityClass);
        SimilarityMethod sm = (SimilarityMethod) similarityClass.newInstance();

        NodeWeightingApproach<String> nwa;
        if (this.ontoRecApproach.equals("BFSPath")) {
            nwa = new BFSPathNodeWeightingApproach<String>();
        } else if (this.ontoRecApproach.equals("TaunthAncestor")) {
            nwa = new TaunthAncestorNodeWeightingApproach<String>();
        } else {
            throw new Exception("Invalid OntoRec approach: " + this.ontoRecApproach);
        }

        MappingsProcessor mp = new FileMappingsProcessor(this.ontoRecMappingsFile);

        OntoRecApproach ora = new OntoRecApproach(this.ontoRecOntologyFile, nwa, this.ontoRecLambdaValue,
                this.ontoRecUpsilonValue, mp, sm);
        ora.setMaxHeight(this.ontoRecMaxHeight);

        return ora;
    }

    public Map<String, List<SimilarityMapper>> runApproaches(Map<String, Double> userProfile, String filesBasePath,
            String diagramType, ItemProfileRepository itemProfileRepository) throws Exception {

        List<Approach> approaches = new ArrayList<Approach>();
        approaches.add(this.getRandomApproach());
        approaches.add(this.getBagOfWordsApproach());
        approaches.add(this.getContentBasedApproach());
        approaches.add(this.getOntoRecApproach());

        Map<String, List<SimilarityMapper>> result = new HashMap<String, List<SimilarityMapper>>();

        JsonJsonParser parser;
        for (Approach approach : approaches) {
            approach.setUserProfile(userProfile);

            // TODO: this should be removed:
            if (approach instanceof BagOfWordsApproach) {
                ((BagOfWordsApproach) approach).setRecommendedItemType(diagramType);
                ((BagOfWordsApproach) approach).setFilesBasePath(filesBasePath);
            }

            Iterable<ItemProfile> items = itemProfileRepository.findByType(diagramType);
            Iterator<ItemProfile> it = items.iterator();
            while (it.hasNext()) {
                ItemProfile item = it.next();
                String jsonProfile = item.getProfile();

                parser = new JsonJsonParser();
                Map<String, Object> itemProfileObject = parser.parseMap(jsonProfile);

                Map<String, Double> itemProfile = new HashMap<String, Double>();
                itemProfile.put(item.getName() + ":ID", (double) item.getId());
                for (String key : itemProfileObject.keySet()) {
                    itemProfile.put(key, ((Integer) itemProfileObject.get(key)).doubleValue());
                }

                approach.addItem(itemProfile);
            }

            result.put(approach.getClass().getSimpleName(), approach.getOrderedItems());
        }

        return result;
    }

    /*
     * public Approach getApproach() { return this.approach; } public void
     * setApproach(Approach approach) { this.approach = approach; }
     */
}
