package com.example.onion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.example.onion.ui.theme.OnionTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }



    // Imagem do Depay
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.menphis),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopCenter)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Textos da equação e seu resultado
        Text(text = input, modifier = Modifier.padding(8.dp))
        Text(text = result, modifier = Modifier.padding(8.dp))

        // Botoes da calculadora
        val buttons = listOf(
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            ".", "0", "C", "+",
            "(", ")", "=", "<-"
        )

        //Lógica dos botoes de apagar, apagar tudo e igual
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttons.forEach { text ->
                item {
                    CalculatorButton(text) {
                        when (text) {
                            "C" -> {
                                input = ""
                                result = ""
                            }
                            "<-" -> {
                                if (input.isNotEmpty()) {
                                    input = input.dropLast(1)  // Remove o último numero digitado
                                }
                            }
                            "=" -> {
                                result = try {
                                    eval(input).toString()
                                } catch (e: Exception) {
                                    "Error"
                                }
                            }
                            else -> input += text
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier
        .size(80.dp),
    shape = RoundedCornerShape(0.dp),
    colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFA500), // Cor do botão
            contentColor = Color.Black // Cor do texto
        )
    ) {
        Text(text = text)
    }
}

fun eval(expression: String): Double {
    val values = mutableListOf<Double>()
    val ops = mutableListOf<Char>()

    var i = 0
    while (i < expression.length) {
        // Ignorar espaços
        if (expression[i] == ' ') {
            i++
            continue
        }

        // Lidar com números
        if (expression[i].isDigit()) {
            var num = 0.0
            while (i < expression.length && expression[i].isDigit()) {
                num = num * 10 + (expression[i] - '0')
                i++
            }
            // Lidar com decimais
            if (i < expression.length && expression[i] == '.') {
                i++
                var decimalPlace = 1
                while (i < expression.length && expression[i].isDigit()) {
                    num += (expression[i] - '0') * 10.0.pow(-decimalPlace.toDouble())
                    decimalPlace++
                    i++
                }
            }
            values.add(num)
            i--
        }
        // Lidar com parênteses
        else if (expression[i] == '(') {
            ops.add(expression[i])
        } else if (expression[i] == ')') {
            while (ops.isNotEmpty() && ops.last() != '(') {
                values.add(applyOp(ops.removeAt(ops.size - 1), values.removeAt(values.size - 1), values.removeAt(values.size - 1)))
            }
            ops.removeAt(ops.size - 1)
        }
        // Lidar com operadores
        else if (expression[i] in listOf('+', '-', '*', '/')) {
            while (ops.isNotEmpty() && hasPrecedence(expression[i], ops.last())) {
                values.add(applyOp(ops.removeAt(ops.size - 1), values.removeAt(values.size - 1), values.removeAt(values.size - 1)))
            }
            ops.add(expression[i])
        }
        i++
    }

    while (ops.isNotEmpty()) {
        values.add(applyOp(ops.removeAt(ops.size - 1), values.removeAt(values.size - 1), values.removeAt(values.size - 1)))
    }

    return values.last()
}


fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if (op2 == '(' || op2 == ')') return false
    if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
    return true
}

fun applyOp(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> if (b != 0.0) a / b else throw ArithmeticException("Cannot divide by zero")
        else -> 0.0
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    OnionTheme {
        Calculator()
    }
}
