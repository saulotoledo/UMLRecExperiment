package br.com.ufcg.splab.recsys.umlexperiment.web.step;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

public class Step03 extends Step
{
    @Override
    public Integer getOrder()
    {
        return 3;
    }

    @Override
    protected void proccess() throws Exception
    {
        this.disableStepAdvance();

        String[] choices = (String[]) this.requestData.get("choices");
        String[] approachNames = (String[]) this.requestData
            .get("approachNames");
        Integer recommenderItemsQuantity = (Integer) this.requestData
            .get("recommenderItemsQuantity");
        Integer currentApproachIndex = (Integer) this.requestData
            .get("currentApproachIndex");
        Map<String, LinkedList<Integer>> approachesResult = (Map<String, LinkedList<Integer>>) this.requestData
            .get("approachesResult");

        if (currentApproachIndex == approachNames.length) {
            this.enableStepAdvance();
        } else {

            List<Integer> choicesList = null;
            String currentApproachName = approachNames[currentApproachIndex];
            this.experimentInstance.setCurrentApproachName(currentApproachName);

            if (choices != null) {
                choicesList = new ArrayList<Integer>();
                for (String choice : choices) {
                    choicesList.add(Integer.valueOf(choice));
                }
            }

            for (String key : approachesResult.keySet()) {
                // OntoRec is KnowledgeBased:
                String methodName = "set"
                    + key.replace("OntoRec", "KnowledgeBased");

                if (choicesList != null) {
                    if (key.equals(currentApproachName)) {
                        this.updateExperimentInstance(methodName + "Selections",
                            choicesList);
                    }
                }

                this.updateExperimentInstance(methodName + "FullList",
                    approachesResult.get(key));

                this.updateExperimentInstance(methodName + "TopN", this.getTopN(
                    approachesResult.get(key), recommenderItemsQuantity));
            }
        }
    }

    private LinkedList<Integer> getTopN(LinkedList<Integer> fullList, Integer n)
    {
        LinkedList<Integer> result = new LinkedList<Integer>();
        Iterator<Integer> it = fullList.iterator();
        for (int i = 0; i < n; i++) {
            if (it.hasNext()) {
                Integer nextItem = it.next();
                result.add(nextItem);
            }
        }
        return result;
    }

    private void updateExperimentInstance(String methodName,
        List<Integer> values) throws NoSuchMethodException, Exception
    {
        JSONArray jsonResult = new JSONArray();
        for (Integer value : values) {
            jsonResult.put(value);
        }

        try {
            Method method = this.experimentInstance.getClass()
                .getMethod(methodName, String.class);
            method.invoke(this.experimentInstance, jsonResult.toString());
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            throw e;
        } catch (IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            throw e;
        }
    }
}
