package service

import dao.ICtfsDao
import entity.CtfsEntity
import java.sql.Connection

/**
 * Servicio para la gestión de CTFs (Capture The Flags), implementando la interfaz [ICtfsService].
 *
 * @property ctfsDao El DAO (Data Access Object) utilizado para interactuar con la base de datos.
 */
class CtfsService(private val ctfsDao: ICtfsDao) : ICtfsService {

    /**
     * Inserta una nueva puntuación para un grupo en un CTF específico.
     *
     * @param ctfid El ID del CTF.
     * @param grupoId El ID del grupo.
     * @param puntuacion La puntuación obtenida por el grupo.
     * @return La entidad `CtfsEntity` recién insertada, o `null` si la inserción falla.
     */
    override fun insert(ctfid: Int, grupoId: Int, puntuacion: Int): Boolean {
        return ctfsDao.insert(ctfid, grupoId, puntuacion)
    }

    /**
     * Actualiza la puntuación de un grupo en un CTF específico.
     *
     * @param ctfId El ID del CTF.
     * @param grupoId El ID del grupo.
     * @param puntuacion La nueva puntuación para el grupo.
     * @return La entidad `CtfsEntity` actualizada, o `null` si la actualización falla.
     */
    override fun update(ctfId: Int, grupoId: Int, puntuacion: Int, grupoDesc: String): Boolean {
        return ctfsDao.update(ctfId, grupoId, puntuacion, grupoDesc)
    }

    /**
     * Elimina la puntuación de un grupo en un CTF específico por su ID.
     *
     * @param ctfsId El ID de la entidad CTF.
     * @param grupoId El ID del grupo.
     * @return `true` si la eliminación fue exitosa, `false` en caso contrario.
     */
    override fun deleteById(ctfsId: Int, grupoId: Int): Boolean {
        return ctfsDao.deleteById(ctfsId, grupoId)
    }

    /**
     * Selecciona un mapa con la descripción del grupo y la puntuación asociada con un CTF específico.
     *
     * @param ctfid El ID del CTF.
     * @return Un mapa con la descripción del grupo como clave y la puntuación como valor, o `null` si no se encuentra ningún dato.
     */
    override fun selectJoin(ctfid: Int): Map<String, Int>? {
        return ctfsDao.selectJoin(ctfid)
    }
    /**
     * Elimina una entidad CTF por el ID del grupo utilizando una conexión específica.
     *
     * @param grupoId El ID del grupo.
     * @param conexion La conexión a la base de datos.
     * @return `true` si la eliminación fue exitosa, `false` en caso contrario.
     */
    override fun deleteByIdConexion( grupoId: Int, conexion: Connection?): Boolean {
        return ctfsDao.deleteByIdConexion(grupoId, conexion)
    }
    /**
     * Comprueba si una entidad CTF existe para un grupo específico.
     *
     * @param ctfID El ID del CTF.
     * @param grupoID El ID del grupo.
     * @return `true` si la entidad existe, `false` en caso contrario.
     */
    override fun existe(ctfID: Int, grupoID: Int): Boolean {
        return ctfsDao.existe(ctfID, grupoID)
    }
    /**
     * Selecciona todos los IDs de CTF asociados con un grupo específico.
     *
     * @param grupoid El ID del grupo.
     * @return Una lista de IDs de CTF, o `null` si no se encuentra ningún dato.
     */
    override fun selectCtf(grupoid: Int): List<Int>? {
        return ctfsDao.selectCtf(grupoid)
    }
    /**
     * Selecciona todas las entidades `CtfsEntity`.
     *
     * @return Una lista de todas las entidades `CtfsEntity`, o `null` si no se encuentra ninguna.
     */
    override fun selectAll(): List<CtfsEntity>? {
        return ctfsDao.selectAll()
    }
}
