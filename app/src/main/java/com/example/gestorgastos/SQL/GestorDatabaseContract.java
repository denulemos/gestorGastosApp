package com.example.gestorgastos.SQL;

import android.provider.BaseColumns;

public final class GestorDatabaseContract {
    private GestorDatabaseContract() {
    }

    public static final class CamionInfoEntry {
        public static final String TABLE_NAME = "camiones";
        public static final String COLUMN_CAMION_ID = "camion_id";
        public static final String COLUMN_CAMION_PATENTE = "camion_patente";
        public static final String COLUMN_CAMION_ACTIVO = "camion_activo";
        public static final String COLUMN_CAMION_CHOFER = "camion_chofer";

        //CREAR TABLA
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_CAMION_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_CAMION_PATENTE + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_CAMION_ACTIVO + " TEXT NOT NULL, " +
                        COLUMN_CAMION_CHOFER + " TEXT UNIQUE )";

        public static final String INICIALIZAR_CAMIONES =
                "INSERT INTO " + (TABLE_NAME) + "(" + COLUMN_CAMION_PATENTE + "," + COLUMN_CAMION_ACTIVO +
                        ") VALUES  ('VIN290' , 'Ford Cargo 1519' ), ('VIN291' , 'Ford Cargo 1519' ), ('HMS464' , 'Ford Cargo 1519' )";
    }

    public static final class UsuarioInfoEntry {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_USUARIO_DNI = "usuario_dni";
        public static final String COLUMN_USUARIO_CONTRASEÑA = "usuario_contraseña";
        public static final String COLUMN_USUARIO_NOMBRE = "usuario_nombre";
        public static final String COLUMN_USUARIO_APELLIDO = "usuario_apellido";
        public static final String COLUMN_USUARIO_MAIL = "usuario_mail";
        public static final String COLUMN_USUARIO_CAMION = "usuario_camion";
        public static final String COLUMN_USUARIO_ADMINISTRADOR = "usuario_administrador";
        public static final String COLUMN_USUARIO_ESTADO = "usuario_estado";
        public static final String COLUMN_USUARIO_SOLICITUDCONTRASEÑA = "usuario_solicitud_contraseña";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_USUARIO_DNI + " TEXT UNIQUE NOT NULL , " +
                        COLUMN_USUARIO_CONTRASEÑA + " TEXT NOT NULL, " +
                        COLUMN_USUARIO_NOMBRE + " TEXT NOT NULL, " +
                        COLUMN_USUARIO_APELLIDO + " TEXT NOT NULL, " +
                        COLUMN_USUARIO_MAIL + " TEXT, " +
                        COLUMN_USUARIO_CAMION + " TEXT ," +
                        COLUMN_USUARIO_ADMINISTRADOR + " INTEGER NOT NULL ," +
                        COLUMN_USUARIO_SOLICITUDCONTRASEÑA + " INTEGER NOT NULL ," +
                        COLUMN_USUARIO_ESTADO + " INTEGER NOT NULL)";

        public static final String INICIALIZAR_USUARIOS =
                "INSERT INTO " + TABLE_NAME + "(" + COLUMN_USUARIO_DNI + "," + COLUMN_USUARIO_APELLIDO + "," + COLUMN_USUARIO_CONTRASEÑA + "," + COLUMN_USUARIO_MAIL + "," + COLUMN_USUARIO_NOMBRE + "," + COLUMN_USUARIO_ADMINISTRADOR + "," + COLUMN_USUARIO_ESTADO +"," +COLUMN_USUARIO_SOLICITUDCONTRASEÑA +  " ) " +
                        " VALUES ('12345678' , 'Usuario' , 'password' ,  'email@email.com' , 'Test' , 0 , 1 ,0) , " +
                        "('00000000' , 'Super' , 'adminpassword' ,  'email@email.com' , 'Admin' , 1, 1, 0)";

    }

    public static final class GastosInfoEntry {
        public static final String TABLE_NAME = "gastos";
        public static final String COLUMN_GASTOS_ID = "gastos_ID";
        public static final String COLUMN_GASTOS_CHOFER = "gastos_chofer";
        public static final String COLUMN_GASTOS_VIAJE = "gastos_viaje";
        public static final String COLUMN_GASTOS_IMPORTE = "gastos_importe";
        public static final String COLUMN_GASTOS_CATEGORIA = "gastos_categoria";
        public static final String COLUMN_GASTOS_FECHA = "gastos_fecha";
        public static final String COLUMN_GASTOS_UBICACIONLAT = "gastos_ubicacion_lat";
        public static final String COLUMN_GASTOS_UBICACIONLONG = "gastos_ubicacion_long";
        public static final String COLUMN_GASTOS_FOTO = "gastos_foto";
        public static final String COLUMN_GASTOS_ESTIMACION = "gastos_estimacion";
        public static final String COLUMN_GASTOS_SINCRONIZADO = "gastos_sincronizado";


        //CREAR TABLA
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_GASTOS_ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_GASTOS_CHOFER + " TEXT NOT NULL , " +
                        COLUMN_GASTOS_VIAJE + " INTEGER NOT NULL, " +
                        COLUMN_GASTOS_IMPORTE + " INTEGER NOT NULL, " +
                        COLUMN_GASTOS_CATEGORIA + " TEXT, " +
                        COLUMN_GASTOS_UBICACIONLAT + " TEXT, " +
                        COLUMN_GASTOS_UBICACIONLONG + " TEXT, " +
                        COLUMN_GASTOS_FOTO + " TEXT, " +
                        COLUMN_GASTOS_ESTIMACION + " INTEGER NOT NULL, " +
                        COLUMN_GASTOS_SINCRONIZADO + " INTEGER NOT NULL, " +
                        COLUMN_GASTOS_FECHA + " TEXT NOT NULL)";

        public static final String INICIALIZAR_GASTOS =
                "INSERT INTO " + TABLE_NAME + "(" + COLUMN_GASTOS_CHOFER + "," + COLUMN_GASTOS_VIAJE + "," + COLUMN_GASTOS_IMPORTE + "," +
                        COLUMN_GASTOS_CATEGORIA + "," + COLUMN_GASTOS_UBICACIONLAT +
                        "," + COLUMN_GASTOS_UBICACIONLONG + "," + COLUMN_GASTOS_ESTIMACION + "," +COLUMN_GASTOS_SINCRONIZADO + "," + COLUMN_GASTOS_FECHA + ") " +
                        " VALUES ('12345678' , 1 , 1234 ,  'Refrigerio' , '-34.636929' , '-58.4324628' ,1, 0,  'Wed Jun 10 17:32:30 GMT-03:00 2020') , " +
                        "('12345678' , 5 , 546 ,  'Hospedaje' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 4 , 2576 ,  'Peaje' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 1 , 75 ,  'Gasolina' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 2 , 246 ,  'Refrigerio' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 1 , 234 ,  'Gasolina' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 3 , 8797 ,  'Hospedaje' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')," +
                        "('12345678' , 1 , 145 ,  'Refrigerio' , '41.3650' , '47.2965' ,1, 0, 'Wed Jun 10 17:32:30 GMT-03:00 2020')";
    }

    public static final class CategoriaInfoEntry {
        public static final String TABLE_NAME = "categoria";
        public static final String COLUMN_CATEGORIA_ID = "categoria_ID";
        public static final String COLUMN_CATEGORIA_NOMBRE = "categoria_nombre";


        //CREAR TABLA
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_CATEGORIA_ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_CATEGORIA_NOMBRE + " TEXT UNIQUE NOT NULL)";

        public static final String INICIALIZAR_CATEGORIA =
                "INSERT INTO " + TABLE_NAME + "(" + COLUMN_CATEGORIA_NOMBRE + ") VALUES  ('Refrigerio'), ('Gasolina') , ('Hospedaje') , ('Peaje')";
    }

    public static final class ViajesInfoEntry {
        public static final String TABLE_NAME = "viajes";
        public static final String COLUMN_VIAJES_ID = "viajes_id";
        public static final String COLUMN_VIAJES_CHOFER = "viajes_chofer";
        public static final String COLUMN_VIAJES_ORIGEN = "viaje_origen";
        public static final String COLUMN_VIAJES_DESTINO = "viaje_destino";
        public static final String COLUMN_VIAJES_ESTADO = "viaje_estado";
        public static final String COLUMN_VIAJES_MONTOTOTAL = "viaje_monto_total";


        //CREAR TABLA
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_VIAJES_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_VIAJES_CHOFER + " TEXT, " +
                        COLUMN_VIAJES_ORIGEN + " TEXT NOT NULL, " +
                        COLUMN_VIAJES_DESTINO + " TEXT NOT NULL," +
                        COLUMN_VIAJES_ESTADO + " TEXT NOT NULL," +
                        COLUMN_VIAJES_MONTOTOTAL + " INTEGER " +
                        ")";

        public static final String INICIALIZAR_VIAJES =
                "INSERT INTO " + TABLE_NAME + "(" + COLUMN_VIAJES_CHOFER + "," + COLUMN_VIAJES_ORIGEN + "," +
                        COLUMN_VIAJES_DESTINO + "," + COLUMN_VIAJES_ESTADO + "," + COLUMN_VIAJES_MONTOTOTAL + ") " +
                        " VALUES ('12345678'  ,  '91407 Joana Prairie' , '591 Boehm Prairie' , 'Planeado' , 0) , " +
                        "('12345678'  , '91407 Joana Prairie' ,  '591 Boehm Prairie' , 'Planeado' , 0)," +
                        "('12345678'  , '91407 Joana Prairie' ,  '695 Doyle Neck' ,'Planeado' , 0)," +
                        "('12345678'  , '91407 Joana Prairie' ,  '591 Boehm Prairie' , 'Planeado', 0)," +
                        "('12345678'  , '91407 Joana Prairie' ,  '79924 Wiegand Extensions' , 'Planeado', 0)," +
                        "('12345678' , '91407 Joana Prairie' ,  '695 Doyle Neck' , 'Planeado', 0)," +
                        "('12345678'  , '91407 Joana Prairie' ,  '591 Boehm Prairie' , 'Planeado', 0)," +
                        "('12345678'  , '914077 Joana Prairie' ,  '5911 Boehm Prairie' , 'Planeado', 0)," +
                        "('12345678'  , '91407 Joana Prairie' ,  '79924 Wiegand Extensions' , 'Planeado', 0)";
    }


}
