CREATE INDEX Room_roomNo_index
ON Room (roomNo);

CREATE INDEX Room_hotelID_index
ON Room (hotelID);

CREATE INDEX Booking_roomNo_index
ON Booking (roomNo);

CREATE INDEX Booking_bookingDate_index
ON Booking (bookingDate);

CREATE INDEX Booking_price_index
ON Booking (price);

CREATE INDEX Booking_hotelID_index
ON Booking (hotelID);

CREATE INDEX Booking_customer_index
ON Booking (customer);

CREATE INDEX Booking_bID_index
ON Booking (bID);

CREATE INDEX Customer_customerID_index
ON Customer (customerID);

CREATE INDEX Customer_fname_index
ON Customer (fname);

CREATE INDEX Customer_lname_index
ON Customer (lname);

CREATE INDEX Customer_Address_index
ON Customer (Address);

CREATE INDEX Customer_phNo_index
ON Customer (phNo);

CREATE INDEX Customer_DOB_index
ON Customer (DOB);

CREATE INDEX Customer_gender_index
ON Customer (gender);

CREATE INDEX Staff_employerID_index
ON Staff (employerID);

CREATE INDEX Request_repairID_index
ON Request (repairID);

CREATE INDEX Request_requestDate_index
ON Request (requestDate);

CREATE INDEX Repair_roomNo_index
ON Repair (roomNo);

CREATE INDEX Repair_rID_index
ON Repair (rID);

CREATE INDEX Repair_hotelID_index
ON Repair (hotelID);

CREATE INDEX Repair_repairType_index
ON Repair (repairType);

CREATE INDEX Repair_mCompany_index
ON Repair (mCompany);

CREATE INDEX MaintenanceCompany_name_index
ON MaintenanceCompany (name);

CREATE INDEX MaintenanceCompany_cmpID_index
ON MaintenanceCompany (cmpID);





