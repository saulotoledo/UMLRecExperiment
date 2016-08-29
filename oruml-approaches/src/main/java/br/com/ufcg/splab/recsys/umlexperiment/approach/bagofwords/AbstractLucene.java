package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public abstract class AbstractLucene
{
    protected Analyzer analyzer;

    protected Directory dir;

    protected String contextPath;

    protected String diagramsPath;
    protected String indexPath;

    // TODO: These relative paths are strange, fix it!
    public static final String CLASS_INDEX_PATH = "/../lucene_index/class_index/";
    public static final String CLASS_DIAGRAMS_PATH = "/../uml_diagrams/class_diagrams/";
    public static final String SEQUENCE_INDEX_PATH = "/../lucene_index/sequence_index/";
    public static final String SEQUENCE_DIAGRAMS_PATH = "/../uml_diagrams/sequence_diagrams/";
    public static final Version LUCENE_VERSION = Version.LUCENE_48;
    public static final int MAX_HITS = 30;

    public AbstractLucene(String contextPath, String diagramsPath,
        String indexPath)
    {
        this.analyzer = new StandardAnalyzer(LUCENE_VERSION);
        this.contextPath = contextPath;

        this.diagramsPath = diagramsPath;
        this.indexPath = indexPath;

        try {
            this.dir = FSDirectory
                .open(new File(this.contextPath + "/" + this.indexPath));
        } catch (IOException e) {
            throw new RuntimeException("Error opening the indexing directory",
                e);
        }
    }

    public UmlDiagram loadUmlDiagram(File file) throws IOException
    {
        FileInputStream fr = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(fr, "UTF-8"));

        StringBuilder builder = new StringBuilder();
        String s = null;

        UmlDiagram diagram = new UmlDiagram();
        diagram.setName(file.getName()).setFileName(file.getName());

        String newLine = System.getProperty("line.separator");
        while ( (s = reader.readLine()) != null) {
            builder.append(s).append(newLine);
        }

        reader.close();
        fr.close();
        diagram.setContent(builder.toString());

        return diagram;
    }
}
