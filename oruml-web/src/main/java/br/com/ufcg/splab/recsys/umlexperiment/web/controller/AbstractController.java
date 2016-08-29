package br.com.ufcg.splab.recsys.umlexperiment.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.ufcg.splab.recsys.umlexperiment.web.config.ClassConfigInfo;
import br.com.ufcg.splab.recsys.umlexperiment.web.config.SequenceConfigInfo;

public abstract class AbstractController
{
    @Autowired
    protected ClassConfigInfo classConfigInfo;

    @Autowired
    protected SequenceConfigInfo sequenceConfigInfo;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleError(
        HttpServletRequest request, Exception exception)
    {
        Map<String, Object> infoMap = new HashMap<String, Object>();

        String exceptionName = exception.getClass().getSimpleName();
        infoMap.put("exception", exceptionName);
        // infoMap.put("exceptionTrace", exception.getStackTrace());
        infoMap.put("message", exception.getMessage());
        infoMap.put("url", request.getRequestURL());

        exception.printStackTrace();

        if (exceptionName.equals("MissingServletRequestParameterException")) {
            infoMap.put("param",
                ((MissingServletRequestParameterException) exception)
                    .getParameterName());
        }

        return new ResponseEntity<Map<String, Object>>(infoMap,
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
