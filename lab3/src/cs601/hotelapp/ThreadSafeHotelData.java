package cs601.hotelapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cs601.concurrent.ReentrantReadWriteLock;

/**
 * Class HotelData - a data structure that stores information about hotels and
 * hotel reviews. Allows to quickly lookup a hotel given the hotel id. 
 * Allows to easily find hotel reviews for a given hotel, given the hotelID. 
 * Reviews for a given hotel id are sorted by the date and user nickname.
 *
 */
public class ThreadSafeHotelData {

	// FILL IN CODE - declare data structures to store hotel data
	private final Map<String, Hotel> hotelsGivenByHotelId;
	private final Map<String, TreeSet<Review>> reviewsGivenByHotelId;
	private Hotel hotel;
	private Address address;
	private Review reviews;
	private Boolean isSuccessful;
	private List<String> hotelIdList;
	
	//Declared ReentrantReadWriteLock lock 
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	

	/**
	 * Default constructor.
	 */
	public ThreadSafeHotelData() {
		// FILL IN CODE
		// Initialise all data structures
		hotelsGivenByHotelId = new HashMap<String,Hotel>();
		reviewsGivenByHotelId = new HashMap<String,TreeSet<Review>>();

	}

	/**
	 * Create a Hotel given the parameters, and add it to the appropriate data
	 * structure(s).
	 * 
	 * @param hotelId
	 *            - the id of the hotel
	 * @param hotelName
	 *            - the name of the hotel
	 * @param city
	 *            - the city where the hotel is located
	 * @param state
	 *            - the state where the hotel is located.
	 * @param streetAddress
	 *            - the building number and the street
	 * @param latitude
	 * @param longitude
	 */
	public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
			double lon) {
		// FILL IN CODE
		/*
		 * Here, I locked address and hotel variable, is that okey ?
		 */
		
		//With the lockWrite it is added to our Hasmap safely
		lock.lockWrite();
		try {
			//Set the values to the address and hotel object.
			address = new Address(streetAddress, city, state, lat, lon);
			hotel = new Hotel(hotelId, hotelName, address);		
			//Add to the hotelsGivenByHotelId TreeMap.
			hotelsGivenByHotelId.put(hotelId, hotel);
		} finally {
			lock.unlockWrite();
		}
		
	}

	/**
	 * Add a new review.
	 * 
	 * @param hotelId
	 *            - the id of the hotel reviewed
	 * @param reviewId
	 *            - the id of the review
	 * @param rating
	 *            - integer rating 1-5.
	 * @param reviewTitle
	 *            - the title of the review
	 * @param review
	 *            - text of the review
	 * @param isRecommended
	 *            - whether the user recommends it or not
	 * @param date
	 *            - date of the review in the format yyyy-MM-dd, e.g.
	 *            2016-08-29.
	 * @param username
	 *            - the nickname of the user writing the review.
	 * @return true if successful, false if unsuccessful because of invalid date
	 *         or rating. Needs to catch and handle ParseException if the date is invalid.
	 *         Needs to check whether the rating is in the correct range
	 */
	public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
			boolean isRecom, String date, String username) {
		
		/*
		 * Here, I have try inside of try, so is it okey ?
		 */
		
		//With the lockRead it is added to our Hasmap safely
		lock.lockWrite();
		try {
			// FILL IN CODE
			//Initialise it to default value.
			isSuccessful = false;
			//Check the rating is in the correct range or not.
			
			//TODO: simplify if condition, since both >1 and <5 return false
			if(1> rating || 5 < rating) {
				// set the false.
				isSuccessful = false;
			} else {
				//Check the date is correct format or not.
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date date1 = sdf.parse(date);
					// set the true.
					isSuccessful = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Date is invalid!");
				}
			}
			// If successful is true add it.
			if(isSuccessful && hotelsGivenByHotelId.containsKey(hotelId)) {
				//Set the values to the reviews object.
				reviews = new Review(reviewId, hotelId, reviewTitle, review, username, date, rating);			
				//Check that if hotel id already exist or not
				if(!(reviewsGivenByHotelId.containsKey(hotelId))) {
					TreeSet<Review> newReviewSet = new TreeSet<Review>();
					newReviewSet.add(reviews);
					//Add to the reviewsGivenByHotelId TreeMap.
					reviewsGivenByHotelId.put(hotelId, newReviewSet);
				} else {
					TreeSet<Review> existingReviewSet;
					existingReviewSet = reviewsGivenByHotelId.get(hotelId);			
					existingReviewSet.add(reviews);	
					//Add to the reviewsGivenByHotelId TreeMap.
					reviewsGivenByHotelId.put(hotelId, existingReviewSet);
				}
			}
			return isSuccessful; // don't forget to change it
			
		} finally {
			lock.unlockWrite();
		}
		
	}

	/**
	 * Return an alphabetized list of the ids of all hotels
	 * 
	 * @return
	 */
	public List<String> getHotels() {
		// FILL IN CODE
		//Initialise an ArrayList to store hotelIds
		
		//TODO: you'd better put all data structures at the first place
		hotelIdList = new ArrayList<>();
			
		//Add hotelId to ArrayList
		for (String hotelId: hotelsGivenByHotelId.keySet()){
			hotelIdList.add(hotelId);
		}
		//Sort hotelIds
		Collections.sort(hotelIdList);
		//return it.
		return hotelIdList; // don't forget to change it
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
							addReview(hotelId, reviewId, rating, reviewTitle, reviewText, isRecom, date, username);
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


	/**
	 * Returns a string representing information about the hotel with the given
	 * id, including all the reviews for this hotel separated by
	 * -------------------- Format of the string: HoteName: hotelId
	 * streetAddress city, state -------------------- Review by username: rating
	 * ReviewTitle ReviewText -------------------- Review by username: rating
	 * ReviewTitle ReviewText ...
	 * 
	 * @param hotel
	 *            id
	 * @return - output string.
	 */
	public String toString(String hotelId) {

		// FILL IN CODE
		String result = "";
		
		for (String hotel_id_hotels: hotelsGivenByHotelId.keySet()){
			if(hotel_id_hotels.equals(hotelId)){
				result = result + hotelsGivenByHotelId.get(hotel_id_hotels).getHotel_name() + ": ";
				result = result + hotelsGivenByHotelId.get(hotel_id_hotels).getHotel_id() + "\n";
				result = result + hotelsGivenByHotelId.get(hotel_id_hotels).getAddress().getStreet_address() + "\n";
				result = result + hotelsGivenByHotelId.get(hotel_id_hotels).getAddress().getCity() + ", ";
				result = result + hotelsGivenByHotelId.get(hotel_id_hotels).getAddress().getState() + "\n";		
			}
		}
		
		for (String hotel_id_review: reviewsGivenByHotelId.keySet()){
			if(hotel_id_review.equals(hotelId)){
				for(Review hotelIdReview : reviewsGivenByHotelId.get(hotel_id_review)){
					result = result + "--------------------\n";
					result = result + "Review by " + hotelIdReview.getUsername() + ": ";
					result = result + hotelIdReview.getRating() + "\n";
					result = result + hotelIdReview.getReview_title() + "\n";
					result = result + hotelIdReview.getReview_text() + "\n";
				}
				
				/*
				for(int i=0; i<reviewsGivenByHotelId.get(hotel_id_review).size(); i++){
					result = result + "--------------------\n";
					result = result + "Review by " + reviewsGivenByHotelId.get(hotel_id_review).headSet(i) + ": ";
					result = result + reviewsGivenByHotelId.get(hotel_id_review).getRating() + "\n";
					result = result + reviewsGivenByHotelId.get(hotel_id_review).getReview_title() + "\n";
					result = result + reviewsGivenByHotelId.get(hotel_id_review).getReview_text() + "\n";
				}
				*/
			}
		}

		return result; // don't forget to change to the correct string
	}

	/**
	 * Save the string representation of the hotel data to the file specified by
	 * filename in the following format: 
	 * an empty line 
	 * A line of 20 asterisks ******************** on the next line 
	 * information for each hotel, printed in the format described in the toString method of this class.
	 * 
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	public void printToFile(Path filename) {
		// FILL IN CODE

		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(filename.toString()));
			
			for(String hotelid_info: getHotels()){
				printWriter.println("\n********************");
				printWriter.print(toString(hotelid_info));
			}
			printWriter.flush();
			printWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
//TODO: remove all System.out.print code if just for debugging
