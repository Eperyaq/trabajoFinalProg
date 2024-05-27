package factory

import dao.ICtfsDao
import dao.IGroupDao
import output.IOutputInfo
import javax.sql.DataSource

class JSONDAOFactory(private val consola: IOutputInfo, private val datasource: DataSource): IDAOFactory {
    override fun getGroupDao(): IGroupDao {
        TODO("Not yet implemented")
    }

    override fun getCtfDao(): ICtfsDao {
        TODO("Not yet implemented")
    }
}