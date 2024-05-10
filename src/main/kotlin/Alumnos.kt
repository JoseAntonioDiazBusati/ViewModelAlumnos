import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.Toolkit
import java.io.File

@Composable
fun StudentWindow(
    title: String,
    icon: Painter,
    windowState: WindowState,
    resizable: Boolean,
    onCloseMainWindow: () -> Unit,
) {
    Window(
        onCloseRequest = onCloseMainWindow,
        title = title,
        icon = icon,
        resizable = resizable,
        state = windowState
    ) {

        val fileManagement = GestorFiles()
        val studentsFile = File("studentList.txt")
        val studentViewModel = StudentViewModel(fileManagement, studentsFile)

        MaterialTheme {
            Surface(
                color = colorWindowBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                StudentScreen(studentViewModel)
            }
        }
    }
}

@Composable
fun GetWindowState(
    windowWidth: Dp,
    windowHeight: Dp,
): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height


    val positionX = (screenWidth / 2 - windowWidth.value.toInt() / 2)
    val positionY = (screenHeight / 2 - windowHeight.value.toInt() / 2)

    return rememberWindowState(
        size = DpSize(windowWidth, windowHeight),
        position = WindowPosition(positionX.dp, positionY.dp)
    )
}

@Composable
fun StudentScreen(
    viewModel : IStudentViewModel
) {
    val newStudentFocusRequester = remember { FocusRequester() }
    val studentListFocusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {  // key1 = true asegura que esto se ejecute solo una vez
        viewModel.cargarAlumnos()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().weight(3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AddNewStudent(
                newStudent = viewModel.nuevoAlumno.value,
                focusRequester = newStudentFocusRequester,
                onNewStudentChange = { name -> viewModel.newStudentChange(name) },
                onButtonAddNewStudentClick = {
                    viewModel.anadirAlumno()
                    newStudentFocusRequester.requestFocus()
                }
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                StudentList(
                    studentsState = viewModel.alumnos,
                    selectedIndex = viewModel.seleccionarId.value,
                    focusRequester = studentListFocusRequester,
                    onStudentSelected = { index -> viewModel.seleccionAlumno(index) },
                    onIconDeleteStudentClick = { index -> viewModel.eliminarAlumno(index) },
                    onButtonClearStudentsClick = { viewModel.limpiarAlumnos() }
                )
                ImageUpDownScroll(
                    showImgScrollStudentList = viewModel.showScrollStudentListImage(),
                )
            }
        }
        SaveChangesButton(
            modifier = Modifier.fillMaxSize().weight(1f),
            onButtonSaveChangesClick = {
                viewModel.guardarAlumnos()
                newStudentFocusRequester.requestFocus()
            }
        )
    }

    if (viewModel.mostrarMensaje.value) {
        InfoMessage(
            message = viewModel.infoMensaje.value,
            onCloseInfoMessage = {
                viewModel.mostrarMensajes(false)
                newStudentFocusRequester.requestFocus()
            }
        )
    }

    LaunchedEffect(viewModel.alumnos.size) {
        newStudentFocusRequester.requestFocus()
    }
}

@Composable
fun AddNewStudent(
    newStudent: String,
    focusRequester: FocusRequester,
    onNewStudentChange: (String) -> Unit,
    onButtonAddNewStudentClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = 20.dp)
            .onKeyEvent { event ->
                controlKeyEnter(
                    event = event,
                    onButtonAddNewStudentClick = { onButtonAddNewStudentClick() }
                )
            }
    ) {
        StudentTextField(
            newStudent = newStudent,
            focusRequester = focusRequester,
            onNewStudentChange = onNewStudentChange
        )
        AddStudentButton(
            onButtonAddNewStudentClick = onButtonAddNewStudentClick
        )
    }
}

@Composable
fun StudentTextField(
    newStudent: String,
    focusRequester: FocusRequester,
    onNewStudentChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .focusRequester(focusRequester),
        value = newStudent,
        onValueChange = onNewStudentChange,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(text = "Nuevo alumno ")
                Text(
                    text = "(10 chars max.)",
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
        },
        maxLines = 1,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = colorFocusComponentsBackground
        )
    )
}

@Composable
fun AddStudentButton(
    onButtonAddNewStudentClick: () -> Unit,
) {
    Button(
        modifier = Modifier.padding(15.dp),
        onClick = onButtonAddNewStudentClick,
    ) {
        Text(text = "Añadir nuevo alumn@")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StudentList(
    studentsState: List<String>,
    selectedIndex: Int,
    focusRequester: FocusRequester,
    onStudentSelected: (Int) -> Unit,
    onIconDeleteStudentClick: (Int) -> Unit,
    onButtonClearStudentsClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Alumnos: ${studentsState.size}",
            modifier = Modifier.padding(bottom = 5.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(0.78f)
                .width(240.dp)
                .background(colorFocusComponentsBackground)
                .border(2.dp, colorBorder)
                .padding(10.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && selectedIndex >= 0) {
                        onStudentSelected(selectedIndex)
                    }
                }
                .onKeyEvent { event ->
                    selectRowScrolling(
                        event = event,
                        selectedIndex = selectedIndex,
                        maxRow = studentsState.size - 1,
                        onStudentSelected = { onStudentSelected(it) }
                    )
                }
        ) {
            items(studentsState.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onStudentSelected(index) }
                        .background(if (index == selectedIndex) colorSelected else colorUnselected)
                        .padding(horizontal = 5.dp)
                ) {
                    StudentText(
                        name = studentsState[index],
                        Modifier.weight(0.8f)
                    )
                    IconButton(
                        modifier = Modifier.weight(0.2f),
                        onClick = { onIconDeleteStudentClick(index) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar student")
                    }
                }
            }
        }
        Button(
            onClick = onButtonClearStudentsClick
        ) {
            Text("Borrar todo")
        }
    }
}

@Composable
fun StudentText(name: String, modifier: Modifier) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        modifier = modifier
    )
}

@Composable
fun ImageUpDownScroll(
    showImgScrollStudentList: Boolean,
) {
    if (showImgScrollStudentList) {
        ImageWithTooltip(
            tooltipText = "Use scroll down-up",
            imagePath = "up_down_arrows.png",
            contentDesc = "Use scroll down-up",
            modifierImg = Modifier
                .padding(start = 5.dp, bottom = 50.dp)
                .width(20.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .padding(start = 5.dp, bottom = 50.dp)
                .size(20.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageWithTooltip(tooltipText: String, imagePath: String, contentDesc: String, modifierImg: Modifier) {
    TooltipArea(
        tooltip = {
            Box(
                modifier = Modifier
                    .background(colorTooltipBackground)
                    .border(1.dp, colorBorder)
            ) {
                Text(
                    text = tooltipText,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    ) {
        Image(
            painter = painterResource(imagePath),
            contentDescription = contentDesc,
            modifier = modifierImg
        )
    }
}

@Composable
fun SaveChangesButton(
    modifier: Modifier,
    onButtonSaveChangesClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onButtonSaveChangesClick
        ) {
            Text(text = "Guardar cambios")
        }
    }
}

@Composable
fun InfoMessage(message: String, onCloseInfoMessage: () -> Unit) {
    DialogWindow(
        icon = painterResource("info_icon.png"),
        title = "Atención",
        resizable = false,
        onCloseRequest = onCloseInfoMessage
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(message)
        }
    }
}

fun selectRowScrolling(
    event: KeyEvent,
    selectedIndex: Int,
    maxRow: Int,
    onStudentSelected: (Int) -> Unit
) : Boolean {
    return if (event.type == KeyEventType.KeyDown) {
        when (event.key) {
            Key.DirectionUp -> {
                if (selectedIndex > 0) {
                    onStudentSelected(selectedIndex - 1)
                    true
                } else false
            }

            Key.DirectionDown -> {
                if (selectedIndex < maxRow) {
                    onStudentSelected(selectedIndex + 1)
                    true
                } else false
            }

            else -> false
        }
    } else {
        false
    }
}

fun controlKeyEnter(
    event: KeyEvent,
    onButtonAddNewStudentClick: () -> Unit
) : Boolean {
    return if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
        onButtonAddNewStudentClick()
        true
    } else {
        false
    }
}