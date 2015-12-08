<title>Seller Main Page</title>

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

  if(isset($_POST['dealership']) && isset($_POST['address'])) {
    $dealership = $_POST['dealership'];
    $address = $_POST['address'];
    $db -> query("update sellers set dealership = '$dealership', address = '$address', completed = '1' where user_id = '$curr_id'");
    create_table();
  } else {
    $completion = $db -> query("select completed, user_id from sellers natural join is_seller where binary user = '$curr_user'");
    $row = $completion -> fetch_array();
    if($row[0] == 0) { ?>
      <center>Welcome to Car Mart! Please fill out this following information for future customers.</br></br>
      <form action = "seller_main.php" method = "POST">
        If this does not apply to you, please leave empty or write "N/A".</br></br>
        Dealership: <input type = "text" name = "dealership" size = "20"></br></br>
        Address: <input type = "text" name = "address" size = "20"></br></br>
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
    <center>As a seller, you can perform the below actions!</center></br>
    <title>Seller Main Page</title>
    <table width = 50% id = "table" align = center >
      <tr align = center>
        <th width = 25%><a "location . href = 'sell_page.php'">Sell</th>
        <th width = 25%><a "location . href = 'profile.php?var=seller'">Profile</th>
        <th width = 25%><a "location . href = 'inventory.php'">Inventory</th>
      </tr>
    </table></br></br>
  <?php }
?>
