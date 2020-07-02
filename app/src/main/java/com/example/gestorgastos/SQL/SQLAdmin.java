package com.example.gestorgastos.SQL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.Modelos.CategoriasModel;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.Modelos.ViajesModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SQLAdmin extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "GestorGastos";
    public static final int DATABASE_VERSION = 1;


    //CONSTRUCTOR
    public SQLAdmin(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREATES
        db.execSQL(GestorDatabaseContract.UsuarioInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(GestorDatabaseContract.CategoriaInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(GestorDatabaseContract.CamionInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(GestorDatabaseContract.ViajesInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(GestorDatabaseContract.GastosInfoEntry.SQL_CREATE_TABLE);

        //INICIALIZACIONES
        //Recomendable inicializar usuarios
        db.execSQL(GestorDatabaseContract.UsuarioInfoEntry.INICIALIZAR_USUARIOS);
        //Inicializacion categorias
        db.execSQL(GestorDatabaseContract.CategoriaInfoEntry.INICIALIZAR_CATEGORIA);
        //inicializacion camiones
        db.execSQL(GestorDatabaseContract.CamionInfoEntry.INICIALIZAR_CAMIONES);
        //Inicializacion gastos (no recomendable)
        //db.execSQL(GestorDatabaseContract.GastosInfoEntry.INICIALIZAR_GASTOS);
        //Inicializacion viajes
        //db.execSQL(GestorDatabaseContract.ViajesInfoEntry.INICIALIZAR_VIAJES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS camiones ");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS gastos");
        db.execSQL("DROP TABLE IF EXISTS categoria");
        db.execSQL("DROP TABLE IF EXISTS viajes");
        onCreate(db);
    }


    public int getUltimoViaje() {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM viajes ORDER BY viajes_id DESC LIMIT 1", null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);


        }
        return id;
    }

    public int getUltimoUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios ORDER BY usuario_id DESC LIMIT 1", null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);


        }
        return id;
    }

    public void sumarMonto(int valor, int viaje) {
        SQLiteDatabase db = this.getWritableDatabase();
        int monto = 0;
        Cursor cursor = db.rawQuery("SELECT viaje_monto_total FROM viajes WHERE viajes_id='" + viaje + "'", null);
        while (cursor.moveToNext()) {
            monto = cursor.getInt(0);
        }

        monto += valor;
        db.execSQL("UPDATE viajes SET viaje_monto_total=" + monto + " WHERE viajes_id='" + viaje + "'");
    }

    public ArrayList<CategoriasModel> getAllCategorias() {
        ArrayList<CategoriasModel> categorias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoria ORDER BY categoria_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String categoria = cursor.getString(1);
            CategoriasModel categoriaObj = new CategoriasModel(id, categoria);
            categorias.add(categoriaObj);

        }
        return categorias;
    }

    public ArrayList<String> getCategoriasString() {
        ArrayList<String> categorias = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoria ORDER BY categoria_ID ASC", null);

        while (cursor.moveToNext()) {
            String categoria = cursor.getString(1);
            categorias.add(categoria);

        }
        return categorias;
    }

    public UsuariosModel getUsuarioShared(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        UsuariosModel usuario = new UsuariosModel();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni = '" + dni + "'", null);
        while (cursor.moveToNext()) {
            usuario.setId(cursor.getInt(0));
            usuario.setDni(cursor.getString(1));
            usuario.setContraseña(cursor.getString(2));
            usuario.setNombre(cursor.getString(3));
            usuario.setApellido(cursor.getString(4));
            usuario.setMail(cursor.getString(5));
            usuario.setCamion(cursor.getString(6));
            usuario.setAdmin(cursor.getInt(7));
            usuario.setEstado(cursor.getInt(8));

        }

        return usuario;

    }


    public ArrayList<UsuariosModel> getAllCamioneros(String dniSearch) {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni NOT LIKE '" + dniSearch + "' AND usuario_estado = 1 ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public ArrayList<UsuariosModel> getAllCamioneros_inhabilitados(String dniSearch) {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni NOT LIKE '" + dniSearch + "' AND usuario_estado = 0 ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public ArrayList<UsuariosModel> getAllAdministradores(String dniSearch) {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni NOT LIKE '" + dniSearch + "' AND usuario_estado = 1 AND  usuario_administrador = 1 ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public ArrayList<UsuariosModel> getAllSolicitudes(String dniSearch) {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni NOT LIKE '" + dniSearch + "' AND usuario_contraseña = '_PASSWORD_' ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public ArrayList<UsuariosModel> getAllUsuarios_SinCamion(String dniSearch) {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni NOT LIKE '" + dniSearch + "' AND usuario_estado = 1 AND  usuario_administrador = 0 AND usuario_camion IS NULL ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public ArrayList<UsuariosModel> getAllUsuarios() {
        ArrayList<UsuariosModel> usuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios  ORDER BY usuario_nombre", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dni = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String nombre = cursor.getString(3);
            String apellido = cursor.getString(4);
            String mail = cursor.getString(5);
            String camion = cursor.getString(6);
            int admin = cursor.getInt(7);
            int solicitud = cursor.getInt(8);
            int estado = cursor.getInt(9);
            UsuariosModel usuario = new UsuariosModel(id, dni, contraseña, nombre, apellido, mail, camion, admin, solicitud, estado);
            usuarios.add(usuario);

        }
        return usuarios;
    }

    public void inhabilitarUsuario(String dni) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET usuario_estado= 0 WHERE usuario_dni='" + dni + "'");
    }

    public void desasignarCamion(String dni) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE camiones SET camion_chofer= null WHERE camion_chofer='" + dni + "'");
        db.execSQL("UPDATE usuarios SET usuario_camion = null WHERE usuario_dni='" + dni + "'");


    }

    public void habilitarUsuario(String dni) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET usuario_estado= 1 WHERE usuario_dni='" + dni + "'");
    }

    public ArrayList<CamionModel> getAllCamiones() {
        ArrayList<CamionModel> camiones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones ORDER BY camion_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String patente = cursor.getString(1);
            String activo = cursor.getString(2);
            String chofer = cursor.getString(3);

            CamionModel camion = new CamionModel(id, patente, activo, chofer);
            camiones.add(camion);

        }
        return camiones;
    }

    public CamionModel getUltimoCamion() {
        CamionModel camion = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones ORDER BY camion_id DESC LIMIT 1", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String patente = cursor.getString(1);
            String activo = cursor.getString(2);
            String chofer = cursor.getString(3);

            camion = new CamionModel(id, patente, activo, chofer);


        }
        return camion;
    }

    public ArrayList<CamionModel> getAllCamiones_Ocupados() {
        ArrayList<CamionModel> camiones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones WHERE camion_chofer IS NOT NULL  ORDER BY camion_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String patente = cursor.getString(1);
            String activo = cursor.getString(2);
            String chofer = cursor.getString(3);

            CamionModel camion = new CamionModel(id, patente, activo, chofer);
            camiones.add(camion);

        }
        return camiones;
    }

    public ArrayList<CamionModel> getAllCamiones_Disponibles() {
        ArrayList<CamionModel> camiones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones WHERE camion_chofer IS NULL ORDER BY camion_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String patente = cursor.getString(1);
            String activo = cursor.getString(2);
            String chofer = cursor.getString(3);

            CamionModel camion = new CamionModel(id, patente, activo, chofer);
            camiones.add(camion);

        }
        return camiones;
    }

    public ArrayList<ViajesModel> getAllViajes() {
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes ORDER BY viajes_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getAllViajes_disponibles() {
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viajes_chofer IS NULL AND viaje_estado = 'Planeado' ORDER BY viajes_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getAllViajes_Planeados() {
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viaje_estado = 'Planeado' AND viajes_chofer IS NOT NULL ORDER BY viajes_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getAllViajes_Camino() {
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viaje_estado = 'En Camino' ORDER BY viajes_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getAllViajes_Completados() {
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viaje_estado = 'Completo' ORDER BY viajes_id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public int getIdCamion(String patente) {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT camion_id FROM camiones WHERE camion_patente = '" + patente + "'", null);

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);


        }
        return id;
    }

    public boolean usuarioExiste(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario_dni = '" + dni + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getUsuariosSinCamionAsignado() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> usuarios = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT usuario_dni FROM usuarios  where usuario_camion IS NULL AND usuario_administrador == 0 AND usuario_estado == 1", null);

        while (cursor.moveToNext()) {
            String dni = cursor.getString(0);

            usuarios.add(dni);

        }

        return usuarios;

    }

    public ArrayList<String> getCamionesSinConductor() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> camiones = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT camion_patente FROM camiones  where camion_chofer IS NULL", null);

        while (cursor.moveToNext()) {
            String patente = cursor.getString(0);

            camiones.add(patente);

        }

        return camiones;

    }

    public String getOrigenDestinoViaje(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String resultado = "";
        Cursor cursor = db.rawQuery("SELECT viaje_origen , viaje_destino FROM viajes WHERE viajes_id = '" + id + "'", null);
        while (cursor.moveToNext()) {
            String origen = cursor.getString(0);
            String destino = cursor.getString(1);

            resultado = origen + " / " + destino;

        }
        return resultado;

    }

    public void asignarCamionUsuario(String usuario, String patente) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET usuario_camion= '" + patente + "' WHERE usuario_dni='" + usuario + "'");

    }

    public void asignarUsuarioaCamion(String patente, String usuario) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE camiones SET camion_chofer='" + usuario + "' WHERE camion_patente='" + patente + "'");
    }

    public ArrayList<CamionModel> getCamionesSinChofer() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CamionModel> camiones = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones WHERE camion_chofer IS NULL", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String patente = cursor.getString(1);
            String activo = cursor.getString(2);
            String chofer = cursor.getString(3);

            CamionModel camion = new CamionModel(id, patente, activo, chofer);
            camiones.add(camion);

        }

        return camiones;

    }

    public ArrayList<ViajesModel> getViajesDeChofer_Planeados(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'Planeado'  ORDER BY viajes_id DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getViajesDeChofer_Camino(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'En Camino' ORDER BY viajes_id DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<ViajesModel> getViajesDeChofer_Terminados(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ViajesModel> viajes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'Completo' ORDER BY viajes_id DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String origen = cursor.getString(2);
            String destino = cursor.getString(3);
            String estado = cursor.getString(4);
            int montoTotal = cursor.getInt(5);

            ViajesModel viaje = new ViajesModel(id, chofer, origen, destino, estado, montoTotal);
            viajes.add(viaje);

        }
        return viajes;
    }

    public ArrayList<String> getViajesChoferId(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> viaje = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT viaje_origen, viaje_destino, viajes_id FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'En Camino'", null);
        while (cursor.moveToNext()) {
            String origen = cursor.getString(0);
            String destino = cursor.getString(1);
            int numero = cursor.getInt(2);
            viaje.add(origen);
            viaje.add(destino);
            viaje.add(String.valueOf(numero));
        }
        return viaje;
    }


    public boolean elChoferTieneViajesEnCamino(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT viajes_id FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'En Camino'", null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }


    //Este camion existe? Busqueda por patente
    public boolean camionExiste(String patente) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM camiones WHERE camion_patente = '" + patente + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<GastosModel> getMisGastos_habiles(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_chofer = '" + dni + "' AND gastos_estimacion = 1 AND gastos_sincronizado = 0  ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);

            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getMisGastos_pendientes(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_chofer = '" + dni + "' AND gastos_estimacion = 2 ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);

            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getMisGastos_desestimados(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_chofer = '" + dni + "' AND gastos_estimacion = 0 ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);

            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getAllGastos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);

            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getGastos_habiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_estimacion = 1 AND gastos_sincronizado = 0 ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);
            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getGastos_pendientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_estimacion = 2 ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);
            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public ArrayList<GastosModel> getGastos_desestimados() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GastosModel> gastos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_estimacion = 0 ORDER BY gastos_ID DESC", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);
            GastosModel gasto = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);
            gastos.add(gasto);


        }
        return gastos;
    }

    public GastosModel getUltimoGasto() {
        SQLiteDatabase db = this.getReadableDatabase();
        GastosModel gm = null;
        Cursor cursor = db.rawQuery("SELECT * FROM gastos ORDER BY gastos_ID DESC LIMIT 1", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String chofer = cursor.getString(1);
            String viaje = cursor.getString(2);
            int importe = cursor.getInt(3);
            String categoria = cursor.getString(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            String foto = cursor.getString(7);
            int estimacion = cursor.getInt(8);
            int sincronizacion = cursor.getInt(9);
            String fecha = cursor.getString(10);
            gm = new GastosModel(id, viaje, chofer, categoria, latitud, longitud, foto, fecha, importe, estimacion, sincronizacion);


        }
        return gm;
    }


    public void eliminarCategoria(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM categoria WHERE categoria_ID = '" + id + "'");
    }

    public void eliminarCamion(String patente) {
        SQLiteDatabase db = this.getWritableDatabase();
        String dni = null;
        Cursor cursor = db.rawQuery("SELECT camion_chofer FROM camiones WHERE camion_patente = '" + patente + "'", null);

        //Desasignar camion a usuario
        if (cursor.moveToFirst()) {
            dni = cursor.getString(0);
            db.execSQL("UPDATE usuarios SET usuario_camion = null WHERE usuario_dni = '" + dni + "'");

        }
        cursor.close();
        //Eliminar camion
        db.execSQL("DELETE FROM camiones WHERE camion_patente = '" + patente + "'");

    }


    public void vencerViaje(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE viajes SET viaje_estado = 'Completo' WHERE viajes_id = '" + id + "'");
    }

    public ArrayList<String> getChoferesdni() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> usuarios = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT usuario_dni FROM usuarios WHERE usuario_administrador == '0'", null);

        while (cursor.moveToNext()) {
            String dni = cursor.getString(0);
            usuarios.add(dni);

        }

        return usuarios;
    }

    public void cambiarAEnCamino(int viaje) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE viajes SET viaje_estado = 'En Camino' WHERE viajes_id = '" + viaje + "'");
    }

    public int desestimarGasto(int gasto, String viaje, int montoGasto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int montoTotal = 0;
        Cursor cursor = db.rawQuery("SELECT viaje_monto_total FROM viajes  where viajes_id ='" + viaje + "'", null);

        while (cursor.moveToNext()) {
            montoTotal = cursor.getInt(0);
        }

        montoTotal = montoTotal - montoGasto;

        db.execSQL("UPDATE viajes SET viaje_monto_total =" + montoTotal + " WHERE viajes_id = '" + viaje + "'");

        db.execSQL("UPDATE gastos SET gastos_estimacion = 0 WHERE gastos_ID = " + gasto);

        return montoTotal;
    }

    public void solicitarDesestimacion(int gasto) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE gastos SET gastos_estimacion = 2 WHERE gastos_ID = " + gasto);
    }

    public boolean hayGastosPendiendientes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE gastos_estimacion = 2", null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public void volverAEstimar(int gasto) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE gastos SET gastos_estimacion = 1 WHERE gastos_ID = " + gasto);
        db.close();
    }

    public void editarViajeEnCamino(String destino, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE viajes SET viaje_destino = '" + destino + "' WHERE viajes_id = '" + id + "'");
        db.close();
    }

    public void editarCamion(String patente, String activo, String chofer) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (chofer == null) {
            db.execSQL("UPDATE camiones SET camion_activo= '" + activo + "', camion_chofer = null WHERE camion_patente = '" + patente + "'");
            db.execSQL("UPDATE usuarios SET usuario_camion= null WHERE usuario_camion = '" + patente + "'");

        } else {
            db.execSQL("UPDATE camiones SET camion_activo= '" + activo + "', camion_chofer = '" + chofer + "' WHERE camion_patente = '" + patente + "'");
            asignarCamionUsuario(chofer, patente);
        }


    }

    public boolean elCamionEstaEnViaje(String patente) {

        String chofer = null;
        boolean tieneViaje = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT camion_chofer FROM camiones  where camion_patente = '" + patente + "'", null);

        if (cursor.moveToFirst()) {
            chofer = cursor.getString(0);
            tieneViaje = elChoferTieneViajesEnCamino(chofer);
        }

        return tieneViaje;

    }

    public void editarViajePlaneado(String destino, String origen, String chofer, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (chofer == null) {
            db.execSQL("UPDATE viajes SET viaje_destino = '" + destino + "' , viaje_origen = '" + origen + "', viajes_chofer = null WHERE viajes_id = '" + id + "'");
        } else {
            db.execSQL("UPDATE viajes SET viaje_destino = '" + destino + "' , viaje_origen = '" + origen + "', viajes_chofer = '" + chofer + "' WHERE viajes_id = '" + id + "'");
        }
        db.close();


    }

    public ArrayList<Integer> getViajesUsuario(String dni) {
        ArrayList<Integer> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT viajes_id FROM viajes WHERE viajes_chofer = '" + dni + "'", null);

        while (cursor.moveToNext()) {
            int resultado = cursor.getInt(0);
            resultados.add(resultado);

        }

        return resultados;
    }

    public void deshabilitarViajesDeUser(String dni) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE viajes SET viajes_chofer = null WHERE viajes_chofer = '" + dni + "'");

        db.close();
    }

    public boolean elUsuarioEstaEnViaje(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM viajes WHERE viajes_chofer = '" + dni + "' AND viaje_estado = 'En Camino'", null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;

        }

    }

    public int getMontoGasto(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int monto = 0;
        Cursor cursor = db.rawQuery("SELECT viaje_monto_total FROM viajes WHERE viajes_id = '" + id + "'", null);
        if (cursor.moveToFirst()) {
            monto = cursor.getInt(0);
        }

        return monto;

    }

    public boolean elUsuarioTieneCamion(String dni) {
        SQLiteDatabase db = this.getReadableDatabase();
        String camion = null;
        Cursor cursor = db.rawQuery("SELECT usuario_camion FROM usuarios WHERE usuario_dni = '" + dni + "'", null);
        while (cursor.moveToNext()) {
            camion = cursor.getString(0);
        }
        if (camion == null) {
            return false;
        } else {
            return true;
        }

    }

    public void editarUsuario(String nombre, String apellido, String mail, int isAdmin, String dni) {
        SQLiteDatabase db = this.getWritableDatabase();
        //El usuario tenia un camion antes de este update?

        boolean camionEstado;
        Cursor cursor = db.rawQuery("SELECT * FROM camiones WHERE camion_chofer = '" + dni + "'", null);

        if (cursor.getCount() > 0 && cursor != null) {
            camionEstado = true;
        } else {
            camionEstado = false;
        }
        cursor.close();


        //Si el camion recibido no es nulo y no es administrador
        if (isAdmin == 0) {
            db.execSQL("UPDATE usuarios SET  usuario_nombre ='" + nombre + "' , usuario_apellido = '" + apellido + "' , usuario_mail = '" + mail + "'  , usuario_administrador =" +
                    isAdmin + " WHERE  usuario_dni = '" + dni + "'");

        } else {
            if (camionEstado && isAdmin == 1) {
                desasignarCamion(dni);
            }
            //Si el camion es nulo o es administrador
            db.execSQL("UPDATE usuarios SET  usuario_nombre ='" + nombre + "' , usuario_apellido = '" + apellido + "' , usuario_mail = '" + mail + "' , usuario_camion = null , usuario_administrador =" +
                    isAdmin + " WHERE  usuario_dni = '" + dni + "'");
        }


    }

    public void cancelarViaje(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE viajes SET viaje_estado = 'Cancelado' WHERE viajes_id = '" + id + "'");
    }

    public void cambiarContraseña(int idUsuario, String nuevaContraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET usuario_contraseña = '" + nuevaContraseña + "' WHERE usuario_id = '" + idUsuario + "'");
    }


    public void sincronizarGasto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE gastos SET gastos_sincronizado = 1 WHERE gastos_ID = '" + id + "'");


    }

    public String getNombreUsuario (String dni){
        SQLiteDatabase db = this.getReadableDatabase();
        String nombre = "test";
        String apellido = "test";
        String nombreCompleto = "test";
        Cursor cursor = db.rawQuery("SELECT usuario_nombre, usuario_apellido FROM usuarios WHERE usuario_dni = '" + dni + "'", null);
        while (cursor.moveToNext()) {
            nombre = cursor.getString(0);
            apellido = cursor.getString(1);
        }
        nombreCompleto = nombre + " " + apellido;
        return nombreCompleto;
    }

    public int getCamionId(String patente) {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT camion_id FROM camiones WHERE camion_patente = '" + patente + "'", null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }
        return id;
    }


}
