package com.example.pm1examen3972.transacciones;

public class Operaciones {
    //Nombre de la BD
    public static final String nambeBD = "examen_kency_jensy";

    //Tablas de la BD
    public static final String tabla = "contactos";

    //Campos de la tabla
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";

    //Consultas de base de datos DDL
    //Creando la tabla con sus campos
    public static final String CreateTableContactos = "CREATE TABLE contactos "+"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "pais TEXT, nombre TEXT, telefono INTEGER, " +
            "nota TEXT)";



    //Para borrar la tabla
    public static final String DropTableContactos = "DROP TABLE IF EXISTS contactos";

    //Consultas de base de datos DML
    public static final String SelectTableContactos = "SELECT * FROM " + Operaciones.tabla;


}
