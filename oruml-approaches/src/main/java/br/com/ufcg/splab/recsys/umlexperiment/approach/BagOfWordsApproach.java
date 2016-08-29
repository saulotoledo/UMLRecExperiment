package br.com.ufcg.splab.recsys.umlexperiment.approach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.recommender.Approach;
import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.recommender.SimilarityMethod;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.AbstractLucene;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.SearchResult;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.SearchService;

public class BagOfWordsApproach extends Approach
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
        .getLogger(BagOfWordsApproach.class);

    protected String filesBasePath;

    protected String currentRecommendedItemType;

    public BagOfWordsApproach()
    {
        super(null);
        LOGGER.debug(String.format("Created a %s approach",
            this.getClass().getSimpleName()));
    }

    public BagOfWordsApproach(SimilarityMethod similarityMethod)
    {
        super(null);
        LOGGER.debug(String.format("Created a %s approach",
            this.getClass().getSimpleName()));
    }

    public Approach setRecommendedItemType(String recommendedItemType)
    {
        this.currentRecommendedItemType = recommendedItemType;
        LOGGER.debug(String.format(
            "%s approach: Setting the recommended item type as '%s'",
            this.getClass().getSimpleName(), recommendedItemType));
        return this;
    }

    public BagOfWordsApproach setFilesBasePath(String filesBasePath)
    {
        this.filesBasePath = filesBasePath;
        LOGGER.debug(String.format(
            "%s approach: Setting the files base path fo the as '%s'",
            this.getClass().getSimpleName(), filesBasePath));
        return this;
    }

    public String getRecommendedItemType()
    {
        return this.currentRecommendedItemType;
    }

    @Override
    public Map<String, Double> getUserProfile() throws Exception
    {
        return this.userProfile;
    }

    private Integer getDiagramIdByName(String name)
    {
        for (Map<String, Double> itemProfile : this.getItemsProfiles()) {
            String diaramInfoKeyName = this.getDiaramInfoKeyName(itemProfile);

            if (diaramInfoKeyName.replace(":ID", "").equals(name)) {
                return itemProfile.get(diaramInfoKeyName).intValue();
            }
        }

        LOGGER.debug(String.format(
            "%s approach: The file '%s' was not found at database. FIX IT!",
            this.getClass().getSimpleName(), name));

        return null;
    }

    @Override
    public List<SimilarityMapper> getOrderedItems() throws Exception
    {
        if (this.getUserProfile() == null) {
            throw new Exception("Invalid user profile");
        }

        Map<String, String> equivalences = new HashMap<String, String>();

        if (this.getRecommendedItemType().equals("class")) {
            equivalences.put("PSA",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(ownedAttribute||\"UML:Attribute\")+(\"isStatic=true\"||\"ownerScope=classifier\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PADV",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(ownedAttribute||\"UML:Attribute\")+(defaultValue||Attribute.initialValue)+(packagedElement||\"UML:Model\")");
            equivalences.put("PASC",
                "(\"uml:AssociationClass\"||\"UML:AssociationClass\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PRI",
                "(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\")+(interfaceRealization||\"Dependency.supplier\")+Interface+(packagedElement||\"UML:Model\")");
            equivalences.put("PABC",
                "(\"uml:Class\"||\"UML:Class\")+\"isAbstract=true\"+(packagedElement||\"UML:Model\")");
            equivalences.put("PTC",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(ownedTemplateSignature||TemplateParameter)+(packagedElement||\"UML:Model\")");
            equivalences.put("PEN",
                "(\"uml:Enumeration\"||UMLEnumeration)+(packagedElement||\"UML:Model\")");
            equivalences.put("POP",
                "(\"uml:Class\"||\"UML:Class\")+ownedAttribute+\"uml:Port\"+(packagedElement||\"UML:Model\")");
            equivalences.put("PSO",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(ownedOperation||\"UML:Operation\")+(\"isStatic=true\"||\"ownerScope=classifier\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PAO",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(ownedOperation||\"UML:Operation\")+\"isAbstract=true\"+(packagedElement||\"UML:Model\")");
            equivalences.put("PNAA",
                "(\"uml:Association\"||\"UML:Association\")+(navigableOwnedEnd||\"isNavigable=true\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PDA",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+ownedAttribute+\"isDerived=true\"+(packagedElement||\"UML:Model\")");
            equivalences.put("POQ",
                "((\"uml:Class\"||\"UML:Class\")||(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\"))+(qualifier||\"AssociationEnd.qualifier\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PSHA",
                "(\"uml:Association\"||\"UML:Association\")+(ownedEnd||AssociationEnd)+(\"aggregation=shared\"||\"aggregation=aggregate\")+(packagedElement||\"UML:Model\")");
            equivalences.put("PCOA",
                "(\"uml:Association\"||\"UML:Association\")+(ownedEnd||AssociationEnd)+\"aggregation=composite\"+(packagedElement||\"UML:Model\")");
            equivalences.put("PGS",
                "\"uml:GeneralizationSet\"+(packagedElement||\"UML:Model\")");
            equivalences.put("POD",
                "((\"uml:Dependency\"||\"UML:Dependency\")||(\"uml:Usage\"||\"UML:Usage\")||(\"uml:Abstraction\"||\"UML:Abstraction\")||\"uml:InterfaceRealization\"||\"uml:ComponentRealization\"||\"UML:Permission\"||\"Dependency.supplier\")+(packagedElement||\"UML:Model\")");
            equivalences.put("POI",
                "(\"uml:Interface\"||\"UML:Interface\"||name=\"interface\")+(packagedElement||\"UML:Model\")");
            equivalences.put("POG",
                "(\"uml:Generalization\"||\"UML:Generalization\")+(packagedElement||\"UML:Model\")");
        } else {
            equivalences.put("PSM",
                "\"uml:Message\"+\"messageSort=asynchSignal\"+\"messageSort=synchCall\"+packagedElement");
            equivalences.put("PACM",
                "\"uml:Message\"+\"messageSort=asynchSignal\"+\"messageSort=asynchCall\"+packagedElement");
            equivalences.put("PASM",
                "\"uml:Message\"+\"messageSort=asynchSignal\"+\"messageSort=asynchSignal\"+packagedElement");
            equivalences.put("PRM",
                "\"uml:Message\"+\"messageSort=reply\"+packagedElement");
            equivalences.put("PCM",
                "\"uml:Message\"+\"messageSort=createMessage\"+packagedElement");
            equivalences.put("PDM",
                "\"uml:Message\"+\"messageSort=deleteMessage\"+packagedElement");
            equivalences.put("PCoACF",
                "\"uml:CombinedFragment\"+\"interactionOperator=alt\"+packagedElement");
            equivalences.put("PCoOCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=opt\"+packagedElement");
            equivalences.put("PItCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=loop\"+packagedElement");
            equivalences.put("PBCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=break\"+packagedElement");
            equivalences.put("PCuCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=par\"+packagedElement");
            equivalences.put("PWCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=seq\"+packagedElement");
            equivalences.put("PSCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=strict\"+packagedElement");
            equivalences.put("PNCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=neg\"+packagedElement");
            equivalences.put("PCrCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=critical\"+packagedElement");
            equivalences.put("PIgCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=ignore\"+packagedElement");
            equivalences.put("PCsCF",
                "\"uml:CombinedFragment\"+\"interactionOperator=consider\"+packagedElement");
            equivalences.put("PACF",
                "\"uml:CombinedFragment\"+\"interactionOperator=assert\"+packagedElement");
            equivalences.put("PIU", "\"uml:InteractionUse\"+packagedElement");
            equivalences.put("PAc", "\"uml:Actor\"+packagedElement");
            equivalences.put("PSI", "\"uml:StateInvariant\"+packagedElement");
        }

        String keyword = "";
        for (String selection : this.getUserProfile().keySet()) {
            if (keyword.length() > 0) {
                keyword += "+";
            }
            keyword += equivalences.get(selection);
        }

        List<SearchResult> luceneResult = this.doSearch(keyword);

        List<SimilarityMapper> values = new LinkedList<SimilarityMapper>();

        for (SearchResult searchResult : luceneResult) {
            Integer diagramId = this
                .getDiagramIdByName(searchResult.getDiagram().getName());

            // TODO: Another problem of the current Lucene approach: the
            // itemProfile is empty! Fix it!
            Map<String, Double> itemProfile = new HashMap<String, Double>();
            values.add(new SimilarityMapper(diagramId, itemProfile,
                Double.valueOf(searchResult.getScore())));
        }

        // Making sure about the order:
        Collections.sort(values);
        Collections.reverse(values);

        LOGGER.debug(String.format("%s approach: The ordered result is '%s'",
            this.getClass().getSimpleName(), values));

        return values;
    }

    private List<SearchResult> doSearch(String keyword) throws Exception
    {
        LOGGER.debug(String.format(
            "%s approach: Performing a Lucene search by using the search string '%s'",
            this.getClass().getSimpleName(), keyword));

        List<SearchResult> results = new ArrayList<SearchResult>();

        SearchService service;
        if (this.getRecommendedItemType().equals("class")) {
            service = new SearchService(this.filesBasePath,
                AbstractLucene.CLASS_DIAGRAMS_PATH,
                AbstractLucene.CLASS_INDEX_PATH);
        } else {
            service = new SearchService(this.filesBasePath,
                AbstractLucene.SEQUENCE_DIAGRAMS_PATH,
                AbstractLucene.SEQUENCE_INDEX_PATH);
        }

        results = service.findDiagramByContent(keyword);

        LOGGER.debug(String.format(
            "%s approach: Lucene returned the following result: '%s'",
            this.getClass().getSimpleName(), results));

        return results;
    }
}
