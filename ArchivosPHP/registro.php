 <?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xeverhorst001"; #el usuario para esa base de datos
$DB_PASS="*EyzCAv7UH"; #la clave para ese usuario
$DB_DATABASE="Xeverhorst001_tareamanager"; #la base de datos a la que hay que conectarse
$USER=$_POST["user"];
$CONT=$_POST["pass"];

// Create connection
$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
$fin="false";
// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$sql = "INSERT INTO usuario (usuario, contra) VALUES ('$USER', '$CONT')";

if ($conn->query($sql) === TRUE) {
  $fin="true";
} else {
	$fin="Error: " . $sql . "<br>" . $conn->error;
  
}
echo $fin;

$conn->close();
?> 
