package com.agrohackathon.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class Pdfextract {

PDFParser parser;
String parsedText;
PDFTextStripper pdfStripper;
PDDocument pdDoc;
COSDocument cosDoc;
PDDocumentInformation pdDocInfo;
   
	public static String getPdf(String url) {
	  PDFParser parser;
	  String parsedText="";
	  PDFTextStripper pdfStripper;
	  PDDocument pdDoc = null;
	  COSDocument cosDoc = null;
	  PDDocumentInformation pdDocInfo;
	  
	  try {
	            parser = new PDFParser(new RandomAccessBuffer(new URL(url).openStream()));
	        } catch (Exception e) {
	            System.out.println("Unable to download PDF document.");
	            return null;
	        }
	        
	        try {
	            parser.parse();
	            cosDoc = parser.getDocument();
	            pdfStripper = new PDFTextStripper();
	            pdDoc = new PDDocument(cosDoc);
	            
	
	            parsedText = pdfStripper.getText(pdDoc);
	            pdDoc.close();
	        } catch (Exception e) {
	            System.out.println("An exception occured in parsing the PDF Document.");
	            e.printStackTrace();
	            try {
	                   if (cosDoc != null) cosDoc.close();
	                   if (pdDoc != null) pdDoc.close();
	               } catch (Exception e1) {
	               e.printStackTrace();
	            }
	            
	            parsedText=toTextiText(url);
	            
	            return parsedText;
	        }      
	        return parsedText;
	}
	
	public static void download_save(String url, String filename)
	{
		try
		{
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+
					System.getProperty("file.separator")
				+"temp"+System.getProperty("file.separator")+filename);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}
	}



	
	public static String toTextiText(String url)
    {
    	try { 
    		
    		File file = new File(System.getProperty("user.dir")+
    				System.getProperty("file.separator")+"temp");
		    String identifier = "";
			file.mkdirs();
			
			String filename="pdf"+(System.currentTimeMillis()/1000)+".pdf";
			
			download_save(url, filename);

			filename=System.getProperty("user.dir")+
					System.getProperty("file.separator")
				+"temp"+System.getProperty("file.separator")+filename;
    		
    		//Create PdfReader instance. 
    		PdfReader pdfReader = new PdfReader(filename);	  
    		
    		String content="";
    		
    		//Get the number of pages in pdf. 
    		int pages = pdfReader.getNumberOfPages();   
    		//Iterate the pdf through pages. 
    		for(int i=1; i<=pages; i++) 
    		{ 
    			//Extract the page content using PdfTextExtractor. 
    			String pageContent = PdfTextExtractor.getTextFromPage(pdfReader, i);   
    			//Print the page content on console. 
    			content+=pageContent;
    			//System.out.println("Content on Page " + i + ": " + pageContent);
    		}   
    		//Close the PdfReader. 
    		pdfReader.close(); 
    		return content;
    		} 
    	catch (Exception e) 
    	{ 
    		e.printStackTrace(); 
    		return "error";
    	}
    	
    }
    

	
}


