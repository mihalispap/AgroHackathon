package com.agrohackathon.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TextAnnotatorWithFremeAPI {

    private static final String USER_AGENT = "Mozilla/5.0";

    public String annotate(String text)
    {       
        String uri = "http://api.freme-project.eu/current/e-terminology/tilde?"
                //+ "body=" + URLEncoder.encode(value, "UTF-8") 
                + "&informat=text&outformat=json-ld"
                + "&source-lang=en&target-lang=en&domain=TaaS-1001";

        Map<String, Object> params = new LinkedHashMap<>();
        String pdfurl = text;//"http://52.18.30.225:8080/agrohackathon-1.0/extract?url=http%3A%2F%2Fwww.agriculturejournals.cz%2FuniqueFiles%2F04084.pdf";

        //params.put("body", "Organic farming is an alternative agricultural system which originated early in the 20th Century in reaction to rapidly changing farming practices. Organic agriculture continues to be developed by various Organic Agriculture organizations today. It relies on fertilizers of organic origin such as compost, manure, green manure, and bone meal and places emphasis on techniques such as crop rotation, companion planting. Biological pest control, mixed cropping and the fostering of insect predators are encouraged. Generally, although there are exceptions, organic standards are designed to allow the use of naturally occurring substances while prohibiting or strictly limiting synthetic substances.[2] For instance, naturally occurring pesticides such as pyrethrin and rotenone are permitted, while synthetic fertilizers and pesticides are generally prohibited. Synthetic substances that are allowed include, for example, copper sulfate, elemental sulfur and Ivermectin. Genetically modified organisms, nanomaterials, human sewage sludge, plant growth regulators, hormones, and antibiotic use in livestock husbandry are prohibited.[3][4] Reasons for advocation of organic farming include real or perceived advantages in sustainability,[5] openness, independence, health, food security, and food safety, although the match between perception and reality is continually challenged.");
        params.put("body", text);
        double taConfidenceThreshold = 0.60;
        return selectAnnotations(sendPost(uri, params), taConfidenceThreshold).toString();
        
        //return "";
    }

    static String sendGet(String url) throws Exception {

        //String url = "http://www.google.com/search?q=mkyong";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

    static byte[] encodePostData(Map<String, Object> params) {
        StringBuilder postData = new StringBuilder();
        byte[] postDataByte = null;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            try {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                postDataByte = postData.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return postDataByte;
    }

    static JSONArray selectAnnotations(JSONArray annotations, double taConfidenceThreshold) {
        JSONArray selectedAnnotations = new JSONArray();
        //ArrayList<String> ids = new ArrayList<>();
        if (annotations != null) {
            //System.out.println(annotations);
            // 1. select the @ids of annotations with a taConfidence >= taConfidenceThreshold
            for (int i = 0; i < annotations.length(); i++) {
                try {
                    JSONObject annotation = (JSONObject) annotations.get(i);
                    JSONObject selectedAnnotation = new JSONObject();
                    if (annotation.has("itsrdf:taConfidence")) {
                    	try
                    	{
	                        if (Double.parseDouble(annotation.get("itsrdf:taConfidence").toString()) >= taConfidenceThreshold) {
	                            //ids.add(annotation.getString("@id"));
	                            selectedAnnotation.put("@id", annotation.getString("@id"));
	                            selectedAnnotation.put("label", annotation.get("label"));
	                            selectedAnnotations.put(selectedAnnotation);
	                            
	                            System.out.println("Filtered in:"+annotation.get("label"));
	                        }
                    	}
                    	catch(java.lang.NumberFormatException e)
                    	{
                    		System.out.println("SomeException");
                    		e.printStackTrace();
                    	}                    	
                    }
                    else if (annotation.has("taConfidence")                    		
                    		) {
                    	try
                    	{
	                        if (Double.parseDouble(annotation.get("taConfidence").toString()) >= taConfidenceThreshold) {
	                            //ids.add(annotation.getString("@id"));
	                            selectedAnnotation.put("@id", annotation.getString("@id"));
	                            selectedAnnotation.put("label", annotation.get("label"));
	                            selectedAnnotations.put(selectedAnnotation);
	                            
	                            System.out.println("Filtered in:"+annotation.get("label"));
	                        }
                    	}
                    	catch(java.lang.NumberFormatException e)
                    	{
                    		System.out.println("SomeException");
                    		e.printStackTrace();
                    	}                    	
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //System.out.println(selectedAnnotations);
            // 2. select the skos:Concept of the object for which we selected the id in (1)
            for (int j = 0; j < selectedAnnotations.length(); j++) {
                try {
                    String id = ((JSONObject) selectedAnnotations.get(j)).getString("@id");
                    for (int i = 0; i < annotations.length(); i++) {
                        try {
                            JSONObject annotation = (JSONObject) annotations.get(i);
                            if(annotation.has("termInfoRef") && annotation.getString("annotationUnit").equals(id)){
                                ((JSONObject) selectedAnnotations.get(j)).put("skos:Concept", annotation.get("termInfoRef"));                                
                            } else {
                                continue;
                            }
                            
                        } catch (JSONException ex) {                            
                            Logger.getLogger(selectedAnnotations.get(j)+"\n"+TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
//                                            subject.vocabulary = "agrovoc";
//                                            subject.score = Double.parseDouble(annotation.get("taConfidence").toString());
//                                            subject.uri = null;
//                    if (annotation.has("termInfoRef")) {
//                        System.out.println(annotation.get("termInfoRef"));
//                    }
                catch (JSONException ex) {
                    Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.err.println("null");
        }
        return selectedAnnotations;
    }

    public static JSONArray sendPost(String uri, Map<String, Object> params) {
        try {
            URL url = new URL(uri);
            //params.put("body", "wheats");            
            byte[] postDataBytes = encodePostData(params);
            System.out.println("Calling FREME e-terminology: "+uri);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/ld+json;charset=UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(postDataBytes);

            BufferedReader streamReader;

            streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;

            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            int responseCode = connection.getResponseCode();
            System.out.println(connection.getResponseCode());
            System.out.println(connection.getResponseMessage());
            switch (responseCode) {
                case 200:
                    JSONObject root = new JSONObject(responseStrBuilder.toString());
                    System.out.println(root);
                    JSONArray annotations = (JSONArray) root.get("@graph");
                    return annotations;
                default:
                    return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TextAnnotatorWithFremeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
