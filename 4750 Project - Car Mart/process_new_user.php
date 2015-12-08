<?php
	if(!empty($_POST['user']) && !empty($_POST['password']) && !empty($_POST['conf_pass']) && !empty($_POST['first_name']) && !empty($_POST['last_name']) && !empty($_POST['email'])) {
		//create connection
		$db = new mysqli('localhost', 'root', "", '4750_database');

		//check connection
		if ($db -> connect_error):
			die("Connection failed: " . $db -> connect_error);
		endif;

		$user = $_POST['user'];
		$pass = $_POST['password'];
		$conf_pass = $_POST['conf_pass'];
		$first_name = $_POST['first_name'];
		$last_name = $_POST['last_name'];
		$email = $_POST['email'];
		$type = $_POST['type'];
		$result = $db -> query("select * from users where binary user = '$user'");
		//check if there is any other person with the same user name
		if($result -> num_rows == 0) {
			if($pass == $conf_pass) { ?>
				<!-- check if the two passwords entered match up -->
				<title>Register Success</title>
				<h3><center>Register Success</center></h3>
				<?php echo "<center>Thank you! You have successfully been added to the database. Please click below to login.</center>";

				//hash the password
				$secure_pass = hash('sha256', $pass);

				//add the information provided into the database so that they can log on later
				$db -> query("insert into users values('$user', '$secure_pass', '$first_name', '$last_name', '$email')");
				if($type == "buyer") {
					$result = $db -> query("select * from buyers");
					$rows = $result -> num_rows;
					$rows++;
					$db -> query("insert into buyers values('$rows', -10, 0, 0)") or ("Invalid: " . $db -> error);
					$db -> query("insert into is_buyer values('$user', '$rows')") or ("Invalid: " . $db -> error);
				} else if($type == "seller") {
					$result = $db -> query("select * from sellers");
					$rows = $result -> num_rows;
					$rows++;
					$db -> query("insert into sellers values('$rows', 'DNE', ' ', 0)") or ("Invalid: " . $db -> error);
					$db -> query("insert into is_seller values('$user', '$rows')") or ("Invalid: " . $db -> error);
				}

				?>
				<form action = login.php></br>
					<center><input type = "submit" value = "Login"></center></br>
				</form>
			<?php } else { ?>
				<!-- if they don't, report that they don't, and ask them to re-submit their registration -->
				<title>Register Fail</title>
				<h3><center>Register Failure</center></h3>
				<center>The two passwords you entered did not match up. Please try again.</center>
				<form action = "create_user.html"></br>
					<center><input type = "submit" value = "Try Again"></center></br>
				</form>
				<?php }
			} else { ?>
				<!-- //if there is already someone in the database with the same user -->
				<title>Register Fail</title>
				<h3><center>Register Failure</center></h3>
				<center>That user name has already been taken. Please try again with a different user name.</center>
				<form action = "create_user.html"></br>
					<center><input type = "submit" value = "Try Again"></center></br>
				</form>
			<?php }

	 	} else { ?>
		<!-- if not, then report back to them that not all fields were completed and return back to register screen -->
		<title>Register Fail</title>
		<center><h3>Register Failure</h3></center>
		<center>You did not enter values for all fields. Please try again.
		<form action = "create_user.html"></br>
			<input type = "submit" value = "Try Again"></br></center>
		</form>
	<?php	} ?>
