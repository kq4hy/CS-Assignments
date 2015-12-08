<?php
  session_start();
  $type = $_SESSION['type'];
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  $db = new mysqli('localhost', 'root', "", '4750_database');
  if ($db -> connect_error)
    die("Connection failed: " . $db -> connect_error);

  $first_name = $_POST['first_name'];
  $last_name = $_POST['last_name'];
  $email = $_POST['email'];
  $db -> query("update users set first_name = '$first_name', last_name = '$last_name',
  email = '$email' where user = '$curr_user'") or die("Invalid: " . $db -> error);

  if($type == "buyer") {
    $minimum = $_POST['minimum'];
    $maximum = $_POST['maximum'];
    $db -> query("update buyers set min_price = '$minimum', max_price = '$maximum' where user_id = '$curr_id'") or die("Invalid: " . $db -> error);
    header("Location: buyer_main.php");

  } else if($type == "seller") {
    $dealership = $_POST['dealership'];
    $address = $_POST['address'];
    $db -> query("update sellers set dealership = '$dealership', address = '$address' where user_id = '$curr_id'") or die("Invalid: " . $db -> error);
    header("Location: seller_main.php");
  }
?>
