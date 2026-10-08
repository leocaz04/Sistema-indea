package com.indea.util;

import com.indea.excepcion.PersistenciaException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static Properties props;

    private ConexionBD() {
    }

    private static synchronized Properties cargar() {
        if (props == null) {
            props = new Properties();
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/indea_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
            File archivo = new File("config/db.properties");
            if (archivo.exists()) {
                try (FileInputStream in = new FileInputStream(archivo)) {
                    props.load(in);
                } catch (IOException e) {
                    throw new PersistenciaException("No se pudo leer el archivo config/db.properties.", e);
                }
            }
        }
        return props;
    }

    public static Connection obtener() {
        Properties p = cargar();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Falta el conector MySQL (mysql-connector-j). Agrega el .jar de la carpeta lib al Build Path del proyecto.", e);
        }
        try {
            return DriverManager.getConnection(p.getProperty("db.url"), p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL. Revisa que el servidor esté encendido, que exista la base indea_db y que config/db.properties tenga el usuario y la contraseña correctos.", e);
        }
    }
}
