package com.example.tipcalculator

import android.os.Bundle
import android.util.Log
import android.util.MutableDouble
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import com.example.tipcalculator.util.calculateTotalPerPerson
import com.example.tipcalculator.util.calculateTotalTip
import com.example.tipcalculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCalculatorTheme {
        Scaffold(modifier = Modifier.fillMaxSize().safeDrawingPadding()) { innerPadding ->
            content()
        }
    }
}

@Preview
@Composable
fun TopHeader(amount: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)) {
        Column(modifier = Modifier
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            val total = "%.2f".format(amount)
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.headlineMedium)
            Text(text = "$$total",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = Bold)
        }
    }
}

@Preview
@Composable
fun MainContent() {

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    val splitValueState = remember {
        mutableIntStateOf(1)
    }
    BillForm(
        splitValueState = splitValueState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) { billAmt ->
        Log.d("JordanLog", billAmt)

    }
}

@Composable
fun BillForm(modifier: Modifier = Modifier,
             splitRange: IntRange = 1..50,
             splitValueState: MutableIntState,
             tipAmountState: MutableDoubleState,
             totalPerPersonState: MutableDoubleState,
             onValChange: (String) -> Unit = {}) {

    val totalBillState = remember {
        mutableStateOf("0.0")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val tipValueState = remember {
        mutableFloatStateOf(.3f)
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(1.dp, Color.LightGray)) {
        Column(modifier = modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            TopHeader(totalPerPersonState.doubleValue)
            InputField(
                modifier = modifier.padding(top = 8.dp),
                valueState = totalBillState,
                labeledId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) {
                        return@KeyboardActions
                    }
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )

            if(validState) {
                Row(modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text("Split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if(splitValueState.intValue > splitRange.start) {
                                    splitValueState.intValue--
                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(totalBillState.value.toDouble(), splitValueState.intValue, (tipValueState.floatValue * 100).toInt())
                                }
                            }
                        )
                        Text(text = splitValueState.intValue.toString(),
                            modifier = Modifier.align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitValueState.intValue < splitRange.endInclusive) {
                                    splitValueState.intValue++
                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(totalBillState.value.toDouble(), splitValueState.intValue, (tipValueState.floatValue * 100).toInt())
                                }
                            }
                        )
                    }
                }
            }
            Row(modifier = modifier
                .padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(text = "Tip",
                    modifier = modifier.align(alignment = Alignment.CenterVertically))
                Spacer(modifier = modifier.width(200.dp))
                Text(text = "$${"%.2f".format(tipAmountState.doubleValue)}",
                    modifier = modifier.align(alignment = Alignment.CenterVertically))
            }

            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${"%.0f".format(tipValueState.floatValue * 100)}%")
                Spacer(modifier = modifier.height(14.dp))
                Slider(modifier = modifier.padding(start = 16.dp, end = 16.dp),
                    value = tipValueState.floatValue,
                    onValueChange = { newValue ->
                        tipValueState.floatValue = (newValue * 100).toInt() / 100f
                        tipAmountState.doubleValue = calculateTotalTip(totalBillState.value.toDouble(), (tipValueState.floatValue * 100).toInt())
                        totalPerPersonState.doubleValue = calculateTotalPerPerson(totalBillState.value.toDouble(), splitValueState.intValue, (tipValueState.floatValue * 100).toInt())
                    }, steps = 19,
                    valueRange = 0f..0.5f)
            }
        }
    }
}
