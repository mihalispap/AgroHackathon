package com.agrohackathon.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.agrohackathon.utils.ParseGET;
import com.agrohackathon.utils.Pdfextract;
import com.agrohackathon.utils.QueryVitisSPARQL;
import com.agrohackathon.utils.TextAnnotatorWithFremeAPI;
import com.agrohackathon.utils.PdfAnnotateAP;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
public class Endpoint {

	@ApiOperation(value = "Get text from pdf", nickname = "pdf text extractor")
	@RequestMapping( value="/extract", method={RequestMethod.GET})
	@ApiImplicitParams({	
        @ApiImplicitParam(
    			name = "url", 
    			value = "url of pdf", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="http://www.agriculturejournals.cz/uniqueFiles/04084.pdf")
      })	
	String run(HttpServletRequest request) {
        
		Pdfextract pdfextract=new Pdfextract();
		
		String url;
		ParseGET parser=new ParseGET();
		
		url=parser.parseURL(request);
		
		//System.out.println(pdfextract.getPdf("http://www.agriculturejournals.cz/uniqueFiles/04084.pdf"));
		
		return pdfextract.getPdf(url);

        
    }
	
	@ApiOperation(value = "Extract and Annotate", nickname = "pdf text extractor/annotator")
	@RequestMapping( value="/annotate", method={RequestMethod.GET})
	@ApiImplicitParams({	
        @ApiImplicitParam(
    			name = "url", 
    			value = "url of pdf", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="http://ontology.irstea.fr/bsv/files/RCeA14Cle/BSV_Legumes_01.pdf"),
        @ApiImplicitParam(
    			name = "source", 
    			value = "where to annotate from (eg. agroportal, freme, vitissparql)", 
    			required = true, 
    			dataType = "string", 
    			paramType = "query",
    			defaultValue="agroportal")
      })	
	String run_annotation(HttpServletRequest request) {
        
		Pdfextract pdfextract=new Pdfextract();
		
		String url;
		ParseGET parser=new ParseGET();
		
		url=parser.parseURL(request);
		
		//System.out.println(pdfextract.getPdf("http://www.agriculturejournals.cz/uniqueFiles/04084.pdf"));
		
		String text = pdfextract.getPdf(url);
		
		String source = parser.parseSource(request);
		
		if(source.equals("agroportal"))
		{
			PdfAnnotateAP annotator = new PdfAnnotateAP();
			return annotator.annotate(text);
		}
        if(source.equals("freme"))
        {
        	TextAnnotatorWithFremeAPI annotator = new TextAnnotatorWithFremeAPI();
        	return annotator.annotate(text);
        }
        if(source.equals("vitissparql"))
        {
        	QueryVitisSPARQL annotator = new QueryVitisSPARQL();
        	return annotator.annotate(text);
        }
		
		if(source.isEmpty())
		{
			PdfAnnotateAP annotator = new PdfAnnotateAP();
			return annotator.annotate(text);
		}
		
		return "";
    }
}
