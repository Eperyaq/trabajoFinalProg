package service

import entity.GroupEntity
import java.sql.Connection

interface IGroupService {

    fun insert(grupoDesc: String): Boolean
    fun selectById(grupoId: Int): GroupEntity?
    fun selectJoinGroup(grupoId: Int): Map<String,List<Triple<Int,Int,Int>>>?
    fun selectAllGroups(): Map<Pair<String,Int>,List<Triple<Int,Int,Int>>>?
    fun deleteByIdConexion(groupId: Int, conexion: Connection?):Boolean
    fun updateANullConexion(grupoId: Int, conexion: Connection?): Boolean
    fun selectAll(): List<GroupEntity>?
    fun updateMejorPos(grupoId: Int, mejorpos:Int): Boolean

}