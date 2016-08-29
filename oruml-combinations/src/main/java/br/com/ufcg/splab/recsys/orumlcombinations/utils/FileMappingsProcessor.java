package br.com.ufcg.splab.recsys.orumlcombinations.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeManager;
import br.com.ufcg.splab.recsys.ontorec.recommender.MappingsProcessor;

public class FileMappingsProcessor implements MappingsProcessor
{
    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileMappingsProcessor.class);

    private List<List<String>> mappingsInfo;

    public FileMappingsProcessor(String mappingsFilepath) throws IOException
    {
        this.mappingsInfo = new LinkedList<List<String>>();

        List<String> lines = Files.readAllLines(Paths.get(mappingsFilepath),
                Charset.defaultCharset());

        for (String line : lines) {
            if (line.length() > 0) {
                List<String> lineContent = Arrays.asList(line
                        .replaceAll(",( )*", ",").replaceAll("\"", "")
                        .split(","));
                this.mappingsInfo.add(lineContent);
            }
        }
    }

    public void mapAt(NodeManager<String> nm) throws Exception
    {
        for (List<String> line : this.mappingsInfo) {
            if (line.size() == 2 && line.get(0).equals("Node")) {
                nm.getNode(line.get(1));
            } else {
                Node<String> node = nm.getNode(line.get(2));

                if (line.size() == 3) {
                    if (line.get(0).equals("Attribute")) {
                        nm.createAttribute(line.get(1), node);
                    }

                    if (line.get(0).equals("FeatureForNode")) {
                        nm.addFeatureMapping(line.get(1), node);
                    }

                } else if (line.size() == 4
                        && line.get(0).equals("FeatureForAttr")) {
                    nm.addFeatureMapping(line.get(1), node,
                            node.getOwnOrInheritedAttributeByName(line.get(3)));

                } else {
                    LOGGER.error("Invalid mapping information: "
                            + line.toString());
                }
            }
        }
    }
}
