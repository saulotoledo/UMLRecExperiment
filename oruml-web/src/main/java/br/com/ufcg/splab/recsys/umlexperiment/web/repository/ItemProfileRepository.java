package br.com.ufcg.splab.recsys.umlexperiment.web.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ItemProfile;

public interface ItemProfileRepository
    extends CrudRepository<ItemProfile, Integer>
{
    /*
     * @Modifying
     * @Query(value = "TRUNCATE TABLE item_profile", nativeQuery = true) void
     * truncate();
     */

    List<ItemProfile> findByIdIn(Collection<Integer> ids);
    
    Iterable<ItemProfile> findByType(String type);

    ItemProfile findOneByName(String name);
}
