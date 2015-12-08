<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  $db = new mysqli('localhost', 'root', "", '4750_database');
  if($db -> connect_error)
    die("Connection failed: " . $db -> connect_error);

  $make = $_POST['make'];
  $model = $_POST['model'];
  $year = $_POST['year'];
  $miles = $_POST['mileage'];
  $mpg_city = $_POST['mpg_city'];
  $mpg_highway = $_POST['mpg_highway'];
  $condition = $_POST['condition'];
  $price = $_POST['price'];
  $comments = $_POST['comments'];

  if($_POST['sell'] == "Update" && isset($_SESSION['inv_id'])) {
    $inv_id = $_SESSION['inv_id'];
    $result = $db -> query("select car_id from cars natural join contains where inv_id = '$inv_id'");
    $arr = $result -> fetch_array();
    $db -> query("update cars set make = '$make', model = '$model', year = '$year', miles = '$miles', mpg_city = '$mpg_city',
    mpg_highway = '$mpg_highway' where car_id = '$arr[0]'") or die("Invalid: " . $db -> error);
    $db -> query("update inventory set price = '$price', car_condition = '$condition', comments = '$comments'
    where inv_id = '$inv_id'") or die("Invalid: " . $db -> error); ?>

    <h3><center>Successful Update!</center></h3>
    <center>You have successfully edited your sale! Click go back to view your listings.</br></br>
  <?php } else if($_POST['sell'] == "Post Listing") {
    $result = $db -> query("select * from cars");
    $car_rows = $result -> num_rows;
    $car_rows++;

    $result = $db -> query("select * from inventory");
    $inven_rows = $result -> num_rows;
    $inven_rows++;

    $db -> query("insert into cars values('$car_rows', '$make', '$model', '$year', '$miles', '$mpg_city', '$mpg_highway')") or die("Invalid: " . $db -> error);
    $db -> query("insert into inventory values('$inven_rows', '$price', '0', '$condition', '$comments')") or die("Invalid: " . $db -> error);
    $db -> query("insert into contains values('$inven_rows', '$car_rows')") or die("Invalid: " . $db -> error);
    $db -> query("insert into owns values('$curr_id', '$inven_rows')") or die("Invalid: " . $db -> error); ?>

    <h3><center>Successful Listing!</center></h3>
    <center>You have successfully posted your car! Click go back to view your listings.</br></br>
  <?php }
?>

<title>Success</title>
<form action = "seller_main.php">
  <input type = "submit" value = "Go Back">
</form>
