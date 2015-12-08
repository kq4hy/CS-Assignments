<title>Seller Main Page</title>
</br></br>

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
    header("Location: seller_main.php");
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
      create_table(); ?>

      <h3><center>Car Inventory</center></h3>
      <table width = 85% id = "table" align = center border = "1">
        <tr align = center>
          <th width = 10%>ID</th>
          <th width = 30%>Price</th>
          <th width = 30%>Condition</th>
          <th width = 30%>Bought</th>
        </tr>

        <?php
        $result = $db -> query("select inv_id, price, car_condition,
        bought from inventory natural join owns where user_id = '$curr_id'") or die("Invalid: " . $db -> error);
        $num_rows = $result -> num_rows;
      	$row = $result -> fetch_array();
      	$num_fields = sizeof($row);
      	for ($row_num = 0; $row_num < $num_rows; $row_num++) {
      		reset($row); ?>
      		<tr align = 'center'>
            <td><a "location . href = 'edit_car.php?var=<?php echo $row[0] ?>'"><?php echo $row[0] ?></td>
            <td>$<?php echo $row[1] ?></td>
            <td><?php echo $row[2] ?></td>
      		  <?php if($row[3] == 0)
              echo "<td>Not yet bought</td>";
            else if($row[3] == 1)
              echo "<td>Sold!</td>";
      	  echo "</tr>";
      		$row = $result -> fetch_array();
        } ?>
      </table></br></br>
      <?php } ?>

    <form action = "login.php"><center>
      <input type = "submit" value = "Log Out">
    </center></form>
    <?php } ?>

<?php
  function create_table() { ?>
    <center>As a seller, you can perform the below actions!</center></br>
    <title>Seller Main Page</title>
    <table width = 50% id = "table" align = center >
      <tr align = center>
        <th width = 25%><a "location . href = 'profile.php?var=seller'">Profile</th>
        <th width = 25%><a "location . href = 'sell_page.html'">Sell</th>
      </tr>
    </table>
  <?php }
?>
