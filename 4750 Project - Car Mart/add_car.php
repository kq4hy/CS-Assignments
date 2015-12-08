<title>Car Information</title>
</br></br>
<h3><center>Car Information</center></h3>
<center>Please view the information below regarding the car you just clicked.</br>
Feel free to contact the seller or add this car to your cart. </br></br>

<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  if(isset($_GET['var'])) {
    $inv_id = $_GET['var'];

    $db = new mysqli('localhost', 'root', "", '4750_database');
    if($db -> connect_error)
      die("Connection failed: " . $db -> connect_error);

    $result = $db -> query("select distinct make, model, year, miles, mpg_city, mpg_highway, car_condition, price,
    comments, first_name, last_name, email, dealership, address, bought from inventory natural join owns natural join users
    natural join sellers natural join cars natural join contains natural join is_seller where inv_id = '$inv_id'") or die("Invalid: " . $db -> error);
    $arr = $result -> fetch_array();
    ?>

    Car: <?php echo $arr[0] . " " . $arr[1] . " " . $arr[2] ?></br></br>
    Total Mileage: <?php echo $arr[3] ?></br></br>
    Miles Per Gallon (mpg) in City: <?php echo $arr[4] ?> miles</br></br>
    Miles Per Gallon (mpg) on Highway: <?php echo $arr[5] ?> miles</br></br>
    Condition: <?php echo $arr[6] ?></br></br>
    Price: $<?php echo $arr[7] ?></br></br>
    Additional Comments: <?php echo $arr[8] ?></br></br>

    <h3><center>Seller Information</center></h3>
    Name: <?php echo $arr[9] . " " . $arr[10] ?></br></br>
    Email: <?php echo $arr[11] ?></br></br>
    Dealership: <?php echo $arr[12] ?></br></br>
    Address: <?php echo $arr[13] ?></br></br>

    <?php if($arr[14] == 0) { ?>
      <a "location . href = 'process_add.php?var= <?php echo $inv_id ?>'">Add to Cart</br></br>
    <?php } else if($arr[14] == 1) { ?>
      This car has already been purchased! Click back to continue shopping.</br></br>
    <?php } ?>

    <form action = "buyer_main.php">
      <input type = "submit" value = "Go Back">
    </form></center>

  <?php } ?>
