<?php
$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xeverhorst001"; #el usuario para esa base de datos
$DB_PASS="*EyzCAv7UH"; #la clave para ese usuario
$DB_DATABASE="Xeverhorst001_tareamanager"; #la base de datos a la que hay que conectarse
$USER=$_POST["user"];
$CONT=$_POST["pass"];

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno($con)) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	$fin="false";
}
else{
	$q="SELECT * FROM usuario WHERE usuario='$USER'";
	$result= mysqli_query($con, $q);

	if(mysqli_num_rows($result)>0){
	    while($row = $result->fetch_assoc())
	    {
	        $claveVerif= $row['contra']; //ver si la contraseña es correcta
	    }
	    if($claveVerif==$CONT){
	        $fin="true";
	    }
	    else{
	        $fin="false";
	    }
		    
	}
	else {
		echo " login failed.. :(";
	}
}
echo $fin;
exit();

?>




