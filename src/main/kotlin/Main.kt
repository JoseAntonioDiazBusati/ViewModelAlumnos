import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import java.io.File

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