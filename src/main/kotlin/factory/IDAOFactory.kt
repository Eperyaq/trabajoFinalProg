package factory
import dao.ICtfsDao
import dao.IGroupDao
import factory.SQLDAOFactory
interface IDAOFactory {

    fun getGroupDao(): IGroupDao
    fun getCtfDao():ICtfsDao
}