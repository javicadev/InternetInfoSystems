CREATE TABLE PROYECTO (ID BIGINT IDENTITY NOT NULL, DETALLE VARCHAR, FECHAALTA DATE, NOMBRE VARCHAR, PRIMARY KEY (ID))
CREATE TABLE TAREA (DESCRIPCION VARCHAR, ESFUERZO DOUBLE, FECHAFIN DATE, FECHAINICIO DATE, NOMBRE VARCHAR, PROYECTO_ID BIGINT NOT NULL, ID BIGINT NOT NULL, PRIMARY KEY (PROYECTO_ID, ID))
ALTER TABLE TAREA ADD CONSTRAINT FK_TAREA_PROYECTO_ID FOREIGN KEY (PROYECTO_ID) REFERENCES PROYECTO (ID)
