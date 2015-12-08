<title>Buyer Main Page</title>

<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  //create connection
  $db = new mysqli('localhost', 'root', "", '4750_database');

  //check connection
  if ($db -> connect_error):
    die("Connection failed: " . $db -> connect_error);
  endif;

  $result = $db -> query("select first_name, last_name from users where binary user = '$curr_user'");
  $row = $result -> fetch_array();
  echo "<h3><center>Welcome $row[0] $row[1]!</center></h3>";

  if(isset($_POST['maximum']) && isset($_POST['minimum'])) {
    $minimum = $_POST['minimum'];
    $maximum = $_POST['maximum'];
    if($minimum > $maximum) { ?>
      <title>Update Failure</title>
      <h3><center>Update Failed</center></h3>
      <center>The values inputted for minimum ($<?php echo $minimum ?>) and maximum ($<?php echo $maximum ?>) do not make sense. Please try again.
      <form action = "buyer_main.php">
      </br></br><input type = "submit" value = "Try Again"></br>
      </center></form>
    <?php } else {
      if($maximum == "" || $maximum > 150000)
        $maximum = 150000;
      $_SESSION['minimum'] = $minimum;
      $_SESSION['maximum'] = $maximum;
      $db -> query("update buyers set min_price = '$minimum', max_price = '$maximum', completed = '1' where user_id = '$curr_id'");
      header("Location: buyer_main.php");
    }
  } else {
    $completion = $db -> query("select completed, user_id, min_price, max_price from buyers natural join is_buyer where binary user = '$curr_user'");
    $row = $completion -> fetch_array();
    $_SESSION['minimum'] = $row[2];
    $_SESSION['maximum'] = $row[3];
    if($row[0] == 0) { ?>
      <center>Welcome to Car Mart! You listed yourself as a buyer. </br>
         Please fill out this following information for potential matches.</br></br>
      <form action = "buyer_main.php" method = "POST">
        If you do not have a price range, please leave the fields empty.</br></br>
        Minimum Price: $<input type = "text" name = "minimum" size = "20"></br></br>
        Maximum Price: $<input type = "text" name = "maximum" size = "20"></br></br>
        <input type = "submit" value = "Submit Information"></br>
      </form>
    <?php } else {
      $result = $db -> query("select * from has_cars natural join has natural join cart where user_id = '$curr_id'") or die("Invalid: " . $db -> error);
      $num_rows = $result -> num_rows;
      create_table($num_rows); ?>

      <h3><center>Car Inventory</center></h3>
      <table width = 85% id = "table" align = center border = "1">
        <tr align = center>
          <th width = 20%>Car Information</th>
          <th width = 15%>Condition</th>
          <th width = 10%>Price</th>
          <th width = 20%>Seller</th>
          <th width = 20%>Bought</th>
        </tr>

        <?php if(isset($_SESSION['maximum']) && isset($_SESSION['minimum'])) {
          $max = $_SESSION['maximum'];
          $min = $_SESSION['minimum'];
          $result = $db -> query("select distinct inv_id, make, model, year, car_condition, price, first_name, last_name, inventory.bought
          from inventory natural join owns natural join users natural join sellers natural join cars natural join contains
          natural join is_seller where price >= '$min' and price <= '$max'") or die("Invalid: " . $db -> error);
          $num_rows = $result -> num_rows;
        	$row = $result -> fetch_array();
        	$num_fields = sizeof($row);
        	for ($row_num = 0; $row_num < $num_rows; $row_num++) {
        		reset($row); ?>
        		<tr align = 'center'>
              <td><a "location . href = 'add_car.php?var=<?php echo $row[0] ?>'"><?php echo $row[1]." ".$row[2]." ".$row[3] ?></td>
              <td><?php echo $row[4] ?></td>
              <td>$<?php echo $row[5] ?></td>
              <td><?php echo $row[6]." ".$row[7] ?></td>
        		  <?php if($row[8] == 0)
                echo "<td>Not yet bought</td>";
              else if($row[8] == 1)
                echo "<td>Sold!</td>";
        	  echo "</tr>";
        		$row = $result -> fetch_array();
          } ?>
          </table></br></br>
        <?php }
      } ?>

  <form action = "login.php"><center>
    <input type = "submit" value = "Log Out">
  </center></form>

  <?php } ?>

<?php
  function create_table($rows) { ?>
    <center>As a buyer, you can perform the below actions!</center></br>
    <title>Seller Main Page</title>
    <table width = 50% id = "table" align = center >
      <tr align = center>
        <th width = 25%><a "location . href = 'profile.php?var=buyer'">Profile</th>
        <th width = 25%><a "location . href = 'cart.php'">Cart(<?php echo $rows ?>)</th>
      </tr>
    </table></br>
  <?php }
?>
