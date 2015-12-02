<?php

$servername = "stardock.cs.virginia.edu";
$username = "cs4750sp5pe";
$password = "fall2015";
$db = "cs4750sp5pe";

// Create connection
$conn = new mysqli($servername, $username, $password, $db);

// Check connection
if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
} else{
	echo "Connection successful" . "<br>";
}

$sql = "DROP TABLE IF EXISTS student;
        CREATE TABLE student (
        id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	firstname VARCHAR(30) NOT NULL,
        lastname  VARCHAR(30) NOT NULL,
        year INTEGER NOT NULL,
        email VARCHAR(30));";

/*
$sql = "SELECT * FROM student;";
$result = $conn->query($sql);

while ($row = $result-> fetch_assoc() ) {
	echo "id: " . $row["id"] . "<br>";
}
*/

if ($conn->query($sql) == TRUE) {
	echo "Table student created";
} else {
	echo "Error creating table" . $conn->error;
}


$conn->close();

?>