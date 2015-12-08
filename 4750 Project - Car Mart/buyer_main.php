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
      $db -> query("update buyers set min_price = '$minimum', max_price = '$maximum', completed = '1' where user_id = '$curr_id'");
      create_table();
    }
  } else {
    $completion = $db -> query("select completed, user_id from buyers natural join is_buyer where binary user = '$curr_user'");
    $row = $completion -> fetch_array();
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
      create_table();
    }
  } ?>
  
  <form action = "login.php"><center>
    <input type = "submit" value = "Log Out">
  </center></form>

<?php
  function create_table() { ?>
    <center>As a buyer, you can perform the below actions!</center></br>
    <title>Seller Main Page</title>
    <table width = 50% id = "table" align = center >
      <tr align = center>
        <th width = 25%><a "location . href = 'buy_page.php'">Buy</th>
        <th width = 25%><a "location . href = 'profile.php?var=buyer'">Profile</th>
        <th width = 25%><a "location . href = 'cart.php'">Cart</th>
      </tr>
    </table></br>
  <?php }
?>
