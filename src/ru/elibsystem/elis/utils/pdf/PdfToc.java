package ru.elibsystem.elis.utils.pdf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * Extract PdfToc (ToC) from a pdf document in file or standart output.
 *
 * Usage: java -jar PdfToc.jar -i &lt;input-pdf&gt; [&lt;input-pdf&gt;]
 *
 * @author Arsen I. Borovinskiy
 * 
 */
public class PdfToc
{
  
    public PDDocument document = null;
    
    public String pdfPath;
    
    public File outFile;
    
    private FileWriter outFileWriter;
    
    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document or io with file.
     */
    public static void main( String[] args ) throws Exception
    {
        if( args.length < 2 )
        {
            usage();
        }
        else
        {
            PdfToc pdfToc = new PdfToc(); 
            pdfToc.execute(args);
        }
    }

    protected void execute(String[] args) throws IOException {

        for (int i=0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                i++;
                pdfPath = args[i];
            }
        }

        if (args.length > 2) {
            String outPath = args[args.length-1]; // last symbol is out path
            outFile = new File(outPath);
            outFileWriter = new FileWriter(outFile);
        }
        
        try {
            File file = new File(pdfPath);
            
            //System.out.println(file.exists() + " " + file.getPath() + " " + file.length());
            document = PDDocument.load( file );

            PDDocumentOutline outline =  document.getDocumentCatalog().getDocumentOutline();
            if( outline != null )
            {
                Integer level = 0;    // A nesting level
                printBookmark( outline, level );
                    
            }
            else
            {
                if (isOutInFile()) {
                   System.out.println( "This document does not contain any bookmarks" );
                }  
            }

        }
        catch (IOException ex) 
        {
            Logger.getLogger(PdfToc.class.getName()).log(Level.SEVERE, null, ex);
        }        
        finally 
        {
            if( document != null )
            {
                document.close();
            }
            if (outFileWriter != null && isOutInFile()) {
                outFileWriter.flush();
                outFileWriter.close();
            }
        }
        
    }
    
    /**
     * Print help.
     */
    private static void usage()
    {
        System.err.println( "Usage: java -jar PdfToc.jar -i <input-pdf> [<output-pdf>]" );
    }

    /**
     * This will print the documents bookmarks to System.out.
     *
     * @param bookmark The bookmark to print out.
     * @param level A nesting level
     *
     * @throws IOException If there is an error getting the page count.
     */
    protected void printBookmark( PDOutlineNode bookmark, Integer level ) throws IOException
    {
        PDOutlineItem current = bookmark.getFirstChild();
        while( current != null )
        {
            int pageIndex = 0;    // first page
            int pageNumber = 1;   // first page

            List<PDPage> pages = document.getDocumentCatalog().getAllPages();
            for (PDPage page : pages) {
              if (page.equals(current.findDestinationPage(document))) {
                break;    // pageNumbed finded
              }
              pageNumber++;   
              pageIndex++;
            }

            String out = pageNumber + " " + level + " " + current.getTitle();
            
            if (!isOutInFile()) {
                System.out.println( out );      
            } else {
                getOutFileWriter().write(out + System.lineSeparator());
            }
            
            printBookmark( current, level + 1 );   // reverse loop on ToC
            current = current.getNextSibling();
        }
    }
    
    /**
     * 
     * @return Boolean must we out in file or not
     */
    protected boolean isOutInFile() {
        return outFile != null;
    }

    /**
     * @return the outFileWriter
     */
    public FileWriter getOutFileWriter() {
        return outFileWriter;
    }

    /**
     * @param outFileWriter the outFileWriter to set
     */
    public void setOutFileWriter(FileWriter outFileWriter) {
        this.outFileWriter = outFileWriter;
    }
}
