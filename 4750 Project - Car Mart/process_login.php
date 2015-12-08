<?php
  session_start();
  if(isset($_POST['user']) && isset($_POST['password'])) {
    $input_user = $_POST['user'];
    $pass = rtrim($_POST['password']);
    $input_pass = str_replace("'", "''", $pass);
    $hashed = hash('sha256', $input_pass);

    //create connection
  	$db = new mysqli('localhost', 'root', "", '4750_database');

  	//check connection
  	if ($db -> connect_error):
  		die("Connection failed: " . $db -> connect_error);
  	endif;

    $result = $db -> query("select * from users where binary user = '$input_user' and password = '$hashed'");
    if($result -> num_rows == 1) {
      $_SESSION['user'] = $input_user;
      $buyer_result = $db -> query("select user_id from is_buyer where binary user = '$input_user'");
      $seller_result = $db -> query("select user_id from is_seller where binary user = '$input_user'");
      if($buyer_result -> num_rows == 1) {
        $arr = $buyer_result -> fetch_array();
        $_SESSION['user_id'] = $arr[0];
        $_SESSION['type'] = "buyer";
        header("Location: buyer_main.php");
      }
      else if($seller_result -> num_rows == 1) {
        $arr = $seller_result -> fetch_array();
        $_SESSION['user_id'] = $arr[0];
        $_SESSION['type'] = "seller";
        header("Location: seller_main.php");
      }
    } else { ?>
      <title>Log In Fail</title>
      <h3><center>Log In Failure</center></h3>
      <?php if($input_user == "" || $input_pass == "") {
				//check if no username or no password was inputted
				echo "<center>You did not enter values for all fields. Please try again.</center>";
			} else {
				//check if username was inputted but the password was wrong
				echo "<center>Invalid combination! Please try again.</center>";
			} ?>
			<form action = login.php></br>
			<center><input type = "submit" value = "Try Again"></center></br>
			</form>
    <?php }
  } else {
    header("Location: login.php");
  }
?>
