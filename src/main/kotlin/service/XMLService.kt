package service

import entity.GroupEntity
import java.sql.Connection

class XMLService:IGroupService {
    override fun insert(grupoDesc: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun selectById(grupoId: Int): GroupEntity? {
        TODO("Not yet implemented")
    }

    override fun selectJoinGroup(grupoId: Int): Map<String, List<Triple<Int, Int, Int>>>? {
        TODO("Not yet implemented")
    }

    override fun selectAllGroups(): Map<Pair<String, Int>, List<Triple<Int, Int, Int>>>? {
        TODO("Not yet implemented")
    }

    override fun deleteByIdConexion(groupId: Int, conexion: Connection?): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateANullConexion(grupoId: Int, conexion: Connection?): Boolean {
        TODO("Not yet implemented")
    }

    override fun selectAll(): List<GroupEntity>? {
        TODO("Not yet implemented")
    }

    override fun updateMejorPos(grupoId: Int, mejorpos: Int): Boolean {
        TODO("Not yet implemented")
    }
}