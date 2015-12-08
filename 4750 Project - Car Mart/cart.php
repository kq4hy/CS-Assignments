<title>Cart</title>
</br></br>
<h3><center>Cart Information</center></h3>
<center>Below contains information regarding past purchases and your current cart.</br>
You can purchase a car from here!</br></br>

<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  $db = new mysqli('localhost', 'root', "", '4750_database');
  if($db -> connect_error)
    die("Connection failed: " . $db -> connect_error);

    $result = $db -> query("select distinct inv_id, make, model, year, price, bought
    from inventory natural join buyers natural join has natural join cars natural join contains
    natural join has_cars where user_id = '$curr_id'") or die("Invalid: " . $db -> error);
  $num_rows = $result -> num_rows;
  if($num_rows == 0) { ?>
    Your cart is empty right now. Press go back to continue looking at listings! </br></br>
  <?php } else { ?>
    <h3><center>Car Inventory</center></h3>
    <table width = 85% id = "table" align = center border = "1">
      <tr align = center>
        <th width = 20%>Car Information</th>
        <th width = 10%>Price</th>
        <th width = 20%>Bought</th>
      </tr>

      <?php
      $row = $result -> fetch_array();
      $num_fields = sizeof($row);
      for ($row_num = 0; $row_num < $num_rows; $row_num++) {
        reset($row); ?>
        <tr align = 'center'>
          <td><a "location . href = 'buy_car.php?var=<?php echo $row[0] ?>'"><?php echo $row[1]." ".$row[2]." ".$row[3] ?></td>
          <td>$<?php echo $row[4] ?></td>
          <?php if($row[5] == 0)
            echo "<td>In Cart</td>";
          else if($row[5] == 1)
            echo "<td>Bought!</td>";
        echo "</tr>";
        $row = $result -> fetch_array();
      } ?>
      </table></br></br>
  <?php }
?>

<form action = "buyer_main.php">
  <input type = "submit" value = "Go Back">
</form></center>
