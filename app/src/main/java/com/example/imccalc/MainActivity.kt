package com.example.imccalc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imccalc.ui.theme.IMCCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IMCCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldComposable()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldComposable() {
    Scaffold(
        topBar = { AppBar()},
        content = { Body() }
    )
}

@Composable
fun Body() {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMan by remember { mutableStateOf(true) }
    var height by remember { mutableStateOf(175F) }
    var kilos by remember { mutableStateOf(70)}
    var selectedActivity by remember { mutableStateOf(0) }
    var listActivity = listOf<String>("Sédentaire", "Faible", "Actif", "Sportif", "Athlete")
    val modifier = Modifier
        .fillMaxWidth()
    val manager = LocalFocusManager.current
    var calories by remember { mutableStateOf(0) }

    fun coeff() : Double {
        when(selectedActivity) {
            0 -> return 1.2
            1 -> return  1.375
            2 -> return  1.55
            3 -> return  1.72
            else -> return  1.9
        }
    }

    fun calculate() : Int {
        var result = ""
        if (name != "") result += name
        var ageInt = age.toIntOrNull() ?: 20
        val caloriesBase = (10 * kilos) + (6.25 * height.toInt()) - (5 * ageInt)
        val caloriesGender = if (isMan) caloriesBase + 5 else caloriesBase - 161
        val caloriesActivity = caloriesGender * coeff()
        return  caloriesActivity.toInt()
    }

    Column(modifier =
    Modifier
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TF(name = name, onChange = { name = it }, modifier = modifier, manager = manager, action = androidx.compose.ui.text.input.ImeAction.Next, info = "Nom")
        TF(name = age, onChange = { age = it }, modifier = modifier, manager = manager, type = KeyboardType.Number, info = "Age")
        Gender(modifier = modifier, isMan = isMan, onCheck = { isMan = it})
        Kilos(modifier = modifier, kilos = kilos, onChange = { kilos = it})
        Text("Taille: ${height.toInt()}cm")
        Height(modifier = modifier, height = height, onChange = {height = it})
        Radios(modifier = modifier, selected = selectedActivity, list = listActivity, onChange = {selectedActivity = it})
        Button(onClick = { calories = calculate() }) {
            Text("Calculer")
        }
        Text(text = "$name, vous avez besoin de $calories journalières")
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TF(
    name: String,
    onChange: (String) -> Unit,
    modifier: Modifier,
    manager: FocusManager,
    type: KeyboardType = KeyboardType.Text,
    action: ImeAction = androidx.compose.ui.text.input.ImeAction.Done,
    info: String
) {
    OutlinedTextField(
        value = name,
        onValueChange = onChange,
        modifier = modifier,
        label = { Text(text = info)},
        placeholder = { Text(text = "Entrez votre $info")},
        keyboardActions = KeyboardActions(
            onNext = {manager.moveFocus(FocusDirection.Down)},
            onDone = {manager.clearFocus()}
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = action,
            keyboardType = type
        )
    )
}

@Composable
fun Gender(modifier: Modifier, isMan: Boolean, onCheck: (Boolean) -> Unit) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text =  if (isMan) "Homme" else "Femme")
        Switch(checked = isMan, onCheckedChange = onCheck)
    }
}

@Composable
fun Kilos(modifier: Modifier, kilos: Int, onChange: (Int) -> Unit) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Kilos: $kilos")
        Surface(modifier = Modifier
            .height(30.dp),
            border = BorderStroke(width = 1.dp, color = Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { onChange(kilos + 1) }) {
                    Text("+")
                }
                IconButton(onClick = { onChange(kilos - 1) }) {
                    Text("-")
                }
            }

        }
    }
}

@Composable
fun Height(modifier: Modifier, height: Float, onChange: (Float) -> Unit) {
    Slider(
        value = height,
        onValueChange = onChange,
        valueRange = 150F..210F
    )
}

@Composable
fun Radios(modifier: Modifier, selected: Int, list: List<String>, onChange: (Int) -> Unit) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        for (index in list.indices) {
            Column() {
                RadioButton(selected = selected == index, onClick = { onChange(index) })
                Text(text = list.get(index))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(title = { Text(text = "Mon calculateur IMC")})
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IMCCalcTheme {
        ScaffoldComposable()
    }
}