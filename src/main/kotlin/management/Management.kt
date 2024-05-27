package management

import DB_Connection.DataSourceFactory
import androidx.compose.runtime.Composable
import service.IGroupService
import gestorFicheros.IFicheros
import output.IOutputInfo
import service.ICtfsService
import ui.Ui
import java.sql.Connection
import java.sql.SQLException

/**
 * Clase que gestiona las operaciones relacionadas con grupos y CTFs.
 *
 * @param grupoService Servicio para operaciones relacionadas con grupos.
 * @param ctfsService Servicio para operaciones relacionadas con CTFs.
 * @param ficheros Servicio para la gestión de ficheros.
 * @param consola Servicio para mostrar mensajes en la consola.
 */
class  Management(private val grupoService: IGroupService, private val ctfsService : ICtfsService, private val ficheros: IFicheros, private val consola: IOutputInfo) {

    /**
     * Elimina un grupo y sus participaciones en CTFs de manera transaccional.
     *
     * @param grupoId El ID del grupo a eliminar.
     */
    private fun borrarGrupoLotes(grupoId: Int){

        var conexion: Connection? = null

        try {
            val datos = ctfsService.selectCtf(grupoId)
            val grupo = grupoService.selectById(grupoId)
            conexion = DataSourceFactory.getConnection()

            conexion?.autoCommit = false

            val updateNull = grupoService.updateANullConexion(grupoId, conexion) //Primero updateamos a null la mejorPos del grupo para poder borrarlo
            if (!updateNull){
                throw SQLException("Error Cambiando a null el valor")
            }


            val borrarCtfs = ctfsService.deleteByIdConexion(grupoId, conexion) //Despues borramos las participaciones de ese grupo en los ctf en los que haya participado
            if (!borrarCtfs){
                throw SQLException("ERROR, ha fallado a la hora de borrar el grupo del ctfs")

            }

            val borrarGrupo = grupoService.deleteByIdConexion(grupoId,conexion) //y por ultimo borramos el grupo de la tabla grupos
            if (!borrarGrupo){
                throw SQLException("ERROR, ha fallado a la hora de borrar el grupo")

            }


            if (datos != null) {
                val participaciones = if (datos.isEmpty()) "cuyas participaciones son: 0" else "y su participación en los ctfs $datos"

                consola.showMessage("Procesado: Eliminado el grupo \"${grupo?.grupoDesc}\" $participaciones")

            }
        } catch (e:SQLException){
            consola.showMessage("Ha habido algun error: ${e.message}")
            conexion?.rollback()
        } finally {
            conexion?.autoCommit= true
            DataSourceFactory.closeDB(conexion)
        }

    }

    private fun obtenerMejorPosicionCTF(): Map<Int, Int> {
        //mapa <grupoId, mejorCtf>
        val grupos = grupoService.selectAll()
        val ctfs = ctfsService.selectAll()


        val mejorCTFPorGrupo = mutableMapOf<Int, Int>()

        if (grupos != null) {
            for (grupo in grupos) {
                val ctfDelGrupo = ctfs?.filter { it.grupoId == grupo.grupoId } //Busca el grupoId que coincida con el grupoId actual de esa vuelta
                if (ctfDelGrupo?.isNotEmpty() == true) {
                    val mejorCTF = ctfDelGrupo.maxByOrNull { it.puntuacion }!! //Busca el ctf de un grupo con su puntuacion mas alta asegurando que no sea nulo con !!
                    mejorCTFPorGrupo[grupo.grupoId] = mejorCTF.CTFid //le da valores al mapa
                }
            }
        }

        return mejorCTFPorGrupo
    }

    private fun posicionesEnCtf(): Map<Int, List<Int>> {
        //mapa<grupoId, posicion en un ctf especifico>
        val ctfs = ctfsService.selectAll()

        val posicionesPorGrupo = mutableMapOf<Int, MutableList<Int>>()


        val ctfsAgrupadosPorCTFid = ctfs?.groupBy { it.CTFid }// Agrupa los CTFs por CTFid


        ctfsAgrupadosPorCTFid?.forEach { (_, ctfsDeUnCTF) -> // Iterar sobre cada grupo de CTF

            val ctfsOrdenados = ctfsDeUnCTF.sortedByDescending { it.puntuacion } // Ordenar los CTFs de este grupo por puntuacion en orden descendente

            ctfsOrdenados.forEachIndexed { index, ctf -> //Asigna las posiciones
                val grupoId = ctf.grupoId
                val posicion = index + 1
                val listaPosiciones = posicionesPorGrupo.getOrPut(grupoId) { mutableListOf() }
                listaPosiciones.add(posicion)
            }
        }

        return posicionesPorGrupo
    }


    /**
     * Updatea las posiciones a las mejores posiciones que haya tenido el grupo en los ctf que haya participado
     * @param grupoPos Mapa con el grupoId y la posicion que haya quedado
     */
    private fun updatearMejorPosicion(grupoPos: Map<Int, Int>){
        for (grupos in grupoPos){
            grupoService.updateMejorPos(grupoId = grupos.key, mejorpos = grupos.value)
        }
    }


    @Composable
    /**
     * Procesa y ejecuta comandos separados desde un archivo.
     *
     * @param ruta La ruta del archivo que contiene los comandos.
     */
    private fun comandosSeparados(ruta:String){

        val comandos = ficheros.sacarComandosSeparados(ruta)

        for ((clave, valor) in comandos) {
            for (i in valor) {
                val parametro = mutableListOf(clave)
                for (j in i) {
                    parametro.add(j)
                }
                entrada(parametro.toTypedArray())
            }

        }
    }

    /**
     * Abre la interfaz gráfica
     */
    @Composable
     fun abrirUi(){
        val listaGrupos = grupoService.selectAll()
        val listaCtfs = ctfsService.selectAll()
        if (listaGrupos != null && listaCtfs != null){
            Ui().MyApp(listaGrupos, listaCtfs)
        }
    }

    @Composable
    /**
     * Procesa los argumentos de entrada y realiza las acciones correspondientes.
     *
     * @param argumentos Array de argumentos pasados a la aplicación.
     */
    fun entrada(argumentos: Array<String>){

//Proyecto de futuro pasar eso a miniFunciones y que cada comando llame a una sola funcion en vez de hacerlo ahi
        when (argumentos[0]){

            "-g" -> {
                if (argumentos.size == 2) {
                    try {

                        val grupoDesc = argumentos[1]

                        grupoService.insert(grupoDesc) //GroupDesc
                    } catch (e: Exception) {
                        consola.showMessage("ERROR, el parametro <grupoDesc> debe ser de tipo String")
                    }
                } else {
                    consola.showMessage("Error, el numero de argumentos es invalido")
                }
            }

            "-p" -> {
                if (argumentos.size == 4) {
                    try {
                        val ctfId = argumentos[1].toInt()
                        val grupoId = argumentos[2].toInt()
                        val puntuacion = argumentos[3].toInt()

                        val comprobar = grupoService.selectById(argumentos[2].toInt())

                        if (comprobar != null) {
                            val existe = ctfsService.existe(ctfId, grupoId)
                            if (existe) {
                                ctfsService.update(ctfId, grupoId, puntuacion, comprobar.grupoDesc)
                                updatearMejorPosicion(obtenerMejorPosicionCTF()) //Updatea la mejor posicion
                            } else {
                                ctfsService.insert(ctfId, grupoId, puntuacion) //ctfid,grupoid,puntuacion
                                updatearMejorPosicion(obtenerMejorPosicionCTF())//updatea la mejor posicion
                            }
                        } else {
                            consola.showMessage("ERROR, El grupo indicado no existe")
                        }
                        //mejorPos(argumentos[1].toInt())
                    } catch (e: NumberFormatException) {
                        consola.showMessage("ERROR, los parametros a introducir han de ser todos de tipo entero.")
                    } catch (e: Exception) {
                        consola.showMessage("Error, ${e.message} ")
                    }

                } else {
                    consola.showMessage("Error, numero de argumentos invalidos")
                }
            }

            "-t" -> {
                if (argumentos.size == 2) {

                    try {

                        val grupoId = argumentos[1].toInt()
                        borrarGrupoLotes(grupoId)
                        updatearMejorPosicion(obtenerMejorPosicionCTF())
                    } catch (e: NumberFormatException) {
                        consola.showMessage("ERROR, el parametro <grupoId> Debe ser de tipo entero.")
                    }

                } else {
                    consola.showMessage("Error, numero de argumentos invalidos")
                }
            }

            "-e" -> {
                if (argumentos.size == 3) {
                    try {

                        val ctfId = argumentos[1].toInt()
                        val grupoId = argumentos[2].toInt()
                        val borrado = ctfsService.deleteById(ctfId, grupoId)//ctfid, grupoid
                        updatearMejorPosicion(obtenerMejorPosicionCTF())
                        if (!borrado) {
                            consola.showMessage("ERROR, el grupo o el ctf no existen")
                        }
                    } catch (e: Exception) {
                        consola.showMessage("ERROR, el valor dado es incorrecto.")
                    }

                } else {
                    consola.showMessage("Error, numero de argumentos invalidos")
                }
            }

            "-l" -> {
                if (argumentos.size == 2) {
                    try {
                        val grupoId = argumentos[1].toInt()
                        val mapa = grupoService.selectJoinGroup(grupoId)
                        consola.mostrarGruposBonitos(
                            grupoId,
                            mapa
                        ) //Si la longitud de argumentos es igual a 1 es decir tiene el -l y el id del grupo saca el id
                    } catch (e: Exception) {
                        consola.showMessage("ERROR, el valor dado es incorrecto.")
                    }

                } else if (argumentos.size == 1) {
                    val mapaValores = grupoService.selectAllGroups()
                    consola.mostrarTodosLosGrupos(mapaValores)//grupoid, si no los muestra todos
                } else {
                    consola.showMessage("Error, numero de argumentos invalido")
                }
            }

            "-c" -> {
                if (argumentos.size == 2) {
                    try {
                        val ctfId = argumentos[1].toInt()

                        val mapas = ctfsService.selectJoin(ctfId)
                        consola.mostrarCtfBonito(ctfId, mapas)
                    } catch (e: Exception) {
                        consola.showMessage("ERROR, el valor dado es incorrecto.")
                    }

                } else {
                    consola.showMessage("Error, el numero de parametros no es adecuado")
                }
            }

            "-f" -> {

                if (argumentos.size == 2) {

                        val ruta = argumentos[1]
                        comandosSeparados(ruta)

                } else {
                    consola.showMessage("Error, numero de argumentos invalidos")
                }
            }


            "-i" -> {
                consola.showMessage("Abriendo ui...")
            }
        }
    }
}