<?php

$cabecera= array(
'Authorization: key=AAAATjSQYE4:APA91bHt7SuJVfSWAAf2zkh1f4SBN5MOrgPcWp0WROOBsaxTP1wrZWhCLi5HLvf8xzeaA4Lga2gI-UcUe3FVtgshXUdLtuEyOmhXDXEajinucr9tE7bMlmfGo4L8nH-owQLHTsfgU7ic',
'Content-Type: application/json'
);

$titulo=$_POST['titulo'];
$mensaje=$_POST['men'];

$msg= array(
'to'=>  "/topics/subirimagen",
'data' => array (
"mensaje" => $message,
"fecha" => 'se supone que aqui la fecha'),
'notification' => array (
'body' => $mensaje,
'title' => $titulo,
'icon' => 'ic_stat_ic_notification',
),

);
$msgJSON= json_encode ( $msg);

$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );
#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
#ejecutar la llamada
$resultado= curl_exec( $ch );
#cerrar el handler de curl
curl_close( $ch );
if (curl_errno($ch)) {
print curl_error($ch);
}
echo $resultado;
?>