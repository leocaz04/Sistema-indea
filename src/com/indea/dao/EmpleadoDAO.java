package com.indea.dao;

import com.indea.modelo.Empleado;
import com.indea.modelo.Rol;

import java.util.List;

public interface EmpleadoDAO extends CrudDAO<Empleado> {

    List<Empleado> listarActivosPorRol(Rol rol);
}
