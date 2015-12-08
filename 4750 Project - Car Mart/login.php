<?php
  session_start();
  if(isset($_SESSION['user']))
    session_destroy();
?>

<title>Log In</title>
</br></br>
<h3><center>Car Mart</center></h3>
<center>Please enter your user name and password. </br></br>
  <form action = "process_login.php" method = "POST">
    User Name: </br><input type = "text" name = "user" size = "20"></br></br>
    Password: </br><input type = "password" name = "password" size = "20"></br></br>
    <input type = "submit" value = "Log In">
  </form>
Or create a new account. </br></br>
<form action = "create_user.html">
  <input type = "submit" value = "Sign Up">
</form>
