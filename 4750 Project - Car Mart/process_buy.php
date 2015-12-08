<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  if(isset($_GET['var'])) {
    $inv_id = $_GET['var'];

    $db = new mysqli('localhost', 'root', "", '4750_database');
    if($db -> connect_error)
      die("Connection failed: " . $db -> connect_error);

    $result = $db -> query("select car_id from cars natural join contains where inv_id = '$inv_id'");
    $arr = $result -> fetch_array();

    $db -> query("update cart set bought = '1' where cart_id = '$arr[0]'") or die("Invalid cart: " . $db -> error);
    $db -> query("update inventory set bought = '1' where inv_id = '$inv_id'") or die("Invalid has_cars: " . $db -> error);
?>

    <title>Success</title>
    <h3><center>Successful!</center></h3>
    <center>You have successfully purchased the car! Click go back to continue shopping.</br></br>
    <form action = "buyer_main.php">
      <input type = "submit" value = "Go Back">
    </form>

    <?php } ?>
