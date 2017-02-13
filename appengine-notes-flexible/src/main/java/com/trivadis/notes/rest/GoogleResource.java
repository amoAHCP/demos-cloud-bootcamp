package com.trivadis.notes.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import com.trivadis.notes.model.Location;
import com.trivadis.notes.model.Translation;

@Path("google")
public class GoogleResource {
    @Path("translate")
    @GET
    // http://localhost:8080/api/google/translate?lang=de&message=The%20quick%20fox%20jumps%20over%20the%20lazy%20dog
    public Response translate(@QueryParam("message") String message, @QueryParam("lang") String lang) {
        try {
            HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            
            if (lang.length()>2)
            	lang = lang.substring(0, 2);
            
            String appName = System.getenv("APPNAME");
            String apiKey = System.getenv("APIKEY");

            final Translate translate = new Translate //
                    .Builder(HTTP_TRANSPORT, JSON_FACTORY, null)//
                    .setApplicationName(appName)
                    .build();

            List<String> q = new LinkedList<>();
            q.add(message);

            TranslationsListResponse response = translate.translations()//
                    .list(q, lang)//
                    .setKey(apiKey) //
                    .execute();

            TranslationsResource translated = response.getTranslations().get(0);
            Translation result = new Translation();
            result.setLangOriginal(translated.getDetectedSourceLanguage());
            result.setLangTranslated(lang);
            result.setTextOriginal(message);
            result.setTextTranslated(translated.getTranslatedText());
            
            return Response.ok(result, MediaType.APPLICATION_JSON).encoding("utf-8").build();
        } catch (Exception ex) {
            ex.printStackTrace();

            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    // http://localhost:8080/api/google/locate?latitude=48.725160599999995&longitude=9.1136542
    @Path("locate")
    @GET
    public Response locate(@QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude) {
    		
    	try {
    		String resultAddress = "Unknown";
	    		
    		if (0.0d!=latitude || 0.0d != longitude) {
	    		String apiKey = System.getenv("APIKEY");
	    		
				GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
				GeocodingResult[] geocodeResult = GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude)).await();
				
				resultAddress = geocodeResult[0].formattedAddress;
    		}
    		
			Location result = new Location();
			result.setAddress(resultAddress);
			result.setLatitude(latitude);
			result.setLongitude(longitude);
			
			return Response.ok(result, MediaType.APPLICATION_JSON).encoding("utf-8").build();
		} catch (Exception ex) {
            ex.printStackTrace();

            return Response.serverError().entity(ex.getMessage()).build();
		}
    }    
}
