package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.util.Date;

public class Step04 extends Step
{
    @Override
    public Integer getOrder()
    {
        return 4;
    }

    @Override
    protected void proccess() throws Exception
    {
        this.experimentInstance.setEndDatetime(new Date());
    }
}
