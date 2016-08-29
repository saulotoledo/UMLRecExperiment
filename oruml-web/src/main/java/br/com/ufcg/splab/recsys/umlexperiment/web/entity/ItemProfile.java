package br.com.ufcg.splab.recsys.umlexperiment.web.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ItemProfile implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String profile;

    protected ItemProfile()
    {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }

    public ItemProfile(String name, String type, String profile)
    {
        this.name = name;
        this.type = type;
        this.profile = profile;
    }

    public String getName()
    {
        return this.name;
    }

    public String getProfile()
    {
        return this.profile;
    }

    public Integer getId()
    {
        return this.id;
    }

    public ItemProfile setId(Integer id)
    {
        this.id = id;
        return this;
    }

    public String getType()
    {
        return this.type;
    }

    public ItemProfile setType(String type)
    {
        this.type = type;
        return this;
    }

    public ItemProfile setName(String name)
    {
        this.name = name;
        return this;
    }

    public ItemProfile setProfile(String profile)
    {
        this.profile = profile;
        return this;
    }

    @Override
    public String toString()
    {
        return String.format("%s[id=%d, name='%s', profile='%s']",
            this.getClass().getSimpleName(), this.getId(), this.getName(),
            this.getProfile());
    }
}
