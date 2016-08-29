package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.util.Date;

import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ExperimentInstance;

public class Step01 extends Step
{
    @Override
    public Integer getOrder()
    {
        return 1;
    }

    @Override
    protected void proccess() throws Exception
    {
        if (this.experimentInstance == null) {
            this.experimentInstance = new ExperimentInstance(
                (String) this.requestData.get("userName"),
                (String) this.requestData.get("userEmail"),
                (String) this.requestData.get("userDegree"),
                (Boolean) this.requestData.get("hasProfessionalExperience"),
                (String) this.requestData.get("startExperiment"),
                (String) this.requestData.get("currentExperiment"), new Date());
        }
    }
}
