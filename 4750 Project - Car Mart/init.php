<?php
	//create connection
	$db = new mysqli('localhost', 'root', "", '4750_database');

	//check connection
	if ($db -> connect_error):
		die("Connection failed: " . $db -> connect_error);
	endif;

	$db -> query("drop table if exists is_buyer") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists is_seller") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists owns") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists has") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists contains") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists has_cars") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists users") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists sellers") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists buyers") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists cars") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists inventory") or die("Invalid: " . $db -> error);
	$db -> query("drop table if exists cart") or die("Invalid: " . $db -> error);

	//create USERS table
	$db -> query("create table users(user varchar(30) primary key not null, password text not null,
	first_name text not null, last_name text not null, email text not null)") or die("Invalid users: " . $db -> error);

	//create BUYERS table
	$db -> query("create table buyers(user_id int primary key not null,
	min_price int not null, max_price int not null, completed boolean not null)") or die("Invalid buyers: " . $db -> error);

	//create SELLERS table
	$db -> query("create table sellers(user_id int primary key not null,
	dealership text not null, address text not null, completed boolean not null)") or die("Invalid sellers: " . $db -> error);

	//create CARS table
	$db -> query("create table cars(car_id int primary key not null, make text not null,
	model text not null, year int not null,	miles int not null, mpg_city int not null,
	mpg_highway int not null)") or die("Invalid cars: " . $db -> error);

	//create INVENTORY table
	$db -> query("create table inventory(inv_id int primary key not null, price int not null,
	bought boolean not null, car_condition text not null, comments text not null)") or die("Invalid inventory: " . $db -> error);

	//create CART table
	$db -> query("create table cart(cart_id int primary key not null, current boolean not null,
	bought boolean not null)") or die("Invalid cart: " . $db -> error);

	//create IS_BUYER table
	$db -> query("create table is_buyer(user varchar(30) not null, user_id int not null,
	primary key(user, user_id), foreign key(user) references users(user) on delete cascade,
	foreign key(user_id) references buyers(user_id) on delete cascade) engine=innodb") or die("Invalid is_buyer: " . $db -> error);

	//create IS_SELLER table
	$db -> query("create table is_seller(user varchar(30) not null, user_id int not null,
	primary key(user, user_id), foreign key(user) references users(user) on delete cascade,
	foreign key(user_id) references sellers(user_id) on delete cascade) engine=innodb") or die("Invalid is_seller: " . $db -> error);

	//create OWNS table
	$db -> query("create table owns(user_id int not null, inv_id int not null,
	primary key (user_id, inv_id), foreign key(user_id) references sellers(user_id) on delete cascade,
	foreign key(inv_id) references inventory(inv_id) on delete cascade) engine=innodb") or die("Invalid owns: " . $db -> error);

	//create HAS table
	$db -> query("create table has(user_id int not null, cart_id int not null,
	primary key(user_id, cart_id), foreign key(user_id) references buyers(user_id) on delete cascade,
	foreign key(cart_id) references cart(cart_id) on delete cascade) engine=innodb") or die("Invalid has: " . $db -> error);

	//create CONTAINS table
	$db -> query("create table contains(inv_id int not null, car_id int not null,
	primary key(inv_id, car_id), foreign key(inv_id) references inventory(inv_id) on delete cascade,
	foreign key(car_id) references cars(car_id) on delete cascade) engine=innodb") or die("Invalid has: " . $db -> error);

	//create HAS_CARS table
	$db -> query("create table has_cars(inv_id int not null, cart_id int not null,
	primary key(inv_id, cart_id), foreign key(inv_id) references inventory(inv_id) on delete cascade,
	foreign key(cart_id) references cart(cart_id) on delete cascade) engine=innodb") or die("Invalid has: " . $db -> error);

	$user_file = file('users.txt');
	foreach($user_file as $str) {
		$str = rtrim($str);
		$chunks = explode("#", $str);
		$row_info = array();
		foreach($chunks as $entry)
			$row_info[] = str_replace("'", "''", $entry);
		$hashed = hash('sha256', $row_info[1]);
		$db -> query("insert into users values('$row_info[0]', '$hashed', '$row_info[2]', '$row_info[3]', '$row_info[4]')") or die ("Invalid: " . $db -> error);
		if($row_info[5] == "buyer") {
			$db -> query("insert into buyers values('$row_info[6]', '$row_info[7]', '$row_info[8]', '0')") or die("Invalid: " . $db -> error);
			$db -> query("insert into is_buyer values('$row_info[0]', '$row_info[6]')") or die("Invalid: " . $db -> error);
		} else if($row_info[5] == "seller") {
			$db -> query("insert into sellers values('$row_info[6]', '$row_info[7]', '$row_info[8]', '0')") or die("Invalid: " . $db -> error);
			$db -> query("insert into is_seller values('$row_info[0]', '$row_info[6]')") or die("Invalid: " . $db -> error);
		}
	}


	$db -> close();
	header("Location: login.php");
?>
