package br.com.ufcg.splab.recsys.umlexperiment.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.AbstractLucene;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.SearchResult;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.SearchService;

// TODO: Remove controller
@RestController
@RequestMapping("/bow")
public class LuceneSearchController extends AbstractController
{
    @Value("${system.diagrams-path}")
    private String filesBasePath;

    @RequestMapping
    private ResponseEntity<List<Map<String, Object>>> sortExperimenter(
        HttpServletRequest request) throws Exception
    {
        List<Map<String, Object>> resultList = this.doSearch(request);
        return new ResponseEntity<List<Map<String, Object>>>(resultList,
            HttpStatus.OK);
    }

    private List<Map<String, Object>> doSearch(HttpServletRequest request)
        throws Exception
    {
        // ServletContext context = this.getServletContext();
        // PrintWriter out = response.getWriter();

        String keyword = request.getParameter("q");
        String type = request.getParameter("type");

        List<SearchResult> results = new ArrayList<SearchResult>();

        if (keyword == null) {
            throw new Exception("Please send me the search keyword!");
        } else if (type == null) {
            throw new Exception(
                "Please send me the search type (class or sequence)!");
        } else {

            if ( !type.equals("class") && !type.equals("sequence")) {
                throw new Exception("Type should be 'class' or 'sequence'!");
            } else {
                SearchService service;
                if (type.equals("class")) {
                    service = new SearchService(this.filesBasePath,
                        AbstractLucene.CLASS_DIAGRAMS_PATH,
                        AbstractLucene.CLASS_INDEX_PATH);
                } else {
                    service = new SearchService(this.filesBasePath,
                        AbstractLucene.SEQUENCE_DIAGRAMS_PATH,
                        AbstractLucene.SEQUENCE_INDEX_PATH);
                }

                results = service.findDiagramByContent(keyword);
            }
        }

        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

        for (SearchResult searchResult : results) {
            Map<String, Object> resultInfo = new HashMap<String, Object>();
            resultInfo.put("name", searchResult.getDiagram().getName());
            resultInfo.put("score", searchResult.getScore());
            items.add(resultInfo);
        }

        return items;
    }
}
