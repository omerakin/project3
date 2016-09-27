package cs601.test;

import cs601.hotelapp.Address;
import cs601.hotelapp.Hotel;
import cs601.hotelapp.ThreadSafeHotelData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

public class Driver {
	public static void main(String[] args) {
		
		
		ThreadSafeHotelData data = new ThreadSafeHotelData();
		// Load hotel info from hotels200.json
		//data.loadHotelInfo("input/hotels200.json");		
		// Traverse input/reviews directory recursively, 
		// find all the json files and load reviews
		//data.loadReviews(Paths.get("input/reviews"));
		//data.printToFile(Paths.get("outputFile"));
		
	}
}
