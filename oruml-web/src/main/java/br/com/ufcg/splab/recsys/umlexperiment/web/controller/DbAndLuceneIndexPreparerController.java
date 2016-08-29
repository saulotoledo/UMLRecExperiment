package br.com.ufcg.splab.recsys.umlexperiment.web.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.AbstractLucene;
import br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords.DocumentIndexer;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ItemProfile;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ItemProfileRepository;
import br.edu.ufcg.splab.reuml.profile.UMLProfile;
import br.edu.ufcg.splab.reuml.service.UMLProfileService;

@RestController
@RequestMapping("/prepare-dasdsdadsadsadsadadsadasdasdsadasdasdasdsadsadsadsadsadsadsadasdas")
public class DbAndLuceneIndexPreparerController extends AbstractController
{
    @Value("${system.diagrams-path}")
    private String filesBasePath;

    @Autowired
    private ItemProfileRepository itemProfileRepository;

    @RequestMapping
    public ResponseEntity<List<String>> stepOne(HttpServletRequest request)
    {
        if (this.itemProfileRepository.count() == 0) {
            this.prepareDiagramsProfiles();
        }
        List<String> resultList = this.luceneIndexer();
        return new ResponseEntity<List<String>>(resultList, HttpStatus.OK);
    }

    private void prepareDiagramsProfiles()
    {
        List<String> diagramTypes = new ArrayList<String>();
        diagramTypes.add("class");
        diagramTypes.add("sequence");

        for (String diagramType : diagramTypes) {
            File folder = new File(
                this.filesBasePath + "/" + diagramType + "_diagrams/");
            File[] listOfFiles = folder.listFiles();

            List<Map<String, Double>> umlProfiles = new ArrayList<Map<String, Double>>();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    UMLProfileService umlProfileService = new UMLProfileService(
                        "br.edu.ufcg.splab.reuml.feature");

                    UMLProfile profile;
                    if (diagramType.equals("sequence")) {
                        profile = umlProfileService.getSequenceDiagramProfile(
                            listOfFiles[i].getAbsolutePath());
                    } else {
                        profile = umlProfileService.getClassDiagramProfile(
                            listOfFiles[i].getAbsolutePath());
                    }
                    // this.diagramsIdentifier.put(i, listOfFiles[i].getName());

                    JSONObject jsonProfile = new JSONObject();

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
                            jsonProfile.put(field.getName(), value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    this.saveItemProfile(listOfFiles[i].getName(), diagramType,
                        jsonProfile);
                }
            }
        }
    }

    private void saveItemProfile(String name, String type,
        JSONObject jsonProfile)
    {
        ItemProfile itemProfile = new ItemProfile(name, type,
            jsonProfile.toString());
        itemProfile = this.itemProfileRepository.save(itemProfile);
    }

    private List<String> luceneIndexer()
    {
        List<String> resultList = new ArrayList<String>();

        // Class diagrams:

        DocumentIndexer di;
        File directory;

        di = new DocumentIndexer(this.filesBasePath,
            AbstractLucene.CLASS_DIAGRAMS_PATH,
            AbstractLucene.CLASS_INDEX_PATH);

        directory = new File(
            this.filesBasePath + "/" + AbstractLucene.CLASS_DIAGRAMS_PATH);

        for (String fileName : directory.list()) {

            String fileStringPath = directory.getAbsolutePath() + '/'
                + fileName;

            try {
                di.addDocument(fileStringPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        resultList.add("Class diagrams index written at " + this.filesBasePath
            + "/" + AbstractLucene.CLASS_INDEX_PATH);

        // Sequence diagrams:

        di = new DocumentIndexer(this.filesBasePath,
            AbstractLucene.SEQUENCE_DIAGRAMS_PATH,
            AbstractLucene.SEQUENCE_INDEX_PATH);

        directory = new File(
            this.filesBasePath + "/" + AbstractLucene.SEQUENCE_DIAGRAMS_PATH);
        for (String fileName : directory.list()) {

            String fileStringPath = directory.getAbsolutePath() + '/'
                + fileName;

            try {
                di.addDocument(fileStringPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        resultList.add("Sequence diagrams index written at "
            + this.filesBasePath + "/" + AbstractLucene.SEQUENCE_INDEX_PATH);

        return resultList;
    }
}
