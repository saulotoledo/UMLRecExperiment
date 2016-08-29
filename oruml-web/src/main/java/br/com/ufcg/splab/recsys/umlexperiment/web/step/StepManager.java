package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import br.com.ufcg.splab.recsys.umlexperiment.web.config.ClassConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.config.SequenceConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ExperimentInstance;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ExperimentInstanceRepository;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ItemProfileRepository;

public class StepManager
{
    public static final String EXPERIMENT_INSTANCE_ID_SESSION_KEY = "experimentInstanceId";
    public static final String CURRENT_EXPERIMENT_PAGE_SESSION_KEY = "currentPage";

    private ClassConfigInfo classConfigInfo;
    private SequenceConfigInfo sequenceConfigInfo;
    private String filesBasePath;

    private ExperimentInstance experimentInstance;
    private ExperimentInstanceRepository experimentInstanceRepository;
    private ItemProfileRepository itemProfileRepository;
    private HttpSession session;
    private int currentPage;
    private Map<Integer, Step> steps;
    private Map<String, Object> requestData;

    public StepManager(HttpSession session, Map<String, Object> requestData,
        ExperimentInstanceRepository experimentInstanceRepository,
        ItemProfileRepository itemProfileRepository,
        ClassConfigInfo classConfigInfo, SequenceConfigInfo sequenceConfigInfo,
        String filesBasePath) throws Exception
    {
        this.session = session;
        this.experimentInstanceRepository = experimentInstanceRepository;
        this.itemProfileRepository = itemProfileRepository;
        this.classConfigInfo = classConfigInfo;
        this.sequenceConfigInfo = sequenceConfigInfo;
        this.filesBasePath = filesBasePath;
        this.initExperimentInstance(requestData);
    }

    private void initExperimentInstance(Map<String, Object> requestData)
        throws Exception
    {
        this.initializeSteps();

        this.requestData = requestData;
        if (this.session.getAttribute(
            StepManager.EXPERIMENT_INSTANCE_ID_SESSION_KEY) != null) {
            int experimentInstanceId = (int) this.session
                .getAttribute(StepManager.EXPERIMENT_INSTANCE_ID_SESSION_KEY);
            this.experimentInstance = this.experimentInstanceRepository
                .findOne(experimentInstanceId);
        }
        this.forward();
    }

    private void initializeSteps() throws Exception
    {
        this.steps = new HashMap<Integer, Step>();

        List<Step> steps = new ArrayList<Step>();
        steps.add(new Step01());
        steps.add(new Step02());
        steps.add(new Step03());
        steps.add(new Step04());

        for (Step currStep : steps) {
            if (this.steps.containsKey(currStep.getOrder())) {
                throw new Exception(
                    "Different steps with the same order value.");
            }
            this.steps.put(currStep.getOrder(), currStep);
        }

        this.currentPage = 1; // <- Intro page
        if (this.session.getAttribute(
            StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY) != null) {
            this.currentPage = (int) this.session
                .getAttribute(StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY);
        }
    }

    public int forward() throws Exception
    {
        if (this.currentPage <= this.steps.size()) {
            Step step = this.steps.get(this.currentPage);
            this.experimentInstance = step
                .loadDiagramsConfigs(this.classConfigInfo,
                    this.sequenceConfigInfo, this.filesBasePath)
                .setExperimentInstance(this.experimentInstance)
                .setExperimentInstanceRepository(
                    this.experimentInstanceRepository)
                .setItemProfileRepository(this.itemProfileRepository)
                .setRequestData(this.requestData).commit();

            if (step.shouldAdvanceStep()) {
                this.currentPage++;
            }
            this.session.setAttribute(
                StepManager.EXPERIMENT_INSTANCE_ID_SESSION_KEY,
                this.experimentInstance.getId());

            this.session.setAttribute(
                StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY,
                this.currentPage);
        }
        return this.currentPage;
    }

    public ExperimentInstance getExperimentInstance()
    {
        return this.experimentInstance;
    }

    public int current()
    {
        return this.currentPage;
    }
}
