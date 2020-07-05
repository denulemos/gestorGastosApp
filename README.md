# Gestor Gastos

Esta aplicacion es un gestor de Gastos hecho con Java y para Android Nativo. Funciona con SQLite (Esto quiere decir que para que la misma funcione en un entorno de produccion, debe ser 
integrada con Firebase) y posee integracion con Google Sheets mediante Google Script, funciona como un ABM de camiones, gastos, usuarios, etc.., es por eso que, para que la misma pueda funcionar
localmente en Android Studio, se deben hacer algunas configuraciones previas.

Sientanse libres de tomar, y mejorar el codigo a su gusto :) Puede que la calidad del mismo no sea la mejor debido a los tiempos en los cuales esta aplicacion fue desarollada.

### Inicializaciones y Detalles 🔧

*La aplicacion posee datos inicializados en la base de datos (Que recordemos, es SQLite, funciona solo en un ambiente local y no productivo) (DatabaseContract).

Usuarios: Un usuario comun y un usuario administrador. El usuario cuyo DNI es 00000000 no puede ser eliminado. 

Camiones: Solo algunos modelos de prueba

Categorias: 4 categorias

Viajes y Gastos no son inicializados. 

*La aplicacion no posee Unit Testing (Perdon!).

*La aplicacion fue desarollada con Android Studio Canary (Tengo AMD, en el momento en que comence el desarollo, la version productiva de Android Studio no soportaba la virtualizacion en AMD).

## Seteando Google Sheets y Google Scripts 🚀

**TENER EN CUENTA COMPLETAR LOS ARCHIVOS DE CONFIGURACION DE GOOGLE MAPS CON UNA KEY PERSONAL PARA QUE LOS MAPAS FUNCIONEN CORRECTAMENTE EN LOCAL!!!** 

Para que la aplicacion tenga una correcta implementacion con Google Sheets, necesitaremos:

1- Crear un Google Sheet, puede ser en nuestro drive personal
2-Crear un nuevo Google Script

Un video/tutorial que me sirvio mucho para el Set up del ambiente fue el siguiente: https://www.youtube.com/watch?v=xz-BX-eLBN0&t=1s_

Hay que tener en cuenta que, no pretendemos usar Google Sheets como base de datos, solo como un "historico" de la aplicacion. Una preview de como se encuentra actualmente la base de datos real de la aplicacion (Que en este caso es SQLite), por lo tanto solo haremos uso del INSERT y de los UPDATE. 

En mi caso tenia un Sheet que poseia la seccion Camiones, Viajes, Usuarios y Gastos, TENERLO EN CUENTA AL VER EL SIGUIENTE SCRIPT!.

El Script utilizado fue el siguiente => 


```
var ss = SpreadsheetApp.openByUrl("LINK DE TU SHEET PERSONAL"); 

function doPost(e){
  
  var jsonPost = JSON.parse(e.postData.contents);
  
  switch(jsonPost.action){
    case "add":
       var sheetName = jsonPost.sheet;
  var rows = jsonPost.rows;

  for(var i = 0; i < jsonPost.rows.length; i++){
 
    ss.getSheetByName(sheetName).appendRow(jsonPost.rows[i]);
  }
   
      break;
      
    case "addCamion":
      var sheetName = "Camiones";
      var id = jsonPost.id;
      var patente = jsonPost.patente;
      var activo = jsonPost.activo;
      var chofer = jsonPost.chofer;
      
      ss.getSheetByName(sheetName).getRange(id + 1, 1).setValue(id);
     ss.getSheetByName(sheetName).getRange(id + 1, 2).setValue(patente);
     ss.getSheetByName(sheetName).getRange(id + 1, 3).setValue(activo);
     ss.getSheetByName(sheetName).getRange(id + 1, 4).setValue(chofer);
      
      break;
    case "addDesestimado":
        var rows = jsonPost.rows;
    var viaje = jsonPost.viaje + 1;
ss.getSheetByName("Viajes").getRange(viaje, 6).setValue(jsonPost.monto);
    
  for(var i = 0; i < jsonPost.rows.length; i++){
 
    ss.getSheetByName("Gastos").appendRow(jsonPost.rows[i]);
    
  }
      break;
      
    case "viajeAEnCamino":
       var sheetName = "Viajes";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 5).setValue("En Camino");
      break;
      
    case "viajeFinalizado":
       var sheetName = "Viajes";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 5).setValue("Completo");
      break;
      
    case "deshabilitarUsuario":
        var sheetName = "Usuarios";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 6).setValue("Inactivo");
      break;
      
    case "editarUsuario":
         var sheetName = "Usuarios";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 2).setValue(jsonPost.nombre);
     ss.getSheetByName(sheetName).getRange(id + 1, 3).setValue(jsonPost.apellido);
     ss.getSheetByName(sheetName).getRange(id + 1, 5).setValue(jsonPost.email);
     ss.getSheetByName(sheetName).getRange(id + 1, 7).setValue(jsonPost.rol);
      break;
      
    case "habilitarUsuario":
       var sheetName = "Usuarios";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 6).setValue("Activo");
      break;
      
    case "editarViajeCamino":
       var sheetName = "Viajes";
  var id = jsonPost.id;
      var destino = jsonPost.destino;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 4).setValue(destino);
      break;
      
    case "editarViajePlan":
        var sheetName = "Viajes";
  var id = jsonPost.id;
      var destino = jsonPost.destino;
         var origen = jsonPost.origen;
         
        if (jsonPost.chofer == null){
         var chofer = "Disponible";
        }
        else{
       var chofer = jsonPost.chofer;
        }
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 4).setValue(destino);
         ss.getSheetByName(sheetName).getRange(id + 1, 3).setValue(origen);
         ss.getSheetByName(sheetName).getRange(id + 1, 2).setValue(chofer);
      break;
      
    case "editarCamion":
          var sheetName = "Camiones";
  var id = jsonPost.id;
         var activo = jsonPost.activo;
       var chofer = jsonPost.chofer;
          /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 3).setValue(activo);
         ss.getSheetByName(sheetName).getRange(id + 1, 4).setValue(chofer);
      break;
      
    case "cancelarViaje":
        var sheetName = "Viajes";
  var id = jsonPost.id;
  
  /* Fila / Columna */
   ss.getSheetByName(sheetName).getRange(id + 1, 5).setValue("Cancelado");
      break;
      
    case "editarCamionChofer":
        var sheetName = "Camiones";
  var id = jsonPost.id;
       var chofer = jsonPost.chofer;
          /* Fila / Columna */
         ss.getSheetByName(sheetName).getRange(id + 1, 4).setValue(chofer);
      break;
      
    case "actualizarMontoViaje":
          var sheetName = "Viajes";
  var id = jsonPost.id;
       var chofer = jsonPost.chofer;
      var monto = jsonPost.monto;
          /* Fila / Columna */
         ss.getSheetByName(sheetName).getRange(id + 1, 6).setValue(monto);
      break;
      
    case "desasignarViajes":
          var sheetName = "Viajes";
     var rows = jsonPost.rows;

  for(var i = 0; i < jsonPost.rows.length; i++){
 
     ss.getSheetByName(sheetName).getRange(jsonPost.rows[i], 2).setValue("Disponible");
  }
      break;
      default:
       return ContentService.createTextOutput("No se llamo a ninguna funcion valida del Script");
      break;
  }
  
}


```

En el caso de uso de esta aplicacion, solo haremos POST sobre el Script de google pero las posibilidades son infinitas. Recomiendo Insomnia o Postman para probar el Script final. 

### Envio de Contraseña 📋

Esta aplicacion posee la funcion de "Olvide mi Contraseña" que aparece en la pantalla principal. La funcionalidad de la misma es, pedir el DNI y el Email del usuario que olvido su contraseña, si ambas coinciden con los registros de la base de datos actual, se envia un numero random al email del usuario, este numero seria la nueva contraseña del usuario. El mismo tiene que usar ese numero como contraseña, para luego cambiarla si asi lo desea.

Para que esta funcionalidad este vigente, en este caso, se debe instalar la libreria JAVAMAIL que se puede encontrar en este link => https://code.google.com/archive/p/javamail-android/

Tener en cuenta que al enviar demasiados Emails de prueba desde la misma IP puede causar un bloqueo por parte del servicio de correos!. 

## ¿Dudas? ✒️


No duden en contactarme a mi Linkedin (https://www.linkedin.com/in/denisse-alejandra-lemos-999370124/) ante cualquier duda o sugerencia! Estoy abierta a aprender cosas nuevas y a reveer todo lo necesario :) .


