package service

import dao.GroupDao
import dao.IGroupDao
import entity.GroupEntity
import java.sql.Connection

class GroupService(private val grupoDao: IGroupDao): IGroupService {

    override fun insert(grupoDesc: String): Boolean {
        return grupoDao.insert(grupoDesc)
    }

    override fun selectById(grupoId: Int): GroupEntity? {
        return grupoDao.selectById(grupoId)
    }

    override fun selectJoinGroup(grupoId: Int): Map<String,List<Triple<Int,Int,Int>>>? {
        return grupoDao.selectJoinGroup(grupoId)
    }

    override fun selectAllGroups(): Map<Pair<String,Int>,List<Triple<Int,Int,Int>>>? {
        return grupoDao.selectAllGroups()
    }

    override fun deleteByIdConexion(groupId: Int, conexion: Connection?): Boolean {
        return grupoDao.deleteByIdConexion(groupId, conexion)
    }

    override fun updateANullConexion(grupoId: Int, conexion: Connection?): Boolean {
        return grupoDao.updateANullConexion(grupoId, conexion)
    }

    override fun selectAll(): List<GroupEntity>? {
        return grupoDao.selectAll()
    }

    override fun updateMejorPos(grupoId: Int, mejorpos: Int): Boolean {
        return grupoDao.updateMejorPos(grupoId, mejorpos)
    }
}