package br.com.ufcg.splab.recsys.umlexperiment.web.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ExperimentInstance;

public interface ExperimentInstanceRepository
    extends CrudRepository<ExperimentInstance, Integer>
{
}
