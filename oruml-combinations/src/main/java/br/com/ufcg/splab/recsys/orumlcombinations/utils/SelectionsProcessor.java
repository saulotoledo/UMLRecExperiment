package br.com.ufcg.splab.recsys.orumlcombinations.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectionsProcessor
{
    private List<List<String>> selectionsInfo;

    public SelectionsProcessor(String selectionsFilepath) throws IOException
    {
        this.selectionsInfo = new LinkedList<List<String>>();

        List<String> lines = Files.readAllLines(Paths.get(selectionsFilepath),
                Charset.defaultCharset());

        for (String line : lines) {
            if (line.length() > 0) {
                List<String> lineContent = Arrays.asList(line
                        .replaceAll(",( )*", ",").replaceAll("\"", "")
                        .split(","));
                this.selectionsInfo.add(lineContent);
            }
        }
    }

    public List<List<String>> getSelectionsInfo()
    {
        return this.selectionsInfo;
    }
}
