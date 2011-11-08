/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package com.olabini.jescov.ant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import com.olabini.jescov.*;
import com.olabini.jescov.generators.*;


public class ReportTask extends Task {
    private String format = "html";

    private Configuration c = new Configuration();

    public void setFormat(String formatName) {
        this.format = formatName;
    }

    public void setDatafile(File file) {
        c.setJsonOutputFile(file.getPath());
    }

    public void setDestfile(File file) {
        c.setXmlOutputFile(file.getPath());
    }

    public void setSrcdir(File dir) {
        c.setSourceDirectory(dir.getPath());
    }

    public void setDestdir(File dir) {
        c.setHtmlOutputDir(dir.getPath());
    }

    public void execute() throws BuildException {
        try {
            if("html".equalsIgnoreCase(format)) {
                executeHtml();
            } else if("xml".equalsIgnoreCase(format)) {
                executeXml();
            } else {
                throw new BuildException("unknown JesCov format: " + format);
            }
        } catch(IOException e) {
            throw new BuildException(e);
        }
    }

    private CoverageData read() throws IOException {
        FileReader fr = null;
        try {
            fr = new FileReader(c.getJsonOutputFile());
            return new JsonIngester().ingest(fr);
        } finally {
            if(fr != null) {
                fr.close();
            }
        }
    }

    private void executeHtml() throws IOException {
        CoverageData data = read();
        new HtmlGenerator(c).generate(data);
    }

    private void executeXml() throws IOException {
        CoverageData data = read();
        FileWriter fw = null;
        try {
            fw = new FileWriter(c.getXmlOutputFile());
            Generator g = new XmlGenerator(fw);
            g.generate(data);
        } finally {
            if(fw != null) {
                fw.close();
            }
        }
    }
}
