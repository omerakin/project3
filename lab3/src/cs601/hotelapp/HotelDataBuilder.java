package cs601.hotelapp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

}
