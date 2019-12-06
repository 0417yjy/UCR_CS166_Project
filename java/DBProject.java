/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.PreparedStatement; // for using prepared statements
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random; // for making price
import java.util.StringTokenizer; // for get id by given name

// to get today's date
import java.util.Date; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class DBProject {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of DBProject
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public DBProject (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end DBProject

/*********** Newly defined methods start from here ***********/
  /**
   * Only used for query like "SELECT COUNT(*) FROM $Relation WHERE $Condition"
   * Return the count result
   *
   * @param query the input query string
   * @throws java.sql.SQLException when failed to execute the query
   */
  public int getCountByExecute (String query) throws SQLException {
    int result;
    // creates a statement object
    Statement stmt = this._connection.createStatement();

    // issues the update instruction
    ResultSet rs =  stmt.executeQuery(query);

    // gets the result of count
    rs.next();
    result = rs.getInt(1);

    // close the instruction
    stmt.close();

    return result;
  }

  /**
   * Used for custom query of additional function 17 for testing
   * print the query's result
   */
  public static void executeCustom(DBProject esql) {
      try {
        System.out.print("\tEnter your query (SELECT only): ");
        String query = in.readLine();
        esql.executeQuery(query);
      } catch(Exception e) {
        System.err.println(e.getMessage());
      }
   }

   /**
   * Get ID by name(Customer or Staff) or SSN(Staff)
   * If find the searching one, return the id. Otherwise, return -1 as a mark of fail
   * 
   * @param mod Search by name if the mod is 1 / Search by the ssn if the mod is 2
   * @param name_or_ssn Searching is processed with this variable
   * @param relation Indicates which relation to search
   * @throws java.sql.SQLException when failed to execute the query
   */
   public int getID(int mod, String name_or_ssn, String relation) throws SQLException {
    if(mod == 1) {
        StringTokenizer st = new StringTokenizer(name_or_ssn);
        String query = "";
        // make initial query
        switch(relation) {
            case "Customer":
            query = "SELECT customerID FROM Customer WHERE fName = '";
            break;

            case "Staff":
            query = "SELECT employerID FROM Staff WHERE fname = '";
            break;

            default:
            System.out.println("Couldn't find the relation!");
            return -1;
        }

        // get fname and lname
        if(!st.hasMoreTokens()) {
         return -1; // If there is no token, invalid name
        }
        String fname = st.nextToken();
        if(!st.hasMoreTokens()) {
         return -1; // if there is no token, invalid name
        }
        String lname = st.nextToken();

        if(st.hasMoreTokens()) {
            return -1; //if there are tokens available, invalid name
        }

        // complete query
        query += (fname + "' AND lname = '" + lname + "'");
        System.out.println("Query made is: " + query);
        
        // creates a statement object
        Statement stmt = this._connection.createStatement();
        // issues the update instruction
        ResultSet rs =  stmt.executeQuery(query);
        rs.next();
        int result = rs.getInt(1);
        return result;
        }
    else if(mod == 2) {
        // make query
        String query = "SELECT employerID FROM Staff WHERE SSN = " + name_or_ssn;
        //System.out.println("Query made is: " + query);

        // creates a statement object
        Statement stmt = this._connection.createStatement();
        // issues the update instruction
        ResultSet rs =  stmt.executeQuery(query);
        rs.next();
        int result = rs.getInt(1);
        return result;
    }
    return -1;
   }

   public int getNewID(String relation) {
        int new_id = 0;

        try {
            String query = "SELECT count(*) FROM " + relation;
            new_id = this.getCountByExecute(query); // sets new customer's id
        } catch(SQLException e) {
            e.getMessage();
        }
        return new_id;
   }
/*********** Newly defined methods ends here ***********/

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            DBProject.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      DBProject esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the DBProject object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new DBProject (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add new customer");
				System.out.println("2. Add new room");
				System.out.println("3. Add new maintenance company");
				System.out.println("4. Add new repair");
				System.out.println("5. Add new Booking"); 
				System.out.println("6. Assign house cleaning staff to a room");
				System.out.println("7. Raise a repair request");
				System.out.println("8. Get number of available rooms");
				System.out.println("9. Get number of booked rooms");
				System.out.println("10. Get hotel bookings for a week");
				System.out.println("11. Get top k rooms with highest price for a date range");
				System.out.println("12. Get top k highest booking price for a customer");
				System.out.println("13. Get customer total cost occurred for a give date range"); 
				System.out.println("14. List the repairs made by maintenance company");
				System.out.println("15. Get top k maintenance companies based on repair count");
				System.out.println("16. Get number of repairs occurred per year for a given hotel room");
                System.out.println("17. Custom Query");
				System.out.println("18. < EXIT");

            switch (readChoice()){
				   case 1: addCustomer(esql); break;
				   case 2: addRoom(esql); break;
				   case 3: addMaintenanceCompany(esql); break;
				   case 4: addRepair(esql); break;
				   case 5: bookRoom(esql); break;
				   case 6: assignHouseCleaningToRoom(esql); break;
				   case 7: repairRequest(esql); break;
				   case 8: numberOfAvailableRooms(esql); break;
				   case 9: numberOfBookedRooms(esql); break;
				   case 10: listHotelRoomBookingsForAWeek(esql); break;
				   case 11: topKHighestRoomPriceForADateRange(esql); break;
				   case 12: topKHighestPriceBookingsForACustomer(esql); break;
				   case 13: totalCostForCustomer(esql); break;
				   case 14: listRepairsMade(esql); break;
				   case 15: topKMaintenanceCompany(esql); break;
				   case 16: numberOfRepairsForEachRoomPerYear(esql); break;
               case 17: executeCustom(esql); break;
				   case 18: keepon = false; break;
				   default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice
   
   public static void addCustomer(DBProject esql){
	  // Given customer details add the customer in the DB 
      boolean address_inserted = false;
      boolean phno_inserted = false;
      boolean dob_inserted = false;
      boolean gender_inserted = false;
      int new_id = esql.getNewID("Customer");
    
      // set values and insert a new one
      try{
         String query = "INSERT INTO Customer (customerID, fName, lName";

         //get user inputs
         System.out.print("\t*Enter First Name: ");
         String input = in.readLine();
         while(input.length() == 0) {
            // if user didn't input something but just enter
            System.out.print("\tFirst Name cannot be null! Try again: ");
            input = in.readLine();
         }
         String fname = input;

         System.out.print("\t*Enter Last Name: ");
         input = in.readLine();
         while(input.length() == 0) {
            // if user didn't input something but just enter
            System.out.print("\tLast Name cannot be null! Try again: ");
            input = in.readLine();
         }
         String lname = input;

         System.out.print("\tEnter Address: ");
         input = in.readLine();
         String address = "";
         if(input.length() != 0) {
            // if user insert address
            address_inserted = true;
            address = input;
         }

         System.out.print("\tEnter Phone Number: ");
         input = in.readLine();
         String phno = "";
         if(input.length() != 0) {
            // if user insert phone number
            phno_inserted = true;
            phno = input;
         }

         System.out.print("\tEnter Birth of Date(mm/dd/yyyy): ");
         input = in.readLine();
         String dob = "";
         if(input.length() != 0) {
            // if user insert DOB
            dob_inserted = true;
            dob = input;
         }

         System.out.print("\tEnter Gender(Male / Female / Other): ");
         input = in.readLine();
         String gender = "";
         if(input.length() != 0) {
            // if user insert gender
            gender_inserted = true;
            gender = input;
         }

         // make query statement
         if(address_inserted) {
            query += ", Address";
         }
         if(phno_inserted) {
            query += ", phNo";
         }
         if(dob_inserted) {
            query += ", DOB";
         }
         if(gender_inserted) {
            query += ", gender";
         }
         query += (") VALUES (" + Integer.toString(new_id) + ", '" + fname + "', '" + lname + "'");

         if(address_inserted) {
            query += (",'" + address + "'");
         }
         if(phno_inserted) {
            query += ("," + phno);
         }
         if(dob_inserted) {
            query += (",'" + dob + "'");
         }
         if(gender_inserted) {
            query += (",'" + gender + "'");
         }
         query += ")";

         //System.out.println("Query made is: " + query);
         System.out.print("Executing query...");
         esql.executeUpdate(query);
         System.out.println("Completed");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }

   }//end addCustomer

   public static void addRoom(DBProject esql){
	  // Given room details add the room in the DB
    try{
    String query = "INSERT INTO Room (hotelID, roomNo, roomType) VALUES (";
    System.out.print("\t*Enter HotelID: ");
    String input = in.readLine();
    while(input.length() == 0) {
      //if user didn't input something but just enter
      System.out.print("\tHotelID cannot be null! Try again:");
      input = in.readLine();
    }
    String hotelID = input;

    //should i ask roomNO 
    System.out.print("\t*Enter RoomNo: ");
    input = in.readLine();
    while(input.length() ==0){
      System.out.print("\tRoomNo cannot be null! Try again: ");
      input = in.readLine();
    }
    String roomNo = input;

    //room type
    System.out.print("\t*Enter RoomType (Suite / Economy / Deluxe");
    input = in.readLine();
    while(input.length() == 0){
      System.out.print("\tRoomType cannot be null! Try again: ");
      input = in.readLine();
    }
    //change first character to upper case
    String roomType = input.substring(0, 1).toUpperCase() + input.substring(1);

    query += (hotelID + ", " + roomNo + ", " + roomType + ")");
    System.out.println("Query made is: " + query);
    System.out.print("Executing query...");
    esql.executeUpdate(query);
    System.out.println("Completed");
    }
    catch(Exception e){
      System.err.println (e.getMessage());
    }

   }//end addRoom

   public static void addMaintenanceCompany(DBProject esql){
      // Given maintenance Company details add the maintenance company in the DB
      boolean address_inserted = false;
      boolean certified_inserted = false;
      int new_id = esql.getNewID("MaintenanceCompany");

       // set values and insert a new one
      try{
         String query = "INSERT INTO MaintenanceCompany (cmpID, name";

         //get user inputs
         System.out.print("\t*Enter Name: ");
         String input = in.readLine();
         while(input.length() == 0) {
            // if user didn't input something but just enter
            System.out.print("\tName cannot be null! Try again: ");
            input = in.readLine();
         }
         String name = input;

         System.out.print("\tEnter Address: ");
         input = in.readLine();
         String address = "";
         if(input.length() != 0) {
            // if user insert address
            address_inserted = true;
            address = input;
         }

         System.out.print("\t*Is this company certified?(true/false): ");
         String certified = "";
         while(!certified_inserted) {
            input = in.readLine();
            if(input.length() == 0) {
                // if user didn't input something but just enter
                System.out.print("\tAnswer of this question cannot be null! Try again: ");
                continue;
            }
            input = input.toUpperCase();
            if(!(input.equals("TRUE") || input.equals("FALSE"))) {
                // if user inserted invalid answer
                System.out.print("\tInvalid Answer! Try again: ");
            }
            else {
                certified = input;
                certified_inserted = true;
            }
         }

         // make query statement
         if(address_inserted) {
            query += ", Address";
         }
         query += (", isCertified) VALUES (" + Integer.toString(new_id) + ", '" + name + "'");

         if(address_inserted) {
            query += (",'" + address + "'");
         }
         query += (", " + certified + ")");

         System.out.println("Query made is: " + query);
         System.out.print("Executing query...");
         esql.executeUpdate(query);
         System.out.println("Completed");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end addMaintenanceCompany

   public static void addRepair(DBProject esql){
	  // Given repair details add repair in the DB
      boolean description_inserted = false;
    boolean rptype_inserted = false;
    int new_id = esql.getNewID("Repair");
      
    try{
      String query = "INSERT INTO Repair (rID, hotelID, roomNo, mCompany, repairDate";

      //get user inputs
      System.out.print("\t*Enter HotelID: ");
      String input = in.readLine();
      while(input.length() ==0) {
        //if user didn't input something but just enter
        System.out.print("\tHotelID cannot be null! Try again: ");
        input = in.readLine();
      }
      String hotelID = input;

      System.out.print("\t*Enter RoomNo: ");
      input = in.readLine();
      while(input.length() ==0) {
        //if user didn't input something but just enter
        System.out.print("\tRoomNo cannot be null! Try again: ");
        input = in.readLine();
      }
      String RoomNo = input;

      System.out.print("\t*Enter cmpID: ");
      input = in.readLine();
      while(input.length() ==0) {
        System.out.print("\tcmpID cannot be null! Try again: ");
        input = in.readLine();
      }
      String mCompany = input;

      System.out.print("\t*Enter Repair Date(mm/dd/yyyy: ");
      input = in.readLine();
      while(input.length() == 0) {
              // if user didn't input something but just enter
              System.out.print("\tRepair Date cannot be null! Try again: ");
             input = in.readLine();
          }
      String repairDate = input;

      System.out.print("\tEnter description for this repair: ");
      input = in.readLine();
      String description = "";
      if(input.length() !=0){
        //if user insert something
        description_inserted = true;
        description = input;
      }

      System.out.print("\tEnter Repair Type(Small / Medium / Large: ");
      input = in.readLine();
      String rptype = "";
      if(input.length() != 0) {
          // if user insert address
          rptype_inserted = true;
          rptype = input;
      }

      // complete repair insert query
      if(description_inserted) {
          query += ", description";
      }
      if(rptype_inserted) {
          query += ", repairType";
      }
      query += (") VALUES (" + Integer.toString(new_id) + ", " + hotelID + ", " + RoomNo + ", " + mCompany + ", " + repairDate + "'");
      if(description_inserted) {
          query += ", '" + description + "'";
      }
      if(rptype_inserted) {
          query += ", '" + rptype + "'";
      }
      query += ")";
      System.out.print("Executing query...");
      esql.executeUpdate(query);
      System.out.println("Completed");
       } catch(Exception e){
      System.err.println (e.getMessage());
      }
   }//end addRepair

   public static void bookRoom(DBProject esql){
      // Given hotelID, roomNo and customer Name create a booking in the DB 
        boolean valid_name = false;
        int customer_id = 0;
        int new_id = esql.getNewID("Booking");

        // set values and insert a new one
      try{
        String query = "INSERT INTO Booking (bID, customer, hotelID, roomNo, bookingDate, noOfPeople, price) VALUES (";

        //get user inputs
        System.out.print("\t*Enter HotelID: ");
        String input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tHotelID cannot be null! Try again: ");
           input = in.readLine();
        }
        String hotelID = input;

        System.out.print("\t*Enter RoomNo: ");
        input = in.readLine();
        while(input.length() == 0) {
            // if user didn't input something but just enter
            System.out.print("\tRoomNo cannot be null! Try again: ");
           input = in.readLine();
        }
        String RoomNo = input;

        System.out.print("\t*Enter Customer's Name([fname] [lname]): ");
        while(!valid_name) {
            input = in.readLine();
            if(input.length() == 0) {
                // if user didn't input something but just enter
                System.out.print("\tYou must enter name! Try again: ");
             }
            customer_id = esql.getID(1, input, "Customer");
            if(customer_id == -1) {
                // searching customer failed
                System.out.print("\tInvalid name! Try again: ");
            }
            else {
                // found customer id. go to the next step
                valid_name = true;
            }
        }

        System.out.print("\t*Enter Booking Date(mm/dd/yyyy): ");
        input = in.readLine();
        while(input.length() == 0) {
            // if user didn't input something but just enter
            System.out.print("\tBooking Date cannot be null! Try again: ");
           input = in.readLine();
        }
        String bookingDate = input;

        System.out.print("\tEnter Number of booking people (if null, 1 will be inserted): ");
        input = in.readLine();
        int num_of_people = 0;
        if(input.length() == 0) {
            // if user didn't input something but just enter
            // set 1
            num_of_people = 1;
        }
        else {
            try {
                num_of_people = Integer.parseInt(input);
            }
            catch(NumberFormatException e) {
                System.out.println("Failed to get number from your input! Number of booking people will be 1..");
                num_of_people = 1;
            }
        }

        // calculate price
        // there is a room type, need to calculate how cost per each type
        int price = 0; // not yet implemented
        String roomType;
        // creates a statement object
        Statement stmt = esql._connection.createStatement();
         // issues the update instruction
        ResultSet rs =  stmt.executeQuery("SELECT roomType FROM Room Where hotelID = " + hotelID + " AND roomNo = " + RoomNo);
        // gets the result of count
        rs.next();
        roomType = rs.getString(1);
        // close the instruction
        stmt.close();

        Random r = new Random();
        switch(roomType) {
            case "Economy":
            price = r.nextInt(500) + 500; // get a random number in range [500, 999]
            break;

            case "Suite":
            price = r.nextInt(500) + 1000; // get a random number in range [1000, 1499]
            break;

            case "Deluxe":
            price = r.nextInt(500) + 1500; // get a random number in range [1500, 1999]
            break;

            default:
            System.out.println("No match! Set the price 1999...");
            price = 1999;
            break;
        }

        // make query statement
        query += (Integer.toString(new_id) + ", " + Integer.toString(customer_id) + ", " + hotelID + ", " + RoomNo + ", '" + bookingDate + "', " + Integer.toString(num_of_people) + ", " + Integer.toString(price) + ")");

        System.out.println("Query made is: " + query);
        System.out.print("Executing query...");
        esql.executeUpdate(query);
        System.out.println("Completed");
     }catch(Exception e){
        System.err.println (e.getMessage());
     }
   }//end bookRoom

   public static void assignHouseCleaningToRoom(DBProject esql){
	  // Given Staff SSN, HotelID, roomNo Assign the staff to the room 
      // Your code goes here.
      // ...
      // ...
   }//end assignHouseCleaningToRoom
   
   public static void repairRequest(DBProject esql){
	  // Given a hotelID, Staff SSN, roomNo, repairID , date create a repair request in the DB
      int new_id = esql.getNewID("Request");
      boolean description_inserted = false;
      boolean rptype_inserted = false;

      // set values and insert a new one
      try{
        String rq_query = "INSERT INTO Request (reqID, managerID, repairID, requestDate";
        String rp_query = "INSERT INTO Repair (rID, hotelID, roomNo, mCompany, repairDate";

        //get user inputs
        System.out.print("\t*Enter HotelID: ");
        String input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tHotelID cannot be null! Try again: ");
           input = in.readLine();
        }
        String hotelID = input;

        System.out.print("\t*Enter Manager's SSN: ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tSSN cannot be null! Try again: ");
           input = in.readLine();
        }
        String m_SSN = input;
        //get manager's id
        int m_id = esql.getID(2, input, "Staff");

        System.out.print("\t*Enter Room No: ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tRoom No cannot be null! Try again: ");
           input = in.readLine();
        }
        String roomNo = input;

        System.out.print("\t*Enter Repair Date(mm/dd/yyyy): ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tRepair Date cannot be null! Try again: ");
           input = in.readLine();
        }
        String rp_date = input;

        System.out.print("\t*Enter Maintenance Company ID: ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tMaintenance Company ID cannot be null! Try again: ");
           input = in.readLine();
        }
        String mc_id = input;

        System.out.print("\tEnter Description: ");
         input = in.readLine();
         String description = "";
         if(input.length() != 0) {
            // if user insert address
            description_inserted = true;
            description = input;
        }

        System.out.print("\tEnter Repair Type(Small / Medium / Large): ");
         input = in.readLine();
         String rptype = "";
         if(input.length() != 0) {
            // if user insert address
            rptype_inserted = true;
            rptype = input;
        }

        // complete repair insert query
        if(description_inserted) {
            rp_query += ", description";
        }
        if(rptype_inserted) {
            rp_query += ", repairType";
        }
        rp_query += (") VALUES (" + Integer.toString(new_id) + ", " + hotelID + ", " + roomNo + ", " + mc_id + ", '" + rp_date + "'");
        if(description_inserted) {
            rp_query += ", '" + description + "'";
        }
        if(rptype_inserted) {
            rp_query += ", '" + rptype + "'";
        }
        rp_query += ")";

        // insert into repair relation
        //System.out.println("Query made is: " + rp_query);
        System.out.print("Executing query...");
        esql.executeUpdate(rp_query);
        System.out.println("Completed");

        // complete request insert query
        DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(); // to get today's date
        if(description_inserted) {
            rq_query += ", description";
        }
        rq_query += ") VALUES (" + Integer.toString(new_id) + ", " + Integer.toString(m_id) + ", " + Integer.toString(new_id) + ", '" + dateformat.format(date) + "'";
        if(description_inserted) {
            rq_query += ", '" + description + "'";
        }
        rq_query += ")";

        // insert into request relation
        //System.out.println("Query made is: " + rq_query);
        System.out.print("Executing query...");
        esql.executeUpdate(rq_query);
        System.out.println("Completed");
    } catch(Exception e){
        System.err.println (e.getMessage());
    }
   }//end repairRequest
   
   public static void numberOfAvailableRooms(DBProject esql){
	  // Given a hotelID, get the count of rooms available 
      // Your code goes here.
      // ...
      // ...
   }//end numberOfAvailableRooms
   
   public static void numberOfBookedRooms(DBProject esql){
	  // Given a hotelID, get the count of rooms booked
      try{
        String query = "SELECT COUNT(*) as BookedRooms FROM Booking WHERE hotelID = ";

        //get user inputs
        System.out.print("\t*Enter HotelID: ");
        String input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tHotelID cannot be null! Try again: ");
           input = in.readLine();
        }
        String hotelID = input;

        DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(); // to get today's date

        query += hotelID + " AND bookingDate = '" + dateformat.format(date) + "'";
        //System.out.println("Query made is: " + query);
        int rowCount = esql.executeQuery(query);
        System.out.println("total row(s): " + rowCount);
    } catch (Exception e) {
        System.err.println (e.getMessage());
    }
   }//end numberOfBookedRooms
   
   public static void listHotelRoomBookingsForAWeek(DBProject esql){
	  // Given a hotelID, date - list all the rooms available for a week(including the input date) 
      // Your code goes here.
      // ...
      // ...
   }//end listHotelRoomBookingsForAWeek
   
   public static void topKHighestRoomPriceForADateRange(DBProject esql){
	  // List Top K Rooms with the highest price for a given date range
      try{
        //get user inputs
        System.out.print("\t*Enter Start date for search(mm/dd/yyyy): ");
        String input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tDate cannot be null! Try again: ");
           input = in.readLine();
        }
        String start_date = input;

        System.out.print("\t*Enter End date for search(mm/dd/yyyy): ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tDate cannot be null! Try again: ");
           input = in.readLine();
        }
        String end_date = input;

        System.out.print("\t*How many rows do you want to get? : ");
        input = in.readLine();
        while(input.length() == 0) {
           // if user didn't input something but just enter
           System.out.print("\tThis cannot be null! Try again: ");
           input = in.readLine();
        }
        String getting_rows_str = input;
        int getting_rows;
        // parse user inputs into integer
        try {
            getting_rows = Integer.parseInt(getting_rows_str);
        }
        catch(NumberFormatException e) {
            System.out.println("Failed to get number from your input! Number K will be 1..");
            getting_rows = 1;
        }

        String query = "SELECT * FROM Booking WHERE bookingDate >= '" + start_date + "' AND bookingDate <= '" + end_date + "' ORDER BY price DESC LIMIT " + Integer.toString(getting_rows);
        //System.out.println("Query made is: " + query);
        int rowCount = esql.executeQuery(query);
        System.out.println("total row(s): " + rowCount);
    } catch (Exception e) {
        System.err.println (e.getMessage());
    }
   }//end topKHighestRoomPriceForADateRange
   
   public static void topKHighestPriceBookingsForACustomer(DBProject esql){
	  // Given a customer Name, List Top K highest booking price for a customer 
      // Your code goes here.
      // ...
      // ...
   }//end topKHighestPriceBookingsForACustomer
   
   public static void totalCostForCustomer(DBProject esql){
	  // Given a hotelID, customer Name and date range get the total cost incurred by the customer
      // Your code goes here.
      // ...
      // ...
   }//end totalCostForCustomer
   
   public static void listRepairsMade(DBProject esql){
	  // Given a Maintenance company name list all the repairs along with repairType, hotelID and roomNo
      // Your code goes here.
      // ...
      // ...
   }//end listRepairsMade
   
   public static void topKMaintenanceCompany(DBProject esql){
	  // List Top K Maintenance Company Names based on total repair count (descending order)
      // Your code goes here.
      // ...
      // ...
   }//end topKMaintenanceCompany
   
   public static void numberOfRepairsForEachRoomPerYear(DBProject esql){
	  // Given a hotelID, roomNo, get the count of repairs per year
      // Your code goes here.
      // ...
      // ...
   }//end listRepairsMade

}//end DBProject
