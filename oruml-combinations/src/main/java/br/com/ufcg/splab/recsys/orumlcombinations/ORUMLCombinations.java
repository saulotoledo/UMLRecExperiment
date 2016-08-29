package br.com.ufcg.splab.recsys.orumlcombinations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.ontorec.recommender.OntoRecApproach;
import br.com.ufcg.splab.recsys.ontorec.weighting.AbstractNodeWeightingApproach;
import br.com.ufcg.splab.recsys.ontorec.weighting.BFSPathNodeWeightingApproach;
import br.com.ufcg.splab.recsys.ontorec.weighting.KnthAncestorNodeWeightingApproach;
import br.com.ufcg.splab.recsys.orumlcombinations.utils.FileMappingsProcessor;
import br.com.ufcg.splab.recsys.orumlcombinations.utils.SelectionsProcessor;
import br.com.ufcg.splab.recsys.recommender.Recommender;
import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.umlexperiment.approach.similarity.CosineSimilarity;
import br.edu.ufcg.splab.reuml.profile.UMLProfile;
import br.edu.ufcg.splab.reuml.service.UMLProfileService;

public class ORUMLCombinations
{
    private String outputFilename;
    private String mappingsFilename;
    private String selectionsFilename;
    private Integer maxDepth;

    private String ontologyFile;
    private Map<Integer, UMLProfile> diagramsProfiles;

    private Map<Integer, String> diagramsIdentifier;

    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(ORUMLCombinations.class);

    public ORUMLCombinations(String ontologyFile, String diagramsType,
        String diagramsPath, Integer maxDepth, String mappingsFilename,
        String selectionsFilename, String outputFile) throws Exception
    {
        this.ontologyFile = ontologyFile;
        this.mappingsFilename = mappingsFilename;
        this.selectionsFilename = selectionsFilename;
        this.maxDepth = maxDepth;

        this.createOutputFile(outputFile);
        this.loadUMLDiagrams(diagramsPath, diagramsType);
        this.computeRecommendations();
    }

    private void createOutputFile(String filename) throws IOException
    {
        File outputFile = new File(filename);

        String outputDir = outputFile.getParent();

        // It will be null for the current dir:
        if (outputDir != null) {
            File outputDirHandler = new File(outputDir);

            Boolean success = true;
            if (outputDirHandler.exists() && !outputDirHandler.isDirectory()) {
                success = false;
            } else {
                if ( !outputDirHandler.exists()) {
                    success = outputDirHandler.mkdirs();
                }
            }
            if ( !success) {
                throw new IOException("Cannot create output directory.");
            }
        }

        this.outputFilename = filename;

        if ( !outputFile.exists()) {
            outputFile.createNewFile();
        } else {
            FileOutputStream out;
            out = new FileOutputStream(filename, false);
            out.getChannel().truncate(0);
            out.getChannel().force(true);
            out.close();
        }
    }

    private void appendToOutput(String line) throws IOException
    {
        BufferedWriter output = new BufferedWriter(
            new FileWriter(this.outputFilename, true));
        output.append(line + "\n");
        output.close();
    }

    private void loadUMLDiagrams(String diagramsPath, String diagramsType)
    {
        // Avoid mistakes at names:
        if ( !diagramsType.equals("sequence")) {
            diagramsType = "class";
        }

        LOGGER.debug(
            "Loading item's profiles from UML files for the following diagram type: "
                + diagramsType);

        File folder = new File(diagramsPath);
        File[] listOfFiles = folder.listFiles();

        Map<Integer, UMLProfile> umlProfiles = new HashMap<Integer, UMLProfile>();
        this.diagramsIdentifier = new HashMap<Integer, String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                LOGGER.debug("Processing file " + listOfFiles[i].getName());

                UMLProfileService umlProfileService = new UMLProfileService(
                    "br.edu.ufcg.splab.reuml.feature");

                UMLProfile profile;
                if (diagramsType.equals("sequence")) {
                    profile = umlProfileService.getSequenceDiagramProfile(
                        listOfFiles[i].getAbsolutePath());
                } else {
                    profile = umlProfileService.getClassDiagramProfile(
                        listOfFiles[i].getAbsolutePath());
                }
                this.diagramsIdentifier.put(i, listOfFiles[i].getName());

                Map<String, Double> profileDebugger = new LinkedHashMap<String, Double>();
                for (Field field : profile.getClass().getDeclaredFields()) {
                    String methodName = "get" + field.getName();

                    try {
                        Method getMethod = profile.getClass()
                            .getMethod(methodName);
                        Double value = (Double) getMethod.invoke(profile);
                        if (value > 0) {
                            value = 1d;
                        } else {
                            value = 0d;
                        }
                        Method setMethod = profile.getClass().getMethod(
                            methodName.replace("get", "set"), Double.TYPE);
                        setMethod.invoke(profile, value);
                        profileDebugger.put(field.getName(), value);
                    } catch (Exception e) {
                        LOGGER.debug(
                            "Error processing '" + field.getName() + '"', e);
                    }
                }

                LOGGER.debug(
                    "This file generated the profile " + profileDebugger);

                umlProfiles.put(i, profile);
            }
        }

        this.diagramsProfiles = umlProfiles;
    }

    private void computeRecommendations() throws Exception
    {
        List<Boolean> lambdaValues = Arrays.asList(true,
            false);
        List<Boolean> upsilonValues = Arrays.asList(true,
            false);

        List<AbstractNodeWeightingApproach<String>> approaches = new ArrayList<AbstractNodeWeightingApproach<String>>();
        approaches.add(new BFSPathNodeWeightingApproach<String>());
        approaches.add(new KnthAncestorNodeWeightingApproach<String>());

        List<Integer> kValues = new ArrayList<Integer>();
        for (int i = 1; i <= this.maxDepth; i++) {
            kValues.add(i);
        }

        String outputHeader = "'selectedFeatures','approachName','lambdaValue','upsilonValue','depth','diagrams'";
        this.appendToOutput(outputHeader);
        LOGGER.debug(outputHeader);

        FileMappingsProcessor mp = new FileMappingsProcessor(
            this.mappingsFilename);

        for (Boolean lambdaValue : lambdaValues) {
            for (Boolean upsilonValue : upsilonValues) {
                for (AbstractNodeWeightingApproach<String> approach : approaches) {

                    OntoRecApproach ora = new OntoRecApproach(this.ontologyFile,
                        approach, lambdaValue, upsilonValue, mp,
                        new CosineSimilarity());

                    for (Integer k : kValues) {
                        ora.setMaxHeight(k);
                        this.calculateRecommendations(ora,
                            approach.getClass().getSimpleName(), lambdaValue,
                            upsilonValue);
                    }
                }
            }
        }
    }

    private void calculateRecommendations(OntoRecApproach ora,
        String approachName, Boolean lambdaValue, Boolean upsilonValue)
        throws IOException, Exception
    {
        SelectionsProcessor selections = new SelectionsProcessor(
            this.selectionsFilename);

        for (List<String> selectedItems : selections.getSelectionsInfo()) {
            this.calculateForFeatureList(ora, selectedItems, approachName,
                lambdaValue, upsilonValue);
        }
    }

    private void calculateForFeatureList(OntoRecApproach ora,
        List<String> selectedItems, String approachName, Boolean lambdaValue,
        Boolean upsilonValue) throws Exception
    {

        Set<String> selectedFeatures = new HashSet<String>();
        for (String selectedItem : selectedItems) {
            selectedFeatures.add(selectedItem);
        }
        /*
         * ora.setSelectedFeatures(selectedFeatures);
         */

        Map<String, Double> tempUserProfile = new HashMap<String, Double>();
        for (String key : selectedFeatures) {
            tempUserProfile.put(key, 1d);
        }
        ora.setUserProfile(tempUserProfile);

        LOGGER.debug(
            String.format("Calculating for the following selected features: %s",
                selectedFeatures));

        List<SimilarityMapper> resultList;
        resultList = this.getTopN(5, ora);

        // TODO: files with the same name are a problem:

        String items = String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]",
            this.diagramsIdentifier.get(resultList.get(0).getProfileId()),
            this.diagramsIdentifier.get(resultList.get(1).getProfileId()),
            this.diagramsIdentifier.get(resultList.get(2).getProfileId()),
            this.diagramsIdentifier.get(resultList.get(3).getProfileId()),
            this.diagramsIdentifier.get(resultList.get(4).getProfileId()));

        String resultStr = String.format("'%s','%s','%s','%s','%s','%s'",
            selectedFeatures.toString(), approachName,
            lambdaValue.toString().toUpperCase(),
            upsilonValue.toString().toUpperCase(), ora.getMaxHeight(), items);

        LOGGER
            .debug(String.format("The recommended diagrams are %s", resultStr));

        this.appendToOutput(resultStr);
    }

    private List<SimilarityMapper> getTopN(Integer n, OntoRecApproach ora)
        throws Exception
    {
        Recommender recommender = new Recommender(ora);

        Boolean selectedProfileIsReady = false;
        Map<String, Double> userProfile = ora.getUserProfile();

        String fieldList = "[ ";
        for (int i = 0; i < this.diagramsProfiles.keySet().size(); i++) {
            UMLProfile profile = this.diagramsProfiles.get(i);
            Map<String, Double> diagramProfile = new HashMap<String, Double>();
            diagramProfile.put(this.diagramsIdentifier.get(i) + ":ID",
                (double) i);

            for (Field field : profile.getClass().getDeclaredFields()) {
                String methodName = "get" + field.getName();

                Method getMethod = profile.getClass().getMethod(methodName);
                Double value = (Double) getMethod.invoke(profile);
                diagramProfile.put(field.getName(), value);

                if ( !selectedProfileIsReady) {
                    if (fieldList.length() > 2) {
                        fieldList += ", ";
                    }
                    fieldList += field.getName();

                    if ( !userProfile.keySet().contains(field.getName())) {
                        LOGGER.debug(
                            String.format("Adding '%s' to the user's profile",
                                field.getName()));
                        userProfile.put(field.getName(), 0d);
                    }
                }
            }

            selectedProfileIsReady = true;
            recommender.addItem(diagramProfile);
        }
        fieldList += " ]";

        LOGGER.debug("We found the following fields at the diagram's class: '"
            + fieldList);
        LOGGER.debug("Our current user profile is: '" + userProfile);

        List<SimilarityMapper> result = recommender.recommendTo(userProfile, n);

        return result;
    }
}
