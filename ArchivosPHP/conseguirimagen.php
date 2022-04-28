<?php
$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xeverhorst001"; #el usuario para esa base de datos
$DB_PASS="*EyzCAv7UH"; #la clave para ese usuario
$DB_DATABASE="Xeverhorst001_tareamanager"; #la base de datos a la que hay que conectarse

$tit = $_POST['titulo'];

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno($con)) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	echo "false";
	exit();
}

$comprobacion="SELECT * FROM imagenes WHERE titulo='$tit'";
$result= mysqli_query($con, $comprobacion);

if (!$result) 
{
    $result[] = array('resultado' => false);
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

if(mysqli_num_rows($result)==1){

//	$r=mysqli_fetch_array($result);
//
//	header('content-type:image/jpeg');

//	echo base64_decode($r[imagen]);
  while($row = $result->fetch_assoc()){
	$img=$row['imagen'];
	//echo base64_decode($img);
	echo $img;
  }
    
}
else{
	echo "false";
}
	

mysqli_close($con);

?>


