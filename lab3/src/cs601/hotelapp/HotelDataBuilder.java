package cs601.hotelapp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HotelDataBuilder {

	
	ThreadSafeHotelData hdata = new ThreadSafeHotelData();
	
	
	/**
	 * Read the json file with information about the hotels (id, name, address,
	 * etc) and load it into the appropriate data structure(s). Note: This
	 * method does not load reviews
	 * 
	 * @param filename
	 *            the name of the json file that contains information about the
	 *            hotels
	 */
	public void loadHotelInfo(String jsonFilename) {

		// Hint: Use JSONParser from JSONSimple library
		// FILL IN CODE
		
		//Get the file directory and find the path
		Path jsonFileNameDirectory = Paths.get(jsonFilename);
		String jsonFilenameString = jsonFileNameDirectory.toAbsolutePath().toString();
		
		
		JSONParser parser = new JSONParser(); 
		try {
			Object object = parser.parse(new FileReader(jsonFilenameString));
			JSONObject jsonObject = (JSONObject) object;
			
			JSONArray listOfHotel = (JSONArray) jsonObject.get("sr");
			JSONObject jsonObjectHotel;
			
			for (int i=0; i<listOfHotel.size();i++) {
				jsonObjectHotel = (JSONObject) listOfHotel.get(i);
				
				// Get hotelId.
				String hotelId = (String) jsonObjectHotel.get("id");
				// Get hotelName.
				String hotelName = (String) jsonObjectHotel.get("f");
				// Get hotelCity.
				String hotelCity = (String) jsonObjectHotel.get("ci");
				// Get hotelState
				String hotelState = (String) jsonObjectHotel.get("pr");
				// Get hotelStreetAddress
				String hotelStreetAddress = (String) jsonObjectHotel.get("ad");
				//Create jsonObjectHotelLL to get Lat and Lng
				JSONObject jsonObjectHotelLL = (JSONObject) jsonObjectHotel.get("ll");
				// Get hotelLat
				double hotelLat = Double.parseDouble((String) jsonObjectHotelLL.get("lat"));
				// Get hotelLon
				double hotelLon = Double.parseDouble((String) jsonObjectHotelLL.get("lng"));
				
				// Add to the hotelsGivenByHotelId
				hdata.addHotel(hotelId, hotelName, hotelCity, hotelState, hotelStreetAddress, hotelLat, hotelLon);
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	

	/**
	 * Load reviews for all the hotels into the appropriate data structure(s).
	 * Traverse a given directory recursively to find all the json files with
	 * reviews and load reviews from each json. Note: this method must be
	 * recursive and use DirectoryStream as discussed in class.
	 * 
	 * @param path
	 *            the path to the directory that contains json files with
	 *            reviews Note that the directory can contain json files, as
	 *            well as subfolders (of subfolders etc..) with more json files
	 */
	public void loadReviews(Path path) {
		// FILL IN CODE

		// Hint: first, write a separate method to read a single json file with
		// reviews
		// using JSONSimple library
		// Call this method from this one as you traverse directories and find
		// json files

		try {
			DirectoryStream<Path> pathsList = Files.newDirectoryStream(path);
			for(Path p : pathsList){
				// check that file is directory or not.
				if(!Files.isDirectory(p)){
					// If not, then this is a json. Add it as review
					JSONParser jsonParser = new JSONParser();
					try {
						JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(p.toAbsolutePath().toString()));
						
						//reviewDetails Object
						JSONObject reviewDetails = (JSONObject) jsonObject.get("reviewDetails");
						//reviewCollection Object
						JSONObject reviewCollection = (JSONObject) reviewDetails.get("reviewCollection");
						//review Array
						JSONArray review = (JSONArray) reviewCollection.get("review");
						JSONObject reviewObject;
						for(int i=0; i<review.size();i++){
							reviewObject = (JSONObject) review.get(i);
							
							String hotelId = (String) reviewObject.get("hotelId");
							String reviewId = (String) reviewObject.get("reviewId");
							long ratingLong = (long) reviewObject.get("ratingOverall");
							int rating = (int) ratingLong;
							String reviewTitle = (String) reviewObject.get("title");
							String reviewText = (String) reviewObject.get("reviewText");
							boolean isRecom = ("YES" == (String) reviewObject.get("isRecommended"));
							String date = (String) reviewObject.get("reviewSubmissionTime");
							
							
							/*
							JSONArray managementResponses = (JSONArray) reviewObject.get("managementResponses");
							String date = "";
							if (managementResponses.size()>0) {
								JSONObject managementResponsesObject = (JSONObject) managementResponses.get(0);
								date = (String) managementResponsesObject.get("date");
							}
							*/
							
							String username = (String) reviewObject.get("userNickname");
							if(username.equals("")){
								username = "anonymous";
							}
							//Add review
							hdata.addReview(hotelId, reviewId, rating, reviewTitle, reviewText, isRecom, date, username);
						}
					
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (Files.isDirectory(p)) {
					// If it is, check the subfolders.
					// this method get the paremeter path, and in it, check sub directories.
					loadReviews(p);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	}

}
