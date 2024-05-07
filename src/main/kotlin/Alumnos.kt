import androidx.compose.desktop.ui.tooling.preview.Preview
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
import kotlinx.coroutines.delay
import java.awt.Toolkit
import java.io.File

@Composable
fun MainWindowStudents(
    title: String,
    icon: Painter,
    windowState: WindowState,
    resizable: Boolean,
    fileManagement: IFiles,
    studentsFile: File,
    onCloseMainWindow: () -> Unit,
) {
    Window(
        onCloseRequest = onCloseMainWindow,
        title = title,
        icon = icon,
        resizable = resizable,
        state = windowState
    ) {
        MaterialTheme {
            Surface(
                color = colorWindowBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                StudentScreen(fileManagement, studentsFile)
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
@Preview
fun StudentScreen(
    fileManagement: IFiles,
    studentsFile: File,
) {
    val maxCharacters = 10
    val maxNumStudentsVisible = 7

    val (newStudent, setNewStudent) = remember { mutableStateOf("") }
    val studentsState = remember { mutableStateListOf<String>() }

    val newStudentFocusRequester = remember { FocusRequester() }
    val studentListFocusRequester = remember { FocusRequester() }

    val (infoMessage, setInfoMessage) = remember { mutableStateOf("") }
    val (showInfoMessage, setShowInfoMessage) = remember { mutableStateOf(false) }

    val showImgScrollStudentList = remember { derivedStateOf { studentsState.size > maxNumStudentsVisible } }

    val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(-1) }

    LaunchedEffect(key1 = true) {
        val loadedStudents = fileManagement.leerFichero(studentsFile)
        if (loadedStudents != null) {
            studentsState.addAll(loadedStudents)
        } else {
            setInfoMessage("No se pudieron cargar los datos de los estudiantes.")
            setShowInfoMessage(true)
        }
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
                newStudent = newStudent,
                focusRequester = newStudentFocusRequester,
                onNewStudentChange = {
                    if (it.length <= maxCharacters) {
                        setNewStudent(it)
                    }
                },
                onButtonAddNewStudentClick = {
                    if (newStudent.isNotBlank()) {
                        studentsState.add(newStudent.trim())
                        setNewStudent("")
                    }
                    newStudentFocusRequester.requestFocus()
                }
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                StudentList(
                    studentsState = studentsState,
                    selectedIndex = selectedIndex,
                    focusRequester = studentListFocusRequester,
                    onStudentSelected = { index -> setSelectedIndex(index) },
                    onIconDeleteStudentClick = { studentsState.removeAt(it) }
                ) {
                    studentsState.clear()
                }
                ImageUpDownScroll(
                    showImgScrollStudentList = showImgScrollStudentList.value,
                )
            }
        }
        SaveChangesButton(
            modifier = Modifier.fillMaxSize().weight(1f),
            onButtonSaveChangesClick = {
                var error = ""
                val newStudentsFile = fileManagement.crearFichero(studentsFile.absolutePath)
                if (newStudentsFile != null) {
                    for (student in studentsState) {
                        error = fileManagement.escribirFichero(studentsFile, "$student\n")
                        if (error.isNotEmpty()) {
                            break
                        }
                    }
                    if (error.isNotEmpty()) {
                        setInfoMessage(error)
                    } else {
                        setInfoMessage("Fichero guardado correctamente")
                    }
                } else {
                    setInfoMessage("No se pudo generar el fichero studentList.txt")
                }
                setShowInfoMessage(true)
            }
        )
    }

    if (showInfoMessage) {
        InfoMessage(
            message = infoMessage,
            onDismiss = {
                setShowInfoMessage(false)
                setInfoMessage("")
                newStudentFocusRequester.requestFocus()
            }
        )
    }

    LaunchedEffect(studentsState.size) {
        newStudentFocusRequester.requestFocus()
    }

    LaunchedEffect(showInfoMessage) {
        if (showInfoMessage) {
            delay(2000)
            setShowInfoMessage(false)
            setInfoMessage("")
            newStudentFocusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
                if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                    onButtonAddNewStudentClick()
                    true // Consumimos el evento
                } else {
                    false // No consumimos el evento
                }
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
                Text(text = "New student name ")
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
        Text(text = "Add new student")
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
            text = "Students: ${studentsState.size}",
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
                    if (event.type == KeyEventType.KeyUp) {
                        when (event.key) {
                            Key.DirectionUp -> {
                                if (selectedIndex > 0) {
                                    onStudentSelected(selectedIndex - 1)
                                    true
                                } else false
                            }
                            Key.DirectionDown -> {
                                if (selectedIndex < studentsState.size - 1) {
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
                        Icon(Icons.Default.Delete, contentDescription = "Delete student")
                    }
                }
            }
        }

        Button(
            onClick = onButtonClearStudentsClick
        ) {
            Text("Clear all")
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
            Text(text = "Save changes")
        }
    }
}

@Composable
fun InfoMessage(message: String, onDismiss: () -> Unit) {
    DialogWindow(
        icon = painterResource("info_icon.png"),
        title = "Atención",
        resizable = false,
        onCloseRequest = onDismiss
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(message)
        }
    }
}