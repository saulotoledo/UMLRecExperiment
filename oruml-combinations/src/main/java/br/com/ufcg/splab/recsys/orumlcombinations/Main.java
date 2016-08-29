package br.com.ufcg.splab.recsys.orumlcombinations;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        Option maxHeight = Option.builder("maxHeight").argName("value")
                .hasArg().desc("max covered height at ontology's tree")
                .required().build();
        Option ontologyFile = Option.builder("ontology").argName("file")
                .hasArg().desc("ontology file path").required().build();
        Option diagramsType = Option.builder("diagType").argName("type")
                .hasArg().desc("diagrams' type (class or sequence)").required()
                .build();
        Option diagramsPath = Option.builder("diagrams").argName("path")
                .hasArg().desc("path to valid UML diagrams' files").required()
                .build();
        Option mappingsFile = Option.builder("mappings").argName("path")
                .hasArg().desc("path to the mappings file").required().build();
        Option selectionsFile = Option.builder("selections").argName("path")
                .hasArg().desc("path to the mappings file").required().build();
        Option outputFile = Option.builder("output").argName("path").hasArg()
                .desc("path to the output file").required().build();

        Options options = new Options();

        options.addOption(ontologyFile);
        options.addOption(diagramsType);
        options.addOption(diagramsPath);
        options.addOption(maxHeight);
        options.addOption(mappingsFile);
        options.addOption(selectionsFile);
        options.addOption(outputFile);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("ontology")) {
                if (!(new File(cmd.getOptionValue("ontology")).exists())) {
                    System.out
                            .println("Please inform a valid ontology file name.");
                    System.exit(1);
                }

                if (!cmd.getOptionValue("diagType").equals("class")
                        && !cmd.getOptionValue("diagType").equals("sequence")) {
                    System.out
                            .println("Please inform a valid diagram type (class or sequence).");
                    System.exit(1);
                }

                File path = new File(cmd.getOptionValue("diagrams"));
                if (!path.exists() || !path.isDirectory()) {
                    System.out.println("Please inform a valid diagrams' path.");
                    System.exit(1);
                }

                if (!(new File(cmd.getOptionValue("mappings")).exists())) {
                    System.out
                            .println("Please inform a valid mappings file name.");
                    System.exit(1);
                }

                if (!(new File(cmd.getOptionValue("selections")).exists())) {
                    System.out
                            .println("Please inform a valid selections file name.");
                    System.exit(1);
                }

                Integer maxDepthValue = 0;
                Boolean validMaxDepth = false;
                try {
                    maxDepthValue = Integer.valueOf(cmd
                            .getOptionValue("maxHeight"));
                    if (maxDepthValue <= 0) {
                        validMaxDepth = false;
                    } else {
                        validMaxDepth = true;
                    }
                } catch (NumberFormatException e) {
                    validMaxDepth = false;
                }

                if (!validMaxDepth) {
                    System.out
                            .println("The max depth must be a positive integer number.");
                    System.exit(1);
                }

                new ORUMLCombinations(cmd.getOptionValue("ontology"),
                        cmd.getOptionValue("diagType"),
                        cmd.getOptionValue("diagrams"), maxDepthValue,
                        cmd.getOptionValue("mappings"),
                        cmd.getOptionValue("selections"),
                        cmd.getOptionValue("output"));
            } else {
                usage(options);
            }

        } catch (MissingOptionException e) {
            System.out.println("Missing options.");
            usage(options);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Invalid option: " + e.getOption());
            usage(options);
        } catch (ParseException e) {
            LOGGER.error("Parsing failed.", e);
        } catch (IOException e) {
            LOGGER.error("An IO exception occurred.", e);
        } catch (Exception e) {
            LOGGER.error("An exception occurred.", e);
        }
    }

    private static void usage(Options options)
    {
        String header = "";
        String footer = "";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar <this-filename.jar>", header, options,
                footer, true);
    }
}
