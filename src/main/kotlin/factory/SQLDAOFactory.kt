package factory

import dao.CtfsDao
import dao.GroupDao
import dao.ICtfsDao
import dao.IGroupDao
import output.IOutputInfo
import javax.sql.DataSource

class SQLDAOFactory(private val consola: IOutputInfo, private val datasource: DataSource) : IDAOFactory{

    /**
     * Implementación de la función `getCtfDao` que devuelve una instancia de `ICtfsDao`.
     *
     * @return Una instancia de `ICtfsDao` creada con `CtfsDao` y los parámetros `consola` y `datasource`.
     */
    override fun getCtfDao(): ICtfsDao{
        val ctfsDao = CtfsDao(consola,datasource)

        return ctfsDao
    }

    /**
     * Implementación de la función `getGroupDao` que devuelve una instancia de `IGroupDao`.
     *
     * @return Una instancia de `IGroupDao` creada con `GroupDao` y los parámetros `consola` y `datasource`.
     */
    override fun getGroupDao(): IGroupDao{
        val groupDao = GroupDao(consola, datasource)

        return groupDao

    }



}