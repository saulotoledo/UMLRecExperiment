package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

public class SearchResult
{
    private UmlDiagram diagram;
    private float score;

    public SearchResult(UmlDiagram diagram, float score)
    {
        this.diagram = diagram;
        this.score = score;
    }

    public UmlDiagram getDiagram()
    {
        return this.diagram;
    }

    public void setDiagram(UmlDiagram diagram)
    {
        this.diagram = diagram;
    }

    public float getScore()
    {
        return this.score;
    }

    public void setScore(float score)
    {
        this.score = score;
    }

    @Override
    public String toString()
    {
        return String.format("%s: [ %s -> %s ]", this.getClass().getSimpleName(), this.getDiagram().getFileName(),
            this.getScore());
    }
}
