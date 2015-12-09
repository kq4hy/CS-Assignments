## Car Mart Documentation

#### Problem
The problem arose when one of us was actually looking for a used car to purchase after graduating in December. We went through multiple sites to browse their inventory and visited dealerhips that specialized in selling at most 2-3 car makes which meant a lot of traveling on our part.  For online, there wasn't too many options of comparing across websites which provded extremely frustrating. All in all, the process was extremely tedious and the information was not consolidated in one place. That being said, the goal of our project is to create an unified portal where buyers can easily view all used car listings in their area and contact the buyers, negotiate price, and solidify the purchase all in one spot if they so desire. Our project also benefits sellers by providing a portal where they can make their used cars inventory seen. It benefits individual sellers by giving them the ability to put the car(s) they’re trying to sell on the same visible platform as dealerships. Overall, this will provide an avenue for people to log into one system and view all their possible options while allowing sellers to reach out to a wider population. 

#### Requirements Document
Car Mart should have the following functionality:

1. Allow for users to sign up and have their own unique account.
2. Allow for buyers to view all cars that have been put up for sale.
3. Allow for sellers to post their car and allow for buyers to purchase them.
4. Buyers should be able to see cars only within their price limit.
5. Sellers and buyers can both modify and change their profiles and respectively, inventory and carts.
6. The database must be secure and accessible through all functionality.

Data Integrity:

1. Validations are kept on the tables such as min_price for buyers can not be below than 0 nor can it be higher than max_price and simiarly, max_price will have a cap of $150,000 and can not be below than min_price.
2. There cannot be multiples of the same user with the same user name.
3. Primary keys ensure that the data stored is unique and there is no redudancy within the tables. 
4. Relationship sets are also correctly stored and only link two tables through two fields, the primary key from each of the tables.
5. The database is in third normal form (as proven below).

#### Design Process
The project was implemented as a web portal with limited but sufficient functionality. The reasoning behind this was that storing all the information on the database and retrieving it would be easiest on the computer but also, it would make more sense that comparison and serious searching would be done while sitting down in front of a computer. There was also more expertise in using PHP, HTML, MySQL to implement the entire project over mobile. Due to the lack of time, CSS was not implemented but with a little more time, the website could use Bootstrap or other free custom background/font frameworks could be used. 

Place ER Diagram below:


#### Database Schema
Due to inability of underlining in markdown, all primary keys have been listed instead.

- users(user varchar(30) primary key not null, password text not null, first_name text not null, last_name text not null, email text not null)
- buyers(user_id int primary key not null, min_price int not null, max_price int not null, completed boolean not null)
- sellers(user_id int primary key not null, dealership text not null, address text not null, completed boolean not null)
- cars(car_id int primary key not null, make text not null, model text not null, year int not null,	miles int not null, mpg_city int not null, mpg_highway int not null)
- inventory(inv_id int primary key not null, price int not null, bought boolean not null, car_condition text not null, comments text not null)
- cart(cart_id int primary key not null, current boolean not null, bought boolean not null)
- is_seller(user varchar(30) not null, user_id int not null, primary key(user, user_id), foreign key(user) references users(user) on delete cascade,	foreign key(user_id) references sellers(user_id) on delete cascade))
- is_buyer(user varchar(30) not null, user_id int not null, primary key(user, user_id), foreign key(user) references users(user) on delete cascade, foreign key(user_id) references buyers(user_id) on delete cascade)
- has(user_id int not null, cart_id int not null, primary key(user_id, cart_id), foreign key(user_id) references buyers(user_id) on delete cascade, foreign key(cart_id) references cart(cart_id) on delete cascade)
- owns(create table owns(user_id int not null, inv_id int not null, primary key (user_id, inv_id), foreign key(user_id) references sellers(user_id) on delete cascade, foreign key(inv_id) references inventory(inv_id) on delete cascade)
-contains(inv_id int not null, car_id int not null, primary key(inv_id, car_id), foreign key(inv_id) references inventory(inv_id) on delete cascade, foreign key(car_id) references cars(car_id) on delete cascade)
-has_cars(inv_id int not null, cart_id int not null, primary key(inv_id, cart_id), foreign key(inv_id) references inventory(inv_id) on delete cascade, foreign key(cart_id) references cart(cart_id) on delete cascade)

Users: user -> password, first_name, last_name, email
Buyers: user_id -> min_price, max_price, completed
Sellers: user_id -> dealership, address, completed
Cars: car_id -> make, model, year, miles, mpg_city, mpg_highway
Inventory: inv_id -> price, bought, car_condition, comments
Cart: cart_id -> current, bought

All RHS entities are dependent on themselves and all LHS entities are superkeys and the above is already the canonical form. Thus, the database is in 3NF based on the above relations. Going even further, the relationship sets that are created only contain two foreign keys, so they are also 3NF as well.  



