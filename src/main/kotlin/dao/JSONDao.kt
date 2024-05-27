package dao

import entity.GroupEntity
import java.sql.Connection

class JSONDao: IGroupDao {
    override fun insert(grupoDesc: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateMejorPos(grupoId: Int, mejorpos: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun selectAll(): List<GroupEntity>? {
        TODO("Not yet implemented")
    }

    override fun selectById(groupId: Int): GroupEntity? {
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
}