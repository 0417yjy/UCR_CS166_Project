# CS 166 Databases Project phase 3

## Folder structure 

* data : holds the necessary data files, these are used by create.sql to insert data into the tables 
* java : holds DBproject.java, Boiler plate code 
	* compile.sh, run this .sh file to start java program
       * pg73jdbc3.jar, jar file used by DBproject.java 
* postgresql : holds startPostgreSQL.sh,createPostgreDB.sh,stopPostgreDB.sh files 
* sql : holds create.sql. This file holds SQL Statements to create appropriate tables and data in DB

## Assumptions

* When user ask for available room, he/she is asking about current date.


## Instructions

1. Run postgresql .sh files to setup your DB
2. Copy data file to DB temp folder before you run create.sql, else the data will not be inserting in the tables.
3. Run compile.sh, to start your java program

## Grades

* Documentation of the project including details about your assumptions (10%).
* Implementation of SQL queries in the Client Application (30%).
* Physical DB Design (DB performance tuning indexes) (10%).
* Extra credit for good GUI design and interface, any dataset or schema changes/extensions, etc. (20%).

## Requirement Analysis

* **Hotel Management**:
	* Given a hotel ID, list a given room’s bookings for the week.
	* For each hotel ID, get highest price among all booked rooms for a given data range.
	* Given a hotel ID and a date, get (1) the number of rooms still available and (2) number of rooms booked
	* Given a hotel ID and date, get a list of customers who made bookings for that date.
	* Given a booking ID, retrieve information about the customer (First & Last Name, Gender, Date of birth, Address) who made the booking.
	* Given a hotel ID and customer ID, get the total cost incurred by the customer for a given data range.
* **Hotel Staff**:
	* Given a Hotel ID, list all details pertaining to staff, including their positions/roles (Hotel Managers, Receptionists, House cleaning, etc.) who are employed by that hotel.
	* Hotel Managers may make maintenance/room repair requests, which will be handled by a maintenance company. The maintenance company must be certified to handle that specific type of repair. Given a manager ID list the hotel ID, room number and date of request.
	* Given a hotel ID and House cleaning staff ID list all the rooms he/she is assigned to.
* **Customers**:
	* Given customer ID, list all the rooms previously booked by that customer in all the hotels.
	*Given a price and a data range, list all the available rooms in all hotels for that date range, and price at or below the specified price.
	* Given a customer ID give the hotel ID where the per-day cost incurred by that customer was the highest.
* **Maintenance Companies**:
	* Given a maintenance company ID, list the type of repair, the hotel, and the room number for all repairs made by that company.
	* For a given date range, list all the requests received by the maintenance company from a particular hotel manager ID.


## Assumptions

* When user ask for available room, he/she is asking about current date.


## TEAM
* Jongyeon Yoon
* Kai Wen Tsai



