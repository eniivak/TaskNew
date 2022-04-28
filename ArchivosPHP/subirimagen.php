<?php
$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xeverhorst001"; #el usuario para esa base de datos
$DB_PASS="*EyzCAv7UH"; #la clave para ese usuario
$DB_DATABASE="Xeverhorst001_tareamanager"; #la base de datos a la que hay que conectarse

$image = $_POST['img'];
$tit = $_POST['titulo'];

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno($con)) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	$fin="false";
}
else{
	$comprobacion="SELECT * FROM imagenes WHERE titulo='$tit'";
	
	
	$result= mysqli_query($con, $comprobacion);

	if(mysqli_num_rows($result)==0){
	    $q="INSERT INTO imagenes (imagen,titulo) VALUES ('$image','$tit')";
	    $result= mysqli_query($con, $q);
	    echo "true";
	    
	}
	else{
		while($row = $comprobacion->fetch_assoc()){
			$titulo=$row['titulo'];
			$img=$row['imagen'];
			echo $titulo;
			echo $img;
		}
	}
	
}

?>


