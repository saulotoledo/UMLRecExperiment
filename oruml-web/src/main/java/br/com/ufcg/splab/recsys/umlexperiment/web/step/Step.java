package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.util.Map;

import br.com.ufcg.splab.recsys.umlexperiment.web.config.ClassConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.config.SequenceConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ExperimentInstance;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ExperimentInstanceRepository;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ItemProfileRepository;

public abstract class Step
{
    protected ExperimentInstance experimentInstance;
    protected Map<String, Object> requestData;
    protected ExperimentInstanceRepository experimentInstanceRepository;
    protected ItemProfileRepository itemProfileRepository;

    protected ClassConfigInfo classConfigInfo;
    protected SequenceConfigInfo sequenceConfigInfo;
    protected String filesBasePath;

    protected boolean shouldAdvanceStepConfig = true;

    public abstract Integer getOrder();

    public Step setExperimentInstanceRepository(
        ExperimentInstanceRepository experimentInstanceRepository)
    {
        this.experimentInstanceRepository = experimentInstanceRepository;
        return this;
    }

    public Step setItemProfileRepository(
        ItemProfileRepository itemProfileRepository)
    {
        this.itemProfileRepository = itemProfileRepository;
        return this;
    }

    public Step loadDiagramsConfigs(ClassConfigInfo classConfigInfo,
        SequenceConfigInfo sequenceConfigInfo, String filesBasePath)
    {
        this.classConfigInfo = classConfigInfo;
        this.sequenceConfigInfo = sequenceConfigInfo;
        this.filesBasePath = filesBasePath;
        return this;
    }

    public Step setExperimentInstance(ExperimentInstance experimentInstance)
    {
        this.experimentInstance = experimentInstance;
        return this;
    }

    public Step setRequestData(Map<String, Object> requestData)
    {
        this.requestData = requestData;
        return this;
    }

    public boolean shouldAdvanceStep()
    {
        return this.shouldAdvanceStepConfig;
    }

    protected Step disableStepAdvance()
    {
        this.shouldAdvanceStepConfig = false;
        return this;
    }

    protected Step enableStepAdvance()
    {
        this.shouldAdvanceStepConfig = true;
        return this;
    }

    public ExperimentInstance commit() throws Exception
    {
        if (this.experimentInstance == null && this.getOrder() > 1) {
            throw new Exception("Invalid experiment instance.");
        }

        if (this.requestData == null) {
            throw new Exception("Invalid request data.");
        }

        this.proccess();
        this.experimentInstance = this.experimentInstanceRepository
            .save(this.experimentInstance);

        return this.experimentInstance;
    }

    protected abstract void proccess() throws Exception;
}
