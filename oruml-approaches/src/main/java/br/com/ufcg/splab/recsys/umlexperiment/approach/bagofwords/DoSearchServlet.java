package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

/*
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
*/
//@WebServlet(name = "doSearch", urlPatterns = { "/doSearch" })
public class DoSearchServlet //extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    /*
    @Override
    protected void service(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        ServletContext context = this.getServletContext();
        PrintWriter out = response.getWriter();

        String keyword = request.getParameter("q");
        String type = request.getParameter("type");

        if (keyword == null) {
            response.setContentType("text/plain");
            out.println("Please send me the search keyword!");
        } else if (type == null) {
            response.setContentType("text/plain");
            out.println("Please send me the search type (class or sequence)!");
        } else {

            if ( !type.equals("class") && !type.equals("sequence")) {
                response.setContentType("text/plain");
                out.println("Type should be 'class' or 'sequence'!");
            } else {

                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<root>");

                List<SearchResult> results = new ArrayList<SearchResult>();

                SearchService service;
                if (type.equals("class")) {
                    service = new SearchService(context,
                        AbstractLucene.CLASS_DIAGRAMS_PATH,
                        AbstractLucene.CLASS_INDEX_PATH);
                } else {
                    service = new SearchService(context,
                        AbstractLucene.SEQUENCE_DIAGRAMS_PATH,
                        AbstractLucene.SEQUENCE_INDEX_PATH);
                }

                results = service.findDiagramByContent(keyword);

                for (SearchResult result : results) {
                    out.println("<diagram>");
                    out.println("   <score>" + result.getScore() + "</score>");
                    out.println("   <filename>"
                        + result.getDiagram().getFileName() + "</filename>");
                    out.println("</diagram>");
                }

                out.println("</root>");
            }
        }

    }
/**/
}
