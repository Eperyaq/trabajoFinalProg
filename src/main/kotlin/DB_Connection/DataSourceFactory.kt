package DB_Connection

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import factory.SQLDAOFactory
import org.h2.jdbcx.JdbcDataSource
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource
import kotlin.contracts.contract

object DataSourceFactory {
    enum class DataSourceType {
        HIKARI,
        JDBC
    }

    private const val URL = "jdbc:h2:./default"
    private const val USUARIO = "user"
    private const val CONTRASENIA = "user"

    fun getDS(dataSourceType: DataSourceType): DataSource? {
            try {
                when (dataSourceType) {
                    DataSourceType.HIKARI -> {
                        val config = HikariConfig()
                        config.jdbcUrl = "jdbc:h2:./default"
                        config.username = "user"
                        config.password = "user"
                        config.driverClassName = "org.h2.Driver"
                        config.maximumPoolSize = 10
                        config.isAutoCommit = true
                        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                        return HikariDataSource(config)
                    }

                    DataSourceType.JDBC -> {
                        val jdbcUrl = "jdbc:h2:./default"
                        val username = "user"
                        val password = "user"
                        val dataSource = JdbcDataSource()
                        dataSource.setURL(jdbcUrl)
                        dataSource.user = username
                        dataSource.password = password
                        return dataSource
                    }
                }
            }catch (_: SQLException) {
                return null
            }

    }

    fun getConnection(): Connection?{
        var conexion : Connection? = null

        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA) //Dar conexion con la base de datos
        } catch ( e: SQLException){
            e.printStackTrace()
        }

        return conexion
    }

    /**
     * Funcion que cierra la database
     * @param conexion Conexion que hay que cerrar
     */
    fun closeDB(conexion:Connection?) {
        try {
            conexion?.close()
        } catch (e:SQLException){
            e.printStackTrace()
        }
    }

}