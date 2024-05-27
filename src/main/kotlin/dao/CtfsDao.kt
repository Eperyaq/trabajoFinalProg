package dao

import DB_Connection.DataSourceFactory
import output.Console
import entity.CtfsEntity
import output.IOutputInfo
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase que implementa la interfaz [ICtfsDao] para proporcionar
 * operaciones de acceso a datos relacionadas con la entidad CTFS.
 *
 */
class CtfsDao(private val console: IOutputInfo, private val dataSource: DataSource): ICtfsDao {

    override fun insert(ctfid: Int, grupoId: Int, puntuacion: Int): Boolean {
        val sql = "INSERT INTO CTFS (CTFID, GRUPOID, PUNTUACION) VALUES (?, ?, ?)"
        var conexion: Connection? = null
        var stmt: PreparedStatement? = null

        try {
            conexion = DataSourceFactory.getConnection()

            conexion?.autoCommit = false //quitamos el autocommit para hacer la transaccion

            stmt = conexion?.prepareStatement(sql)

            stmt?.setInt(1, ctfid)
            stmt?.setInt(2, grupoId)
            stmt?.setInt(3, puntuacion)

            val rs = stmt?.executeUpdate()

            if (rs == 1) {
                console.showMessage("Procesado: Añadida participación del grupo $grupoId en el CTF $ctfid con una puntuación de $puntuacion puntos.")
                conexion?.autoCommit = true //lo ponemos de nuevo una vez terminada la transaccion
                return true
            } else {
                console.showMessage("Error, insert query failed ($rs records inserted)")
                conexion?.rollback() //si no se ha completado bien la transaccion hace rollback
                return false
            }


        } catch (e: SQLException) {
            //Deberia hacer rollback aqui tambien pero como solo se va a insertar una cosa y ya tengo controlado por si no funciona bien no voy a hacer rollback aqui
            console.showMessage("Error, ${e.message}")
            return false
        } finally {
            DataSourceFactory.closeDB(conexion)
        }
    }


    override fun update(ctfId: Int, grupoId: Int, puntuacion: Int, grupoDesc: String): Boolean {
        val sql = "UPDATE ctfs SET puntuacion = ? WHERE CTFid = ? AND grupoid = ?"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, puntuacion)
                    stmt.setInt(2, ctfId)
                    stmt.setInt(3, grupoId)
                    val rs = stmt.executeUpdate()
                    if (rs == 1) {
                        console.showMessage("Actualizada participación del grupo $grupoDesc en el CTF $ctfId con una puntuación de $puntuacion puntos.")
                        true
                    } else {
                        console.showMessage("Error")
                        false
                    }
                }
            }

        } catch (e: SQLException) {
            console.showMessage("Error, ${e.message}")
            false
        }
    }


    override fun deleteById(ctfsId: Int, grupoId: Int): Boolean {
        //Ejercicio hecho sin .use para demostrar que se hacerlo sin el .use y con el rollback en el catch
        val sql = "DELETE FROM CTFS WHERE grupoid = ? and ctfid = ?"
        var conexion: Connection? = null
        var statement: PreparedStatement? = null
        var resultado = false

        try {
            conexion = DataSourceFactory.getConnection()

            conexion?.autoCommit = false

            statement = conexion?.prepareStatement(sql)

            statement?.setInt(1, grupoId)
            statement?.setInt(2, ctfsId)

            val rs = statement?.executeUpdate()

            if (rs == 1) {
                conexion?.autoCommit = true
                resultado = true
            } else {
                conexion?.rollback()
                resultado = false
            }


        } catch (e: SQLException) {
            resultado = false
            conexion?.rollback() //Si algo ha ido mal se hace un rollback
        } finally {
            DataSourceFactory.closeDB(conexion)
        }

        return resultado
    }

    override fun selectAll(): List<CtfsEntity>? {
        val sql = "SELECT * FROM ctfs"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    val ctfs = mutableListOf<CtfsEntity>()
                    while (rs.next()) {
                        ctfs.add(
                            CtfsEntity(
                                CTFid = rs.getInt("CTFid"),
                                grupoId = rs.getInt("grupoid"),
                                puntuacion = rs.getInt("puntuacion")
                            )
                        )
                    }
                    ctfs
                }
            }
        } catch (e: SQLException) {
            console.showMessage("Error, ${e.message}")
            null
        }
    }


    /**
     * Realiza una consulta a la base de datos para obtener la descripcion del grupo y la puntuacion del grupo en un ctf especifico haciendo uso de un join con las claves
     * de ambas tablas para no tener un producto cartesiano.
     */
    override fun selectJoin(ctfId: Int): Map<String, Int>? {

        val sql = "SELECT G.GRUPODESC, C.PUNTUACION FROM GRUPOS G,CTFS C WHERE C.GRUPOID = G.GRUPOID AND CTFID = ? ORDER BY PUNTUACION DESC" //descendente para que aparezca de mayor a menor
        val mapaIdPuntuacion = mutableMapOf<String, Int>() //grupo puntuacion

        try {

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, ctfId)
                    val rs = stmt.executeQuery()

                    if (rs.next()) {
                        while (rs.next()) {

                            val grupoDesc = rs.getString(1)
                            val puntuacion = rs.getInt(2)

                            mapaIdPuntuacion[grupoDesc] = puntuacion
                        }
                    }else{
                        null
                    }
                }
            }
            return mapaIdPuntuacion
        } catch (e: SQLException) {
            return null
        }

    }

    /**
     * Elimina una entrada de la tabla CTFS por su ID de grupo, utilizando una conexión proporcionada ya que va a ser usada en una transaccion.
     *
     * @param grupoId El ID del grupo para el cual se deben eliminar las entradas.
     * @param conexion La conexión a la base de datos.
     * @return true si se eliminó con éxito, false si hay un error.
     */
    override fun deleteByIdConexion(grupoId: Int, conexion: Connection?): Boolean {
        //Ejercicio hecho sin .use para demostrar que se hacerlo sin el .use y con el rollback en el catch
        val sql = "DELETE FROM CTFS WHERE grupoid = ?"
        var statement: PreparedStatement? = null

        return try {
            statement = conexion?.prepareStatement(sql)

            statement?.setInt(1, grupoId)


            val rs = statement?.executeUpdate()

            if (rs != null) {
                true
            } else {
                false
            }
        } catch (e: SQLException) {
            false
        }
    }

    /**
     * Comprueba si existe una entrada en la tabla CTFS con un ID de CTF y un ID de grupo específicos.
     *
     * @param ctfID El ID del CTF.
     * @param grupoID El ID del grupo.
     * @return true si la entrada existe, false en caso contrario.
     */
    override fun existe(ctfID: Int, grupoID: Int): Boolean {

        val sql = "SELECT * FROM CTFS WHERE CTFID = ? AND GRUPOID = ?"

        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, ctfID)
                    stmt.setInt(2, grupoID)
                    val rs = stmt.executeQuery()

                    if (rs.next()) {
                        true
                    } else {
                        false
                    }
                }
            }
        } catch (e: SQLException) {
            console.showMessage("Error, ${e.message}")
            false
        }
    }

    /**
     * Selecciona los IDs de los CTFs para un grupo específico.
     *
     * @param grupoid El ID del grupo.
     * @return Una lista de IDs de CTF, o null si hay un error.
     */
    override fun selectCtf(grupoid: Int): List<Int>? {
        val sql = "SELECT CTFID FROM CTFS WHERE GRUPOID = ?"

        val listaCtfsID = mutableListOf<Int>()

        try {

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, grupoid)
                    val rs = stmt.executeQuery()

                    while (rs.next()) {

                        val ctfid = rs.getInt(1)

                        listaCtfsID.add(ctfid)
                    }
                }
            }
            return listaCtfsID
        } catch (e: SQLException) {
            return null
        }
    }
}