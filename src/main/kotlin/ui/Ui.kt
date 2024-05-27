package ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import entity.CtfsEntity
import entity.GroupEntity
import gestorFicheros.Ficheros
import output.Console
import java.io.File

class Ui {
    @Composable
    fun MyApp(grupos: List<GroupEntity>, ctfs: List<CtfsEntity>) {
        var datosBusqueda by remember { mutableStateOf("") }
        var estadoGrupos by remember { mutableStateOf(grupos) }

        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            // Lazy column con la lista de los grupos

            LazyColumn(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(
                    border = BorderStroke(2.dp, Color.Gray),
                    shape = RoundedCornerShape(4.dp)
                )

            ) {
                items(estadoGrupos.size) { index ->
                    val grupo = estadoGrupos[index]
                    Text(modifier = Modifier.padding(5.dp),
                        text="GrupoId: ${grupo.grupoId}, Descripción: ${grupo.grupoDesc}, Mejor posición CTF ID: ${grupo.mejorPosCTFSId}")
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Parte de abajo de la ventana
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = datosBusqueda,
                    onValueChange = { datosBusqueda = it },
                    label = { Text("Grupo ID o Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = {
                            estadoGrupos = if (datosBusqueda.isBlank()) {
                                grupos
                            } else {
                                estadoGrupos.filter {
                                    it.grupoId.toString() == datosBusqueda || it.grupoDesc == datosBusqueda
                                }
                            }
                            datosBusqueda = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Filtrar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            exportarClasificacion(estadoGrupos, ctfs)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Exportar")
                    }
                }
            }
        }
    }

    private fun exportarClasificacion(groups: List<GroupEntity>, ctfs: List<CtfsEntity>) {
        val stringBuilder = StringBuilder()
        val agrupadoCtf = groups.groupBy { it.mejorPosCTFSId }
        val ctfsAgrupados = ctfs.groupBy { it.CTFid }

        agrupadoCtf.forEach { (ctfId, listaGrupos) ->
            stringBuilder.append(if (ctfId == 0) "Grupos sin Participaciones \n" else "Ctf: $ctfId \n")

            listaGrupos.forEachIndexed { index, grupo ->
                val puntos = ctfsAgrupados[ctfId]?.find { it.grupoId == grupo.grupoId }?.puntuacion ?: "sin"
                stringBuilder.append("${index + 1}. GrupoId: ${grupo.grupoId} Descripcion: ${grupo.grupoDesc} ($puntos puntos)\n")
            }
            stringBuilder.append("\n")
        }

        val file = File("clasificacion.txt")

        Ficheros(Console()).escribir(file,stringBuilder.toString())

    }
}