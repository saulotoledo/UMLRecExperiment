package br.com.ufcg.splab.recsys.umlexperiment.approach.bagofwords;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class DocumentIndexer extends AbstractLucene
{

    public DocumentIndexer(String contextPath, String diagramsPath,
        String indexPath)
    {
        super(contextPath, diagramsPath, indexPath);

        File tmpdir = new File(this.contextPath + "/" + this.indexPath);

        File[] files = tmpdir.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if ( !f.isDirectory()) {
                    f.delete();
                }
            }
        }
    }

    public void addDocument(String fileStringPath) throws IOException
    {
        UmlDiagram diagram = this.loadUmlDiagram(new File(fileStringPath));
        this.index(diagram);
    }

    private void index(UmlDiagram diagram)
    {
        StandardAnalyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
        IndexWriterConfig indexConfig = new IndexWriterConfig(LUCENE_VERSION,
            analyzer);

        // Informa que o index deve ser recriado, se já existir:
        // indexConfig.setOpenMode(OpenMode.CREATE);

        IndexWriter w = null;

        FSDirectory dir;
        try {
            dir = FSDirectory.open(new File(this.contextPath + "/" + this.indexPath));
            w = new IndexWriter(dir, indexConfig);
            w.addDocument(this.createDocument(diagram));
            w.close();

        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document createDocument(UmlDiagram diagram)
    {
        Document doc = new Document();

        doc.add(new Field(UmlDiagram.FIELD_NAME, diagram.getName(), Store.YES,
            Index.NOT_ANALYZED_NO_NORMS));
        doc.add(new Field(UmlDiagram.FIELD_FILENAME, diagram.getFileName(),
            Store.YES, Index.NOT_ANALYZED_NO_NORMS));
        doc.add(new Field(UmlDiagram.FIELD_CONTENT, diagram.getContent(),
            Store.NO, Index.ANALYZED));

        return doc;
    }
}
