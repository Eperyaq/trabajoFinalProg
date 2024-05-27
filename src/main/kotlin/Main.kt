import DB_Connection.DataSourceFactory
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.singleWindowApplication
import dao.CtfsDao
import dao.GroupDao
import factory.JSONDAOFactory
import factory.SQLDAOFactory
import factory.XMLDAOFactory
import gestorFicheros.Ficheros
import management.Management
import output.Console
import service.CtfsService
import service.GroupService



fun main(args: Array<String>) = application {

    val windowState = rememberWindowState(size = DpSize(1200.dp, 800.dp))
    val icon = BitmapPainter(useResource("Perrete.jpg", ::loadImageBitmap))

    val consola = Console()
    val dataSourceJdbc = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.JDBC)

    if (dataSourceJdbc != null) {
        val fichero = Ficheros(consola)


        val tipo = fichero.leerIni("un9pe.ini")
        val tipoDao = when (tipo) {
            "SQL" -> SQLDAOFactory(consola, dataSourceJdbc)
            "XML" -> XMLDAOFactory(consola, dataSourceJdbc)
            "JSON" -> JSONDAOFactory(consola, dataSourceJdbc)

            else -> {
                consola.showMessage("ERROR configuracion no válida, se va a usar SQL")
                SQLDAOFactory(consola, dataSourceJdbc)
            }
        }

        val groupDao = tipoDao.getGroupDao()
        val ctfsDao = tipoDao.getCtfDao()

        val ctfsService = CtfsService(ctfsDao)

        val groupService = GroupService(groupDao)

        val management = Management(groupService, ctfsService, fichero, consola)


        val test = arrayOf("-l")
        if (test.isNotEmpty()) {
            management.entrada(test)
            Window(
                onCloseRequest = { exitApplication() },
                title = "Grupos y Ctfs",
                state = windowState,
                icon = icon
            ){
                management.abrirUi()
            }

        } else {
            consola.showMessage("No se han encontrdo argumentos")
        }

//        if (args.isNotEmpty()) {
//            management.entrada(args)
//            Window(
//                onCloseRequest = { exitApplication() },
//                title = "Grupos y Ctfs",
//                state = windowState,
//                icon = icon
//            ){
//                management.abrirUi()
//            }
//
//        } else {
//            consola.showMessage("No se han encontrdo argumentos")
//        }

    }else{
        consola.showMessage("Error, la base de datos esta siendo usada")
    }




}