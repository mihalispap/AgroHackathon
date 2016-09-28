package com.agrohackathon.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class QueryVitisSPARQL {

	public String annotate(String text)
	{
		String sparqlQuery;
		
		String[] splitted=text.split(" ");
		
		try
		{
			FileWriter fostream = new FileWriter("sample.rdf");
			BufferedWriter out = new BufferedWriter(fostream);
			
			int counter=0;
			
			for(int i=0;i<splitted.length;i++)
			{
	
				
				splitted[i]=splitted[i].replace("\n", "");
				splitted[i]=splitted[i].replace(",", "");
				splitted[i]=splitted[i].replace("(", "");
				splitted[i]=splitted[i].replace(")", "");
				
				
				if(splitted[i].length()<=4)
					continue;
				if(!splitted[i].equals("Chardonnay") && !splitted[i].equals("Pinot"))
					continue;
				
				counter++;
				
				if(counter>50)
					break;
				
				//System.out.println("Querying:"+splitted[i]);
				
				sparqlQuery=""
						+ "CONSTRUCT "
						+ "{ ?s <http://vitis.agroknow.com/ns#hasCultivationCountry> ?cl.?s <http://www.w3.org/2004/02/skos/core#prefLabel> ?pl."
						+ "?s <http://vitis.agroknow.com/ns#hasGrapeColor> ?gcl.?s <http://www.w3.org/2004/02/skos/core#broader> ?bt."
						+ "?s <http://www.w3.org/2000/01/rdf-schema#label> ?label.?s <http://www.w3.org/2004/02/skos/core#altLabel> ?altl."
						+ "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept>.}"
						+"WHERE {"
		+"?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept>."
		+ "?s <http://www.w3.org/2004/02/skos/core#prefLabel> \""+splitted[i]+"\". ?s <http://www.w3.org/2004/02/skos/core#prefLabel> ?pl. "
		+"OPTIONAL {?s <http://vitis.agroknow.com/ns#hasCultivationCountry> ?c.?c <http://www.w3.org/2000/01/rdf-schema#label> ?cl.}"
		+"OPTIONAL {?s <http://vitis.agroknow.com/ns#hasGrapeColor> ?gc.?gc <http://www.w3.org/2000/01/rdf-schema#label> ?gcl.}"
		+"OPTIONAL {?s <http://www.w3.org/2004/02/skos/core#broader> ?bt.}OPTIONAL {?s <http://www.w3.org/2004/02/skos/core#altLabel> ?altl.}"
		+"?s <http://www.w3.org/2000/01/rdf-schema#label> ?label.}";
				
				Query query = QueryFactory.create(sparqlQuery);
				
				QueryExecution qExe = QueryExecutionFactory.sparqlService(
						"http://vitis.agroknow.com/sparql/grape-varieties", query 
					);
				//qExe.
				
				Model test = qExe.execConstruct();
				//Model results = qexec.execConstruct();
				
				if(test.size()==0)
					continue;
				System.out.println(test.size());
				
				test.write(out);
			
			}
			
			out.close();
			String result="";
			
			try (BufferedReader br = new BufferedReader(new FileReader("sample.rdf"))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       result+=line;
			    }
			    
			    br.close();
			    return result;
			}
			
			
		}
		catch(java.lang.Exception e)
		{
			return "";
		}
		
	}
	
}
