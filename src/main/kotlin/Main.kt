import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application

fun main() = application {

    val icon = painterResource("sample.png")
    val windowState = GetWindowState(
        windowWidth = 800.dp,
        windowHeight = 800.dp
    )
    StudentWindow(
        title = "Alumnos",
        icon = icon,
        windowState = windowState,
        resizable = false,
        onCloseMainWindow = { exitApplication() }
    )
}

@Composable
fun SelectMethodButtons(
    onFileMethodSelected: () -> Unit,
    onDatabaseMethodSelected: () -> Unit
) {
    Column {
        Button(
            onClick = onFileMethodSelected,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Manejo de estudiantes por archivos")
        }
        Button(
            onClick = onDatabaseMethodSelected,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Manejo de estudiantes por base de datos")
        }
    }
}