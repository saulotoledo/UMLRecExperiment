package br.com.ufcg.splab.recsys.umlexperiment.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import br.com.ufcg.splab.recsys.umlexperiment.web.step.StepManager;

@Component
public class StepInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response, Object handler) throws Exception
    {
        System.out.println("inntercept");
        String currentURI = request.getRequestURI().toString();

        System.out.println("currentURI: " + currentURI);
        if ( !currentURI.equals("/error") && !currentURI.equals("/clear")
            && !currentURI.equals("/testAllCombinations")
            && !currentURI.startsWith("/prepare")
            && !currentURI.startsWith("/bow")
            && !currentURI.startsWith("/diagram")) {
            HttpSession session = request.getSession();
            Integer currentPage = 1; // <- Intro page
            if (session.getAttribute(
                StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY) != null) {
                currentPage = (int) session.getAttribute(
                    StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY);
            }

            System.out.println("currentPage: " + currentPage);
            System.out.println("currentURI:" + currentURI);

            if (currentPage == 1 && !currentURI.equals("/")
                && !currentURI.equals("/1")) {

                System.out.println("Redirecting to /1");

                response.sendRedirect("/1");
                return false;
            } else {
                if (currentPage != 1 && !currentURI.equals("/" + currentPage)) {
                    if (currentPage > 4) {
                        session.setAttribute(
                            StepManager.CURRENT_EXPERIMENT_PAGE_SESSION_KEY, 1);
                        currentPage = 1;
                    }
                    System.out.println("Redirecting to /" + currentPage);

                    response.sendRedirect("/" + currentPage);
                    return false;
                }
            }
        }

        return true;
    }
}
