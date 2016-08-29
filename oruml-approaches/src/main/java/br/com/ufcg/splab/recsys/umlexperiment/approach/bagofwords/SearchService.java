package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchService extends AbstractLucene
{
    public SearchService(String contextPath, String diagramsPath,
        String indexPath)
    {
        super(contextPath, diagramsPath, indexPath);
    }

    public List<SearchResult> findDiagramByContent(String content)
    {
        List<SearchResult> results = this.runDiagramsSearch(content);

        String shortContent = content;

        // TODO: 20 is a fixed min value here, this should be changed:
        while (results.size() < 20) {
            shortContent = this.generateShortKeyword(shortContent);

            // TODO: this "break" approach should be refactored:
            // Stops the loop if we cannot get a shorter search string:
            if (shortContent.equals(content)) {
                break;
            }
            results = this.mergeResults(results,
                this.runDiagramsSearch(shortContent));

            content = shortContent;
        }

        return results;
    }

    private List<SearchResult> runDiagramsSearch(String content)
    {
        Query q = null;
        try {
            q = new QueryParser(LUCENE_VERSION, UmlDiagram.FIELD_CONTENT,
                this.analyzer).parse(content);

        } catch (ParseException pex) {
            throw new RuntimeException("Error on query parsing.", pex);
        }

        List<SearchResult> results = this.executeQuery(q);
        return results;
    }

    private List<SearchResult> executeQuery(Query q)
    {
        List<SearchResult> results = new ArrayList<SearchResult>();

        try {
            IndexReader reader = IndexReader.open(this.dir);
            IndexSearcher searcher = new IndexSearcher(reader);

            TopDocs topDocs = searcher.search(q, this.MAX_HITS);
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);

                File f = new File(this.contextPath + "/" + this.diagramsPath,
                    d.get(UmlDiagram.FIELD_FILENAME));
                UmlDiagram diagram = this.loadUmlDiagram(f);

                SearchResult result = new SearchResult(diagram, hits[i].score);
                results.add(result);
            }
        } catch (CorruptIndexException e) {
            throw new RuntimeException("Error opening IndexReader.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error opening file.", e);
        }

        return results;
    }

    private String generateShortKeyword(String previousKeyword)
    {
        String result = previousKeyword;
        String[] keywords = result.split("\\+");

        String newResult = "";
        for (int i = 1; i < keywords.length; i++) {
            if ( !newResult.equals("")) {
                newResult += "+";
            }
            newResult += keywords[i];
        }
        if ( !newResult.equals("")) {
            result = newResult;
        }

        // System.out.println("prevKeyw: " + previousKeyword + " - NewResult: "
        // + result);

        return result;
    }

    private List<SearchResult> mergeResults(List<SearchResult> result1,
        List<SearchResult> result2)
    {
        List<SearchResult> mergedResult = new LinkedList<SearchResult>();
        mergedResult.addAll(result1);
        for (SearchResult searchResultToAdd : result2) {
            Boolean exists = false;
            for (SearchResult searchResultToVerify : mergedResult) {
                if (searchResultToAdd.getDiagram().getFileName()
                    .equals(searchResultToVerify.getDiagram().getFileName())) {
                    exists = true;
                }
            }

            if ( !exists) {
                mergedResult.add(searchResultToAdd);
            }
        }
        return mergedResult;
    }
}
