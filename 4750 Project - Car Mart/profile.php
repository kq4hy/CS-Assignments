<title>User Profile</title>
<h3><center>User Profile</center></h3>
<center>Below is your information. Feel free to revise it as you see fit!</center></br>

<?php
  session_start();
  $curr_user = $_SESSION['user'];
  $curr_id = $_SESSION['user_id'];

  if(isset($_SESSION['user']) && isset($_GET['var'])) {
    $user_type = $_GET['var'];

    get_info($curr_user, $curr_id, $user_type);
  }

  function get_info($user, $id, $type) {
    $db = new mysqli('localhost', 'root', "", '4750_database');
    if ($db -> connect_error)
      die("Connection failed: " . $db -> connect_error);
    $user_fields = $db -> query("select first_name, last_name, email from users where user = '$user'") or die("Invalid: " . $db -> error);
    $user_arr = $user_fields -> fetch_array();  ?>
    <center><form action = "process_profile.php" method = "POST">
      First Name: <input type = "text" name = "first_name" value = <?php echo $user_arr[0] ?>></br></br>
      Last Name: <input type = "text" name = "last_name" value = <?php echo $user_arr[1] ?>></br></br>
      Email: <input type = "text" name = "email" value = <?php echo $user_arr[2] ?>></br></br>

      <?php if($type == "buyer") {
        $buyer_fields = $db -> query("select min_price, max_price from buyers where user_id = '$id'") or die("Invalid: " . $db -> error);
        $buyer_arr = $buyer_fields -> fetch_array(); ?>
        Minimum Price: <input type = "text" name = "minimum" value = "<?php echo $buyer_arr[0] ?>"></br></br>
        Maximum Price: <input type = "text" name = "maximum" value = "<?php echo $buyer_arr[1] ?>"></br></br>

      <?php } else if($type == "seller") {
        $seller_fields = $db -> query("select dealership, address from sellers where user_id = '$id'") or die("Invalid: " . $db -> error);
        $seller_arr = $seller_fields -> fetch_array();?>
        Dealership: <input type = "text" name = "dealership" value = "<?php echo $seller_arr[0] ?>"></br></br>
        Address: <input type = "text" name = "address" value = "<?php echo $seller_arr[1] ?>"></br></br>
        <?php } ?>
        <input type = "submit" value = "Change Profile">
      </form>

      <?php if($type == "buyer") { ?>
        <form action = "buyer_main.php">
          <input type = "submit" value = "Go Back">
        </form>

      <?php } else if($type == "seller") { ?>
        <form action = "seller_main.php">
          <input type = "submit" value = "Go Back">
        </form>
      <?php }
    }
?>
