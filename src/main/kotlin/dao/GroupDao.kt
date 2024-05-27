package dao

import DB_Connection.DataSourceFactory
import androidx.compose.runtime.staticCompositionLocalOf
import output.Console
import entity.GroupEntity
import output.IOutputInfo
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource
import kotlin.collections.List

/**
 * Clase que implementa la interfaz [IGroupDao] para proporcionar
 * operaciones de acceso a datos relacionadas con la entidad GroupEntity.
 */
class GroupDao(private val console: IOutputInfo, private val dataSource: DataSource): IGroupDao {

    override fun insert(grupoDesc: String): Boolean {

        val sql = "INSERT INTO GRUPOS (GRUPODESC) VALUES (?)"
        var conexion: Connection? = null
        var stmt: PreparedStatement? = null
         try {
            conexion = DataSourceFactory.getConnection()

            conexion?.autoCommit = false //Indicamos el autocommit a false para poder hacer la transaccion

            stmt = conexion?.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

                stmt?.setString(1, grupoDesc)

                val resultado = stmt?.executeUpdate() //devuelve el numero de filas "afectadas" como estamos insertando ha de ser 1 si se ha insertado bien si no

                if (resultado ==1){

                    conexion?.autoCommit = true //Si ha salido bien el autocomit se pone a true

                    stmt?.generatedKeys.use { generatedKeys->
                        if (generatedKeys?.next() == true){
                            val generatedId = generatedKeys.getInt(1) //Le indicamos que la columna autoincremental es la primera de la tabla
                            console.showMessage("Procesado: Añadido el grupo '$grupoDesc'")
                            GroupEntity( generatedId,grupoDesc, 999)//Dejo esto por aqui por si se necesita en algun momento.
                            return true

                        } else{
                            console.showMessage("Error, no se generó ninguna key")
                            return false
                        }
                    }

                } else {
                    conexion?.rollback() //si hay algun error se hace rollback y no se
                    console.showMessage("Error, insert query failed ($resultado records inserted)")
                    return false
                }
         }catch (e: SQLException){
            console.showMessage("Error, ${e.message}")
            return false
         }
    }

    override fun selectAll(): List<GroupEntity>? {
        val sql = "SELECT * FROM grupos"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    val groups = mutableListOf<GroupEntity>()
                    while (rs.next()) {
                        groups.add(
                            GroupEntity(
                                grupoId = rs.getInt("grupoid"),
                                grupoDesc = rs.getString("grupodesc"),
                                mejorPosCTFSId = rs.getInt("mejorposCTFid")
                            )
                        )
                    }
                    groups
                }
            }
        } catch (e: SQLException) {
            console.showMessage("Error, ${e.message}")
            null
        }
    }

    override fun selectById(groupId: Int): GroupEntity? {

        val sql = "SELECT * FROM GRUPOS WHERE GRUPOID = ?"

          return try {

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, groupId)

                    val rs = stmt.executeQuery()

                    if (rs.next()) {
                        GroupEntity(
                           grupoId = rs.getInt("GRUPOID"),
                           grupoDesc = rs.getString("GRUPODESC"),
                           mejorPosCTFSId = rs.getInt("MEJORPOSCTFID")
                       )
                    } else {
                       null
                    }
                }
            }
        } catch (e:SQLException){
             console.showMessage("Error, ${e.message}")
            return null
        }
    }

    /**
     * Actualiza el campo `mejorposctfid` de un grupo a la mejor posicion que tenga
     *
     * @param grupoId El ID del grupo a actualizar.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    override fun updateMejorPos(grupoId: Int, mejorpos:Int): Boolean {
        val sql = "UPDATE GRUPOS SET mejorposctfid = ? WHERE GRUPOID = ?"

        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->

                    stmt.setInt(1, mejorpos)
                    stmt.setInt(2, grupoId)
                    val rs = stmt.executeUpdate()

                    if (rs ==1) {
                        return true
                    } else{
                        console.showMessage("Error, update query failed ($rs) rows updated")
                        conn.rollback()
                        return false
                    }
                }
            }
        }catch (e:SQLException){
            console.showMessage("Error, ${e.message}")
            return false
        }
    }


    /**
     * Realiza una consulta SQL para seleccionar ctfid, puntuacion, mejorPosCtfid, groupdesc, de un grupo especifico
     */
    override fun selectJoinGroup(grupoId: Int): Map<String,List<Triple<Int,Int,Int>>>?{

        val sql = "SELECT C.CTFID, C.PUNTUACION, G.MEJORPOSCTFID, G.GRUPODESC FROM GRUPOS G,CTFS C WHERE C.GRUPOID = G.GRUPOID AND G.GRUPOID = ?"
         //Triple con el ctfId, puntuacion y posicion
        val mapaFinal = mutableMapOf<String, MutableList<Triple<Int,Int,Int>>>()
        try {

            dataSource.connection.use { conn ->

                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, grupoId)
                    val rs = stmt.executeQuery()

                    while (rs.next()) {
                        //Saco todos los valores dado por la sentencia select
                        val idCtf = rs.getInt(1)
                        val puntuacion = rs.getInt(2)
                        val posicion = rs.getInt(3)
                        val grupodesc = rs.getString(4)


                        val tripleResultados= Triple(idCtf,puntuacion,posicion) //creo un valor de triples con los datos
                        // Y los añado al mapa
                        if (mapaFinal.containsKey(grupodesc)) {
                            mapaFinal[grupodesc]?.add(tripleResultados)
                        } else {
                            mapaFinal[grupodesc] = mutableListOf(tripleResultados)
                        }

                    }

                }
            }
            return mapaFinal
        } catch (e:SQLException){
            return null
        }

    }

    /**
     * Realiza una consulta SQL para seleccionar todos los grupos y sus respectivas puntuaciones, ctfid, mejorposicion en un ctf y la descripcion del grupo.
     * haciendo uso de las claves primarias para no obtener un producto cartesiano al hacer el join.
     *
     * @return Un mapa con toda la informacion necesaria para mostrarla en otras funciones.
     */
    override fun selectAllGroups(): Map<Pair<String,Int>,List<Triple<Int,Int,Int>>>?{

        val sql = "SELECT C.CTFID, C.PUNTUACION, G.MEJORPOSCTFID, G.GRUPODESC, G.GRUPOID FROM GRUPOS G,CTFS C WHERE C.GRUPOID = G.GRUPOID" //descendente para que aparezca de mayor a menor
        //Pair < grupoDesc grupoId> Triple  <ctfId, puntuacion y posicion>
        val mapaFinal = mutableMapOf<Pair<String,Int>, MutableList<Triple<Int,Int,Int>>>()
        try {

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()

                    while (rs.next()) {

                        val idCtf = rs.getInt(1)
                        val puntuacion = rs.getInt(2)
                        val mejorposicion = rs.getInt(3)
                        val grupodesc = rs.getString(4)
                        val grupoId = rs.getInt(5)

                        val tripleResultados= Triple(idCtf,puntuacion,mejorposicion) //creo un valor de triples con los datos
                        val pairResultados = Pair(grupodesc,grupoId)

                        // los añado al map
                        if (mapaFinal.containsKey(pairResultados)) {
                            mapaFinal[pairResultados]?.add(tripleResultados)
                        } else {
                            mapaFinal[pairResultados] = mutableListOf(tripleResultados)
                        }
                    }
                }
            }
            return mapaFinal
        } catch (e:SQLException){
            return null
        }
    }

    /**
     * Funcion que borrar un grupo dependiendo del id que se le pase, esta hecha para no tener commit ni rollback ya que va a funcionar en un procesamiento por lotes
     * lo que significa que la funcion que llame a esta funcion va a controlar los errorres de esta.
     */
    override fun deleteByIdConexion(groupId: Int,conexion: Connection?):Boolean {

        val sql = "DELETE FROM GRUPOS WHERE grupoid = ?"
        var stmt: PreparedStatement? = null
        try {

            stmt = conexion?.prepareStatement(sql)

            stmt?.setInt(1, groupId)

            val resultado = stmt?.executeUpdate()

            if (resultado ==1){
                return true
            }else{
                return false
            }

        } catch (e: SQLException) {
            return false
        }
    }

    /**
     * Actualiza el campo `mejorposctfid` de un grupo a null utilizando una conexión existente ya que se usará en un procesamiento por lotes.
     *
     */
    override fun updateANullConexion(grupoId: Int, conexion: Connection?): Boolean {
        val sql = "UPDATE GRUPOS SET mejorposctfid = ? WHERE GRUPOID = ?"
        var stmt: PreparedStatement? = null
        var resultado = false

        try {

            stmt = conexion?.prepareStatement(sql)
            stmt?.setObject(1, null)
            stmt?.setInt(2, grupoId)

            val rs = stmt?.executeUpdate()

            if (rs ==1) {
                 resultado = true
            }

        }catch (e:SQLException){
            console.showMessage("Error, ${e.message}")
            resultado = false
        }
        return resultado
    }

}