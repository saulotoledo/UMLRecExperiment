package br.com.ufcg.splab.recsys.umlexperiment.web.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.JSONObject;
import org.springframework.boot.json.JsonJsonParser;

import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;

@Entity
public class ExperimentInstance implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userDegree;

    @Column(nullable = false)
    private Boolean hasProfessionalExperience;

    @Column
    private String startExperiment;

    @Column
    private String currentExperiment;

    @Column
    private String userProfile;

    @Column
    private String randomApproachFullList;

    @Column
    private String bagOfWordsApproachFullList;

    @Column
    private String contentBasedApproachFullList;

    @Column
    private String knowledgeBasedApproachFullList;

    @Column
    private String randomApproachTopN;

    @Column
    private String bagOfWordsApproachTopN;

    @Column
    private String contentBasedApproachTopN;

    @Column
    private String knowledgeBasedApproachTopN;

    @Column
    private String randomApproachSelections;

    @Column
    private String bagOfWordsApproachSelections;

    @Column
    private String contentBasedApproachSelections;

    @Column
    private String knowledgeBasedApproachSelections;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDatetime;

    @Transient
    private Map<String, List<SimilarityMapper>> approachesResult;

    @Transient
    private String currentApproachName;

    protected ExperimentInstance()
    {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public ExperimentInstance(String userName, String userEmail,
        String userDegree, Boolean hasProfessionalExperience,
        String startExperiment, String currentExperiment, Date startDatetime)
    {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userDegree = userDegree;
        this.hasProfessionalExperience = hasProfessionalExperience;
        this.startExperiment = startExperiment;
        this.currentExperiment = currentExperiment;
        this.startDatetime = startDatetime;
    }

    public Integer getId()
    {
        return this.id;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public ExperimentInstance setUserName(String userName)
    {
        this.userName = userName;
        return this;
    }

    public String getUserEmail()
    {
        return this.userEmail;
    }

    public ExperimentInstance setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
        return this;
    }

    public String getUserDegree()
    {
        return this.userDegree;
    }

    public ExperimentInstance setUserDegree(String userDegree)
    {
        this.userDegree = userDegree;
        return this;
    }

    public Boolean getHasProfessionalExperience()
    {
        return this.hasProfessionalExperience;
    }

    public ExperimentInstance setHasProfessionalExperience(
        Boolean hasProfessionalExperience)
    {
        this.hasProfessionalExperience = hasProfessionalExperience;
        return this;
    }

    public String getStartExperiment()
    {
        return this.startExperiment;
    }

    public ExperimentInstance setStartExperiment(String startExperiment)
    {
        this.startExperiment = startExperiment;
        return this;
    }

    public String getCurrentExperiment()
    {
        return this.currentExperiment;
    }

    public ExperimentInstance setCurrentExperiment(String currentExperiment)
    {
        this.currentExperiment = currentExperiment;
        return this;
    }

    public Map<String, Double> getUserProfile()
    {
        Map<String, Double> userProfile = null;
        if (this.userProfile != null) {
            JsonJsonParser parser = new JsonJsonParser();

            Map<String, Object> userProfileFromDb = parser
                .parseMap(this.userProfile);

            userProfile = new HashMap<String, Double>();
            for (String feature : userProfileFromDb.keySet()) {
                userProfile.put(feature, Double
                    .valueOf(String.valueOf(userProfileFromDb.get(feature))));
            }
        }
        return userProfile;
    }

    public List<String> getUserProfileSelections()
    {
        List<String> result = null;
        Map<String, Double> userProfile = this.getUserProfile();
        if (userProfile != null) {
            result = new LinkedList<String>();
            for (String key : userProfile.keySet()) {
                if (userProfile.get(key) > 0) {
                    result.add(key);
                }
            }
        }
        return result;
    }

    public ExperimentInstance setUserProfile(Map<String, Double> userProfile)
    {
        JSONObject jsonProfile = new JSONObject();
        for (String key : userProfile.keySet()) {
            jsonProfile.put(key, userProfile.get(key));
        }

        this.userProfile = jsonProfile.toString();
        return this;
    }

    public String getRandomApproachFullList()
    {
        return this.randomApproachFullList;
    }

    public ExperimentInstance setRandomApproachFullList(
        String randomApproachFullList)
    {
        this.randomApproachFullList = randomApproachFullList;
        return this;
    }

    public String getBagOfWordsApproachFullList()
    {
        return this.bagOfWordsApproachFullList;
    }

    public ExperimentInstance setBagOfWordsApproachFullList(
        String bagOfWordsApproachFullList)
    {
        this.bagOfWordsApproachFullList = bagOfWordsApproachFullList;
        return this;
    }

    public String getContentBasedApproachFullList()
    {
        return this.contentBasedApproachFullList;
    }

    public ExperimentInstance setContentBasedApproachFullList(
        String contentBasedApproachFullList)
    {
        this.contentBasedApproachFullList = contentBasedApproachFullList;
        return this;
    }

    public String getKnowledgeBasedApproachFullList()
    {
        return this.knowledgeBasedApproachFullList;
    }

    public ExperimentInstance setKnowledgeBasedApproachFullList(
        String knowledgeBasedApproachFullList)
    {
        this.knowledgeBasedApproachFullList = knowledgeBasedApproachFullList;
        return this;
    }

    public String getRandomApproachTopN()
    {
        return this.randomApproachTopN;
    }

    public ExperimentInstance setRandomApproachTopN(String randomApproachTopN)
    {
        this.randomApproachTopN = randomApproachTopN;
        return this;
    }

    public String getBagOfWordsApproachTopN()
    {
        return this.bagOfWordsApproachTopN;
    }

    public ExperimentInstance setBagOfWordsApproachTopN(
        String bagOfWordsApproachTopN)
    {
        this.bagOfWordsApproachTopN = bagOfWordsApproachTopN;
        return this;
    }

    public String getContentBasedApproachTopN()
    {
        return this.contentBasedApproachTopN;
    }

    public ExperimentInstance setContentBasedApproachTopN(
        String contentBasedApproachTopN)
    {
        this.contentBasedApproachTopN = contentBasedApproachTopN;
        return this;
    }

    public String getKnowledgeBasedApproachTopN()
    {
        return this.knowledgeBasedApproachTopN;
    }

    public ExperimentInstance setKnowledgeBasedApproachTopN(
        String knowledgeBasedApproachTopN)
    {
        this.knowledgeBasedApproachTopN = knowledgeBasedApproachTopN;
        return this;
    }

    public String getRandomApproachSelections()
    {
        return this.randomApproachSelections;
    }

    public ExperimentInstance setRandomApproachSelections(
        String randomApproachSelections)
    {
        this.randomApproachSelections = randomApproachSelections;
        return this;
    }

    public String getBagOfWordsApproachSelections()
    {
        return this.bagOfWordsApproachSelections;
    }

    public ExperimentInstance setBagOfWordsApproachSelections(
        String bagOfWordsApproachSelections)
    {
        this.bagOfWordsApproachSelections = bagOfWordsApproachSelections;
        return this;
    }

    public String getContentBasedApproachSelections()
    {
        return this.contentBasedApproachSelections;
    }

    public ExperimentInstance setContentBasedApproachSelections(
        String contentBasedApproachSelections)
    {
        this.contentBasedApproachSelections = contentBasedApproachSelections;
        return this;
    }

    public String getKnowledgeBasedApproachSelections()
    {
        return this.knowledgeBasedApproachSelections;
    }

    public ExperimentInstance setKnowledgeBasedApproachSelections(
        String knowledgeBasedApproachSelections)
    {
        this.knowledgeBasedApproachSelections = knowledgeBasedApproachSelections;
        return this;
    }

    public Date getStartDatetime()
    {
        return this.startDatetime;
    }

    public ExperimentInstance setStartDatetime(Date startDatetime)
    {
        this.startDatetime = startDatetime;
        return this;
    }

    public Date getEndDatetime()
    {
        return this.endDatetime;
    }

    public ExperimentInstance setEndDatetime(Date endDatetime)
    {
        this.endDatetime = endDatetime;
        return this;
    }

    public Map<String, List<SimilarityMapper>> getApproachesResult()
    {
        return this.approachesResult;
    }

    public ExperimentInstance setApproachesResult(
        Map<String, List<SimilarityMapper>> approachesResult)
    {
        this.approachesResult = approachesResult;
        return this;
    }

    public ExperimentInstance setCurrentApproachName(String currentApproachName)
    {
        this.currentApproachName = currentApproachName;
        return this;
    }

    public List<Integer> getCurrentTopN() throws Exception
    {
        List<Integer> result = null;
        if (this.currentApproachName != null) {
            // OntoRec is KnowledgeBased:
            String methodName = "get"
                + this.currentApproachName.replace("OntoRec", "KnowledgeBased")
                + "TopN";
            String resultStr = null;
            try {
                Method method = this.getClass().getMethod(methodName);
                resultStr = (String) method.invoke(this);
            } catch (SecurityException e) {
                throw e;
            } catch (NoSuchMethodException e) {
                throw e;
            } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
                throw e;
            }

            if (resultStr != null) {
                JsonJsonParser parser = new JsonJsonParser();

                List<Object> resultAsObject = parser.parseList(resultStr);
                result = new LinkedList<Integer>();

                for (Object item : resultAsObject) {
                    result.add(Integer.valueOf(String.valueOf(item)));
                }
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return String.format(
            "%s[id=%d, name='%s', email='%s', degree='%s', randomApproach='%s', bagOfWordsApproach='%s', contentBasedApproach='%s', knowledgeBasedApproach='%s', startedAt='%s', endedAt='%s']",
            this.getClass().getSimpleName(), this.getUserDegree(),
            this.getRandomApproachTopN(), this.getBagOfWordsApproachTopN(),
            this.getContentBasedApproachTopN(),
            this.getKnowledgeBasedApproachTopN(), this.getStartDatetime(),
            this.getEndDatetime());
    }
}
