package dao

import entity.GroupEntity
import java.sql.Connection

interface IGroupDao {
    fun insert(grupoDesc: String): Boolean
    fun updateMejorPos(grupoId: Int, mejorpos:Int): Boolean
    fun selectAll(): List<GroupEntity>?
    fun selectById(groupId: Int): GroupEntity?
    fun selectJoinGroup(grupoId: Int): Map<String,List<Triple<Int,Int,Int>>>?
    fun selectAllGroups(): Map<Pair<String,Int>,List<Triple<Int,Int,Int>>>?
    fun deleteByIdConexion(groupId: Int, conexion: Connection?):Boolean
    fun updateANullConexion(grupoId: Int, conexion: Connection?): Boolean

}