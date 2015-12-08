<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  if(isset($_GET['var'])) {
    $inv_id = $_GET['var'];

    $db = new mysqli('localhost', 'root', "", '4750_database');
    if($db -> connect_error)
      die("Connection failed: " . $db -> connect_error);

    $result = $db -> query("select * from cart");
    $cart_rows = $result -> num_rows;
    $cart_rows++;

    $result = $db -> query("select * from inventory");
    $inven_rows = $result -> num_rows;
    $inven_rows++;

    $db -> query("insert into cart values('$cart_rows', '1', '0')") or die("Invalid cart: " . $db -> error);
    $db -> query("insert into has_cars values('$inv_id', '$cart_rows')") or die("Invalid has_cars: " . $db -> error);
    $db -> query("insert into has values('$curr_id', '$cart_rows')") or die("Invalid owns: " . $db -> error);

?>

    <title>Success</title>
    <h3><center>Successful!</center></h3>
    <center>You have successfully added to your cart! Click go back to continue shopping.</br></br>
    <form action = "buyer_main.php">
      <input type = "submit" value = "Go Back">
    </form>

    <?php } ?>
