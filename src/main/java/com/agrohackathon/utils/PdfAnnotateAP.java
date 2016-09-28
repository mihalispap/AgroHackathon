package com.agrohackathon.utils;

//pdfbox
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.io.RandomAccessBuffer;

//Std java library
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.Map;
import java.util.HashMap;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

public class PdfAnnotateAP {

PDFParser parser;
String parsedText;
PDFTextStripper pdfStripper;
PDDocument pdDoc;
COSDocument cosDoc;
PDDocumentInformation pdDocInfo;

static String AGROPORTAL = "http://services.stageportal.lirmm.fr/annotator/" ;
static String API_KEY = "261efcc5-b16b-4460-b4b8-00dd9956b88f" ;
static String ONTOLOGY = "CCF" ;	//CropsUsage Ontology ID

static boolean AVOID_BUG = true;


/** Downloads pdf document from url.
 * 
 * @return The pdf document in text format, using Apache pdfbox, null if fails.
 */
public static String getPdf(String url) {
  PDFParser parser;
  String parsedText;
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
  } catch (Exception e) {
    System.out.println("An exception occured while parsing the PDF Document.");
    e.printStackTrace();
    try {
      if (cosDoc != null) cosDoc.close();
      if (pdDoc != null) pdDoc.close();
    } catch (Exception e1) {
      e.printStackTrace();
    }
    return null;
  }      
  return parsedText;
}

public String annotate(String text)
{
	String doc_name = "doc"+(System.currentTimeMillis()/1000);
	  doc_name = doc_name.replaceAll(".pdf$","");
	  
	  String doc = text;
	  //String[] splitted = doc.split("\n");
	  
	  if (AVOID_BUG)
	    doc = doc.substring(1000,1499);

	  String ret = httpPost(doc, ONTOLOGY);

	  try {
		  
		  if(!true)
		  {
			    // Writes txt file
			    PrintStream ps = new PrintStream(new FileOutputStream(doc_name+".txt"));
			    ps.print(doc);
			    ps.flush(); ps.close();
		
			    // Writes rdf
			    ps = new PrintStream(new FileOutputStream(doc_name+".rdf"));
			    ps.print(ret);
			    ps.flush(); 
			    ps.close();
		  }
	    return ret;

	  } catch (Exception e) {
	    System.err.println("Error writing file...");
	    e.printStackTrace();
	    
	    return "";
	  }
	  
}

/** Sends POST request to agroportal's annotator
 * 
 * @param document : The content to be annotated
 * @param onto : The ontology to be used for annotation
 * @return The annotation in rdf format.
 */
static String httpPost(String document, String onto) {
  /* 
   * wget -O test.rdf "http://services.stageportal.lirmm.fr/annotator/
   * ?apikey=YOUR_API_KEY
   * &ontologies=CCF
   * &longest_only=false
   * &exclude_numbers=false
   * &whole_word_only=true
   * &exclude_synonyms=false
   * &expand_mappings=false
   * &format=rdf
   * &text=CEFEL "
   */
  try {
    URL url = new URL(AGROPORTAL);
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection)con;
    http.setRequestMethod("POST");
    http.setRequestProperty("User-Agent", "AgroHackathon pdf annotator V-(minus)1.0.1");
    http.setDoOutput(true);
    
    String urlParameters = "apikey="+API_KEY
      +"&ontologies="+onto
      +"&whole_word_only=true"
      +"&format=rdf"
      +"&text="+document;
		
		// Send post request
		DataOutputStream wr = new DataOutputStream(http.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
    
    int responseCode = http.getResponseCode();

		System.err.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.err.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new java.io.InputStreamReader(http.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine.trim()+"\n");
		}
		in.close();
    
    return response.toString();

  } catch (Exception ex) {
    ex.printStackTrace();
  }
  return null;
}

/*static public void main(String[] args) {
  if (args.length != 1) {
    System.err.println("Usage : java fr.agrohackathon.Pdfextract <PdfURL>");
    System.exit(-1);
  }

  String[] dummy = args[0].split("/");
  String doc_name = dummy[dummy.length - 1];
  doc_name = doc_name.replaceAll(".pdf$","");
  
  String doc = getPdf(args[0]);
  String[] splitted = doc.split("\n");
  
  if (AVOID_BUG)
    doc = doc.substring(0,499);

  String ret = httpPost(doc, ONTOLOGY);

  try {
    // Writes txt file
    PrintStream ps = new PrintStream(new FileOutputStream(doc_name+".txt"));
    ps.print(doc);
    ps.flush(); ps.close();

    // Writes rdf
    ps = new PrintStream(new FileOutputStream(doc_name+".rdf"));
    ps.print(ret);
    ps.flush(); ps.close();

  } catch (Exception e) {
    System.err.println("Error writing file...");
    e.printStackTrace();
    System.exit(-1);
  }

  System.exit(0);
  }
  */


}