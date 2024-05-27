package dao

import entity.CtfsEntity
import java.sql.Connection

interface ICtfsDao {
    fun insert(ctfid: Int, grupoId: Int, puntuacion: Int): Boolean
    fun update( ctfId:Int, grupoId: Int, puntuacion: Int, grupoDesc: String): Boolean
    fun deleteById(ctfsId: Int,grupoId: Int): Boolean
    fun selectJoin(ctfId: Int): Map<String,Int>?
    fun deleteByIdConexion(grupoId: Int, conexion: Connection? ): Boolean
    fun existe(ctfID: Int, grupoID: Int): Boolean
    fun selectCtf(grupoid:Int):List<Int>?
    fun selectAll(): List<CtfsEntity>?

}