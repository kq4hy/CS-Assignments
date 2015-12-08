<title>Edit Car</title>
<h3><center>Edit Car Information</center></h3>
<center>Below is the information associated with this car. Feel free to revise it as you see fit!</center></br>

<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  if(isset($_GET['var'])) {
    $inv_id = $_GET['var'];
    $_SESSION['inv_id'] =  $inv_id;
    $db = new mysqli('localhost', 'root', "", '4750_database');
    if ($db -> connect_error)
      die("Connection failed: " . $db -> connect_error);

    $result = $db -> query("select make, model, year, miles, mpg_city, mpg_highway, price, car_condition, comments
    from inventory natural join contains natural join cars where inv_id = '$inv_id'") or die("Invalid: " . $db -> error);
    $arr = $result -> fetch_array(); ?>
    <center>
    <form action = "process_sell.php" method = "POST">
      Make: <input type = "text" name = "make" value = "<?php echo $arr[0] ?>" size = "20"></br></br>
      Model: <input type = "text" name = "model" value = "<?php echo $arr[1] ?>" size = "20"></br></br>
      Year: <input type = "text" name = "year" value = "<?php echo $arr[2] ?>" size = "20"></br></br>
      Total Mileage: <input type = "text" name = "mileage"  value = "<?php echo $arr[3] ?>" size = "10">miles</br></br>
      Miles Per Gallon (mpg) in City: <input type = "text" name = "mpg_city"  value = "<?php echo $arr[4] ?>" size = "10">miles</br></br>
      Miles Per Gallon (mpg) on Highway: <input type = "text" name = "mpg_highway"  value = "<?php echo $arr[5] ?>" size = "10">miles</br></br>
      Condition: <select name = "condition">
        <option value = "Poor">Poor</option>
        <option value = "Okay">Okay</option>
        <option value = "Good">Good</option>
        <option value = "Excellent">Excellent</option>
      </select></br></br>
      Price: $<input type = "text" name = "price"  value = "<?php echo $arr[6] ?>" size = "10"></br></br>
      Additional Comments: </br><textarea name = "comments" cols = "50" rows = "5"><?php echo $arr[8] ?></textarea></br></br>
      <input type = "submit" name = "sell" value = "Update">
    </form>
  <?php } ?>

<form action = "seller_main.php">
  <input type = "submit" value = "Go Back">
</form></center>
