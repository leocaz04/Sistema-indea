CREATE DATABASE indea_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE indea_db;


CREATE TABLE empleados (
    id_empleado    INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    telefono       VARCHAR(15)  NOT NULL,
    correo         VARCHAR(100) NULL,
    rol            ENUM('ADMINISTRADOR','TECNICO','VENDEDOR','MARKETING') NOT NULL,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_ingreso  DATE         NOT NULL
) ENGINE=InnoDB;


CREATE TABLE usuarios (
    id_usuario         INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado        INT          NOT NULL UNIQUE,
    nombre_usuario     VARCHAR(50)  NOT NULL UNIQUE,
    contrasena_hash    VARCHAR(255) NOT NULL,
    intentos_fallidos  TINYINT      NOT NULL DEFAULT 0,
    bloqueado          BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_usuarios_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleados (id_empleado)
) ENGINE=InnoDB;

CREATE TABLE clientes (
    id_cliente      INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(15)  NOT NULL UNIQUE,
    correo          VARCHAR(100) NULL,
    direccion       VARCHAR(200) NULL,
    fecha_registro  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE reparaciones (
    id_reparacion    INT AUTO_INCREMENT PRIMARY KEY,
    folio            VARCHAR(20)   NOT NULL UNIQUE,
    id_cliente       INT           NOT NULL,
    id_tecnico       INT           NOT NULL,
    tipo_equipo      VARCHAR(50)   NOT NULL,
    marca            VARCHAR(50)   NULL,
    modelo           VARCHAR(50)   NULL,
    falla_reportada  VARCHAR(500)  NOT NULL,
    diagnostico      VARCHAR(500)  NULL,
    costo_servicio   DECIMAL(10,2) NOT NULL DEFAULT 0,
    pagado           BOOLEAN       NOT NULL DEFAULT FALSE,
    estado           ENUM('RECIBIDO','EN_DIAGNOSTICO','EN_REPARACION','LISTO_PARA_ENTREGA','ENTREGADO')
                     NOT NULL DEFAULT 'RECIBIDO',
    fecha_ingreso    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega    DATETIME      NULL,
    CONSTRAINT fk_reparaciones_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
    CONSTRAINT fk_reparaciones_tecnico FOREIGN KEY (id_tecnico) REFERENCES empleados (id_empleado),
    CONSTRAINT chk_reparaciones_costo CHECK (costo_servicio >= 0)
) ENGINE=InnoDB;

CREATE TABLE historial_estado_reparacion (
    id_historial   INT AUTO_INCREMENT PRIMARY KEY,
    id_reparacion  INT      NOT NULL,
    estado         ENUM('RECIBIDO','EN_DIAGNOSTICO','EN_REPARACION','LISTO_PARA_ENTREGA','ENTREGADO') NOT NULL,
    fecha_cambio   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_empleado    INT      NOT NULL,
    CONSTRAINT fk_historial_reparacion FOREIGN KEY (id_reparacion)
        REFERENCES reparaciones (id_reparacion) ON DELETE CASCADE,
    CONSTRAINT fk_historial_empleado FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado)
) ENGINE=InnoDB;


CREATE TABLE refacciones (
    id_refaccion  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL UNIQUE,
    costo         DECIMAL(10,2) NOT NULL,
    stock         INT           NOT NULL DEFAULT 0,
    CONSTRAINT chk_refacciones_costo CHECK (costo >= 0),
    CONSTRAINT chk_refacciones_stock CHECK (stock >= 0)
) ENGINE=InnoDB;

CREATE TABLE reparacion_refaccion (
    id_reparacion   INT           NOT NULL,
    id_refaccion    INT           NOT NULL,
    cantidad        INT           NOT NULL,
    costo_unitario  DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_reparacion, id_refaccion),
    CONSTRAINT fk_rr_reparacion FOREIGN KEY (id_reparacion)
        REFERENCES reparaciones (id_reparacion) ON DELETE CASCADE,
    CONSTRAINT fk_rr_refaccion FOREIGN KEY (id_refaccion) REFERENCES refacciones (id_refaccion),
    CONSTRAINT chk_rr_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB;


CREATE TABLE equipos (
    id_equipo     INT AUTO_INCREMENT PRIMARY KEY,
    tipo          VARCHAR(50)   NOT NULL,
    marca         VARCHAR(50)   NULL,
    modelo        VARCHAR(50)   NULL,
    condicion     VARCHAR(50)   NOT NULL,
    precio_venta  DECIMAL(10,2) NOT NULL,
    estado        ENUM('DISPONIBLE','VENDIDO','NO_DISPONIBLE_AUN') NOT NULL DEFAULT 'NO_DISPONIBLE_AUN',
    fecha_alta    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_equipos_precio CHECK (precio_venta >= 0)
) ENGINE=InnoDB;


CREATE TABLE compras (
    id_compra      INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo      INT           NOT NULL UNIQUE,
    origen         ENUM('CLIENTE','PROVEEDOR') NOT NULL,
    id_cliente     INT           NULL,
    proveedor      VARCHAR(100)  NULL,
    id_empleado    INT           NOT NULL,
    precio_compra  DECIMAL(10,2) NOT NULL,
    fecha_compra   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_compras_equipo   FOREIGN KEY (id_equipo)   REFERENCES equipos (id_equipo),
    CONSTRAINT fk_compras_cliente  FOREIGN KEY (id_cliente)  REFERENCES clientes (id_cliente),
    CONSTRAINT fk_compras_empleado FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado),
    CONSTRAINT chk_compras_precio CHECK (precio_compra >= 0),
    CONSTRAINT chk_compras_origen CHECK (
        (origen = 'CLIENTE'   AND id_cliente IS NOT NULL) OR
        (origen = 'PROVEEDOR' AND proveedor  IS NOT NULL))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. VENTAS (un equipo solo se puede vender una vez)
-- ---------------------------------------------------------------------
CREATE TABLE ventas (
    id_venta      INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo     INT           NOT NULL UNIQUE,
    id_cliente    INT           NOT NULL,
    id_vendedor   INT           NOT NULL,
    precio_venta  DECIMAL(10,2) NOT NULL,
    fecha_venta   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ventas_equipo   FOREIGN KEY (id_equipo)   REFERENCES equipos (id_equipo),
    CONSTRAINT fk_ventas_cliente  FOREIGN KEY (id_cliente)  REFERENCES clientes (id_cliente),
    CONSTRAINT fk_ventas_vendedor FOREIGN KEY (id_vendedor) REFERENCES empleados (id_empleado),
    CONSTRAINT chk_ventas_precio CHECK (precio_venta >= 0)
) ENGINE=InnoDB;


CREATE TABLE publicaciones (
    id_publicacion     INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo          INT          NOT NULL,
    id_autor           INT          NOT NULL,
    titulo             VARCHAR(100) NOT NULL,
    descripcion        VARCHAR(500) NULL,
    tipo               ENUM('DISPONIBLE','AGOTADO','PROXIMAMENTE') NOT NULL,
    activa             BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_publicacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_publicaciones_equipo FOREIGN KEY (id_equipo) REFERENCES equipos (id_equipo),
    CONSTRAINT fk_publicaciones_autor  FOREIGN KEY (id_autor)  REFERENCES empleados (id_empleado)
) ENGINE=InnoDB;



SHOW TABLES;