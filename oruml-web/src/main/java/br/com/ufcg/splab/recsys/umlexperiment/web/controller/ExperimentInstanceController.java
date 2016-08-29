package br.com.ufcg.splab.recsys.umlexperiment.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.ufcg.splab.recsys.recommender.SimilarityMapper;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ExperimentInstance;
import br.com.ufcg.splab.recsys.umlexperiment.web.entity.ItemProfile;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ExperimentInstanceRepository;
import br.com.ufcg.splab.recsys.umlexperiment.web.repository.ItemProfileRepository;
import br.com.ufcg.splab.recsys.umlexperiment.web.step.StepManager;

@Controller
@Scope(scopeName = "request")
@RequestMapping("/")
public class ExperimentInstanceController extends AbstractController
{
    @Value("${system.diagrams-path}")
    private String filesBasePath;

    @Value("${recommender.items.quantity}")
    private Integer recommenderItemsQuantity;

    @Value("${system.experiments}")
    private String[] experiments;

    @Value("${finalFormUrl}")
    private String finalFormUrl;

    @Autowired
    private ItemProfileRepository itemProfileRepository;

    @Autowired
    private ExperimentInstanceRepository experimentInstanceRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    // Fisher–Yates shuffle
    private void shuffleArray(String[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    private Map<Integer, String> getRecommendedDiagramsByIds(String diagramType,
        List<Integer> ids)
    {
        List<ItemProfile> itemProfiles = this.itemProfileRepository
            .findByIdIn(ids);
        Map<Integer, String> result = new HashMap<Integer, String>();

        for (ItemProfile itemProfile : itemProfiles) {
            result.put(itemProfile.getId().intValue(),
                "images/" + diagramType + "_diagrams/"
                    + itemProfile.getName().replace("uml", "png")
                        .replace("mdxml", "png").replace("xmi", "png")
                        .replace("xml", "png"));
        }

        return result;
    }

    private void clearSession(HttpSession session)
    {
        Enumeration<String> sessionAttrs = session.getAttributeNames();

        while (sessionAttrs.hasMoreElements()) {
            String attrName = sessionAttrs.nextElement();
            session.removeAttribute(attrName);
        }
    }

    // see
    // http://stackoverflow.com/questions/5162254/all-possible-combinations-of-an-array
    private <T> List<List<T>> combination(List<T> values, int size)
    {

        if (0 == size) {
            return Collections.singletonList(Collections.<T> emptyList());
        }

        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> combination = new LinkedList<List<T>>();

        T actual = values.iterator().next();

        List<T> subSet = new LinkedList<T>(values);
        subSet.remove(actual);

        List<List<T>> subSetCombination = this.combination(subSet, size - 1);

        for (List<T> set : subSetCombination) {
            List<T> newSet = new LinkedList<T>(set);
            newSet.add(0, actual);
            combination.add(newSet);
        }

        combination.addAll(this.combination(subSet, size));

        return combination;
    }

    @RequestMapping("/testAllCombinations")
    @ResponseBody
    public String testAllClassCombinations(HttpServletRequest request)
    {
        /*
         * String[] options = { "PSA", "PADV", "PTC", "PSO", "PSHA", "POQ",
         * "POP", "POI", "POG", "POD", "PNAA", "PGS", "PEN", "PDA", "PCOA",
         * "PASC", "PAO", "PABC", "PRI" };
         */

        String[] options = { "POI", "PABC", "POG", "POD", "PSA", "PEN", "PASC",
            "PRI", "PAO", "PCOA" };

        List<List<String>> combinations = new LinkedList<List<String>>();

        for (int i = 1; i <= options.length; i++) {
            combinations.addAll(this.combination(Arrays.asList(options), i));
        }

        for (List<String> list : combinations) {

            HttpSession session = request.getSession();
            this.updateExperimentsOrder(session);

            Map<String, Object> requestData = new HashMap<String, Object>();
            requestData.put("userName", "AUTOMATIC_10");
            requestData.put("userEmail", "AUTOMATIC_10@AUTOMATIC_10");
            requestData.put("userDegree", "AUTOMATIC_10");
            requestData.put("hasProfessionalExperience", false);
            requestData.put("startExperiment", this.experiments[0]);
            requestData.put("currentExperiment", this.experiments[0]);

            try {
                this.initStepManager(session, requestData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] userprofile = list.toArray(new String[list.size()]);

            requestData = new HashMap<String, Object>();
            requestData.put("userprofile", userprofile);

            StepManager sm;
            try {
                sm = this.initStepManager(session, requestData);
                ExperimentInstance ei = sm.getExperimentInstance();

                // TODO: checar valores de similaridade para SimilarityMapper
                // TODO: checar userProfile
                Map<String, LinkedList<Integer>> approachesResultToSession = new HashMap<String, LinkedList<Integer>>();
                Map<String, List<SimilarityMapper>> approachesResult = ei
                    .getApproachesResult();
                for (String approachName : approachesResult.keySet()) {
                    LinkedList<Integer> items = new LinkedList<Integer>();
                    for (SimilarityMapper similarityMapper : approachesResult
                        .get(approachName)) {
                        items.add(similarityMapper.getProfileId());
                    }
                    approachesResultToSession.put(approachName, items);
                }

                Set<String> approachNamesSet = approachesResultToSession
                    .keySet();
                String[] approachNames = approachNamesSet
                    .toArray(new String[approachNamesSet.size()]);
                this.shuffleArray(approachNames);
                session.setAttribute("userprofile", ei.getUserProfile());
                session.setAttribute("approachesResult",
                    approachesResultToSession);
                session.setAttribute("approachNames", approachNames);
                session.setAttribute("currentApproachIndex", 0);

                session = request.getSession();
                this.updateExperimentsOrder(session);

                approachNames = (String[]) session
                    .getAttribute("approachNames");
                Integer currentApproachIndex = (Integer) session
                    .getAttribute("currentApproachIndex");

                Map<String, LinkedList<Integer>> approachesResult2 = (Map<String, LinkedList<Integer>>) session
                    .getAttribute("approachesResult");

                requestData = new HashMap<String, Object>();
                requestData.put("recommenderItemsQuantity",
                    this.recommenderItemsQuantity);
                requestData.put("approachNames", approachNames);
                requestData.put("approachesResult", approachesResult2);
                requestData.put("currentApproachIndex", currentApproachIndex);

                sm = this.initStepManager(session, requestData);

            } catch (Exception e) {
                e.printStackTrace();
            }

            this.clearSession(request.getSession());
        }

        return "Done";
    }

    @RequestMapping("/clear")
    public String clear(HttpServletRequest request)
    {
        this.clearSession(request.getSession());
        return "redirect:/";
    }

    @RequestMapping("/")
    public String start(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        return "redirect:/1";
    }

    private void updateExperimentsOrder(HttpSession session)
    {
        if (session.getAttribute("experiments") == null) {
            this.shuffleArray(this.experiments);
            session.setAttribute("experiments", this.experiments);
            session.setAttribute("experimentIndex", 0);
        } else {
            this.experiments = (String[]) session.getAttribute("experiments");
        }
    }

    @RequestMapping("1")
    public String stepOne(HttpServletRequest request,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "degree", required = false) String degree,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "hasProfessionalExperience", required = false) Boolean hasProfessionalExperience,
        @RequestParam(value = "acceptAgreement", required = false) Boolean acceptAgreement,
        Model model) throws Exception
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        Integer experimentIndex = (Integer) session
            .getAttribute("experimentIndex");

        if (experimentIndex == null) {
            return "redirect:/";
        }

        // If the user is restarting the experiment, reinitialize his data:
        if (name == null) {
            name = (String) session.getAttribute("user.name");
            email = (String) session.getAttribute("user.email");
            degree = (String) session.getAttribute("user.degree");

            if (name != null && degree != null && email != null) {
                // If the user is here, he has already accepted the agreement
                // terms:
                acceptAgreement = true;
                hasProfessionalExperience = (Boolean) session
                    .getAttribute("user.hasProfessionalExperience");

                // These attributes are not needed anymore:
                session.removeAttribute("user.name");
                session.removeAttribute("user.email");
                session.removeAttribute("user.degree");
                session.removeAttribute("user.hasProfessionalExperience");
            }
        }

        if (name != null && degree != null && email != null) {
            if ( !acceptAgreement) {
                throw new Exception(
                    "You must accept the agreement terms to continue.");
            }

            if (hasProfessionalExperience == null) {
                hasProfessionalExperience = false;
            }

            Map<String, Object> requestData = new HashMap<String, Object>();
            requestData.put("userName", name);
            requestData.put("userEmail", email);
            requestData.put("userDegree", degree);
            requestData.put("hasProfessionalExperience",
                hasProfessionalExperience);
            requestData.put("startExperiment", this.experiments[0]);
            requestData.put("currentExperiment",
                this.experiments[experimentIndex]);

            this.initStepManager(session, requestData);

            return "redirect:/2";
        }

        return "steps/step01";
    }

    private StepManager initStepManager(HttpSession session,
        Map<String, Object> requestData) throws Exception
    {
        return new StepManager(session, requestData,
            this.experimentInstanceRepository, this.itemProfileRepository,
            this.classConfigInfo, this.sequenceConfigInfo, this.filesBasePath);
    }

    @RequestMapping("2")
    public String stepTwo(HttpServletRequest request,
        @RequestParam(value = "userprofile", required = false) String[] userprofile,
        Model model) throws Exception
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        if (userprofile != null) {
            Map<String, Object> requestData = new HashMap<String, Object>();
            requestData.put("userprofile", userprofile);

            StepManager sm = this.initStepManager(session, requestData);
            ExperimentInstance ei = sm.getExperimentInstance();

            // TODO: checar valores de similaridade para SimilarityMapper
            // TODO: checar userProfile
            Map<String, LinkedList<Integer>> approachesResultToSession = new HashMap<String, LinkedList<Integer>>();
            Map<String, List<SimilarityMapper>> approachesResult = ei
                .getApproachesResult();
            for (String approachName : approachesResult.keySet()) {
                LinkedList<Integer> items = new LinkedList<Integer>();
                for (SimilarityMapper similarityMapper : approachesResult
                    .get(approachName)) {
                    items.add(similarityMapper.getProfileId());
                }
                approachesResultToSession.put(approachName, items);
            }

            Set<String> approachNamesSet = approachesResultToSession.keySet();
            String[] approachNames = approachNamesSet
                .toArray(new String[approachNamesSet.size()]);
            this.shuffleArray(approachNames);
            session.setAttribute("userprofile", ei.getUserProfile());
            session.setAttribute("approachesResult", approachesResultToSession);
            session.setAttribute("approachNames", approachNames);
            session.setAttribute("currentApproachIndex", 0);
            /*
             * session.setAttribute("approachChoices", new HashMap<String,
             * List<Integer>>());
             */

            return "redirect:/3";
        }

        Integer experimentIndex = (Integer) session
            .getAttribute("experimentIndex");
        model.addAttribute("diagramType", this.experiments[experimentIndex]);

        return "steps/step02";
    }

    @RequestMapping(value = "diagram", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getDiagram(HttpServletRequest request,
        @RequestParam(value = "id", required = true) Integer imageId)
        throws IOException
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        ItemProfile itemProfile = this.itemProfileRepository.findOne(imageId);
        Integer experimentIndex = (Integer) session
            .getAttribute("experimentIndex");

        // Retrieve image from the classpath.
        String imageFileExtension = "png";
        String diagramFilePath = "oruml-web/src/main/resources/static/images/"
            + this.experiments[experimentIndex] + "_diagrams/"
            + itemProfile.getName().replace(".xml", "." + imageFileExtension)
                .replace(".mdxml", "." + imageFileExtension)
                .replace(".xmi", "." + imageFileExtension)
                .replace(".uml", "." + imageFileExtension);

        // Resource resource = this.resourceLoader.getResource(diagramFilePath);

        InputStream is = new FileInputStream(new File(diagramFilePath)); // resource.getInputStream();

        // Prepare buffered image.
        BufferedImage img = ImageIO.read(is);

        // Create a byte array output stream.
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        // Write to output stream
        ImageIO.write(img, imageFileExtension, bao);

        return bao.toByteArray();
    }

    @RequestMapping("3")
    public String stepThree(HttpServletRequest request,
        @RequestParam Map<String, String> allRequestParams, Model model)
        throws Exception
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        String[] approachNames = (String[]) session
            .getAttribute("approachNames");
        Integer currentApproachIndex = (Integer) session
            .getAttribute("currentApproachIndex");

        Map<String, LinkedList<Integer>> approachesResult = (Map<String, LinkedList<Integer>>) session
            .getAttribute("approachesResult");

        Map<String, Object> requestData = new HashMap<String, Object>();
        requestData.put("recommenderItemsQuantity",
            this.recommenderItemsQuantity);
        requestData.put("approachNames", approachNames);
        requestData.put("approachesResult", approachesResult);
        requestData.put("currentApproachIndex", currentApproachIndex);

        StepManager sm = null;
        // allRequestParams contains the submit button: we need to check
        // keySet().size > 1:
        if (allRequestParams != null && !allRequestParams.isEmpty()
            && allRequestParams.keySet().size() > 1) {
            List<String> choices = new LinkedList<String>();
            for (String key : allRequestParams.keySet()) {
                if (allRequestParams.get(key).equals("1")) {
                    choices.add(key);
                }
            }
            requestData.put("choices",
                choices.toArray(new String[choices.size()]));

            try {
                System.out.println(currentApproachIndex + ": "
                    + approachNames[currentApproachIndex]);
                System.out.println(choices);
            } catch (Exception e) {

            }

            sm = this.initStepManager(session, requestData);
            session.setAttribute("currentApproachIndex",
                currentApproachIndex + 1);

            // Prevents that reload at user's browser send again the previous
            // selected diagrams to this controller action. The HTTP interceptor
            // will redirect the user to the next step when needed:
            return "redirect:/3";
        } else {
            sm = this.initStepManager(session, requestData);
        }

        Integer experimentIndex = (Integer) session
            .getAttribute("experimentIndex");
        ExperimentInstance ei = sm.getExperimentInstance();

        model.addAttribute("currentApproachIndex", currentApproachIndex);
        model.addAttribute("diagramType", this.experiments[experimentIndex]);
        model.addAttribute("userSelections", ei.getUserProfileSelections());
        model.addAttribute("currentTopN", ei.getCurrentTopN());

        return "steps/step03";
    }

    @RequestMapping("/4")
    public String finish(HttpServletRequest request, Model model)
        throws Exception
    {
        HttpSession session = request.getSession();
        this.updateExperimentsOrder(session);

        Map<String, Object> requestData = new HashMap<String, Object>();
        StepManager sm = this.initStepManager(session, requestData);
        ExperimentInstance ei = sm.getExperimentInstance();

        Integer experimentIndex = (Integer) session
            .getAttribute("experimentIndex");

        if (experimentIndex != this.experiments.length - 1) {
            this.clearSession(session);
            experimentIndex++;

            session.setAttribute("experiments", this.experiments);
            session.setAttribute("experimentIndex", experimentIndex);

            session.setAttribute("user.name", ei.getUserName());
            session.setAttribute("user.email", ei.getUserEmail());
            session.setAttribute("user.degree", ei.getUserDegree());
            session.setAttribute("user.hasProfessionalExperience",
                ei.getHasProfessionalExperience());

            return "redirect:/1";
        }

        model.addAttribute("experimentId", String.format("%03d", ei.getId()));
        model.addAttribute("finalFormUrl", this.finalFormUrl);
        this.clearSession(session);

        return "steps/step04";
    }

}
