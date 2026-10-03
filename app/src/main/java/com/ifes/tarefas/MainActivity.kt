
package com.ifes.tarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifes.tarefas.ui.theme.TarefasTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TarefasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppTarefas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppTarefas(modifier: Modifier = Modifier) {
    //  CONTROLE DE TELAS
    var telaAtual by remember { mutableIntStateOf(1) }

    //  CONTROLE DE DADOS DO FORMULÁRIO (Tela 2)
    var campoTitulo by remember { mutableStateOf("") }
    var campoTopicos by remember { mutableStateOf("") }
    var marcarComoUrgente by remember { mutableStateOf(false) }

    // CONTADORES E ARMAZENAMENTO MÓVEL
    var contadorTarefas by remember { mutableIntStateOf(1) }
    // Lista reativa que guardará todos os blocos de tarefas criados
    val listaDeTarefasSalvas = remember { mutableStateListOf<Tarefa>() }

    // Lista que guarda os IDs das tarefas selecionadas (ficarão cinzas) para exclusão em lote
    val tarefasSelecionadasParaExcluir = remember { mutableStateListOf<Int>() }

    // Variável para mensagens de feedback visual
    var feedbackMensagem by remember { mutableStateOf("") }

    var modoEdicao by remember { mutableStateOf(false) }

    //  CONTROLE DE EDIÇÃO INTERNA (Tela 3)
    var editandoTarefaPopup by remember { mutableStateOf(false) }
    var campoEditTitulo by remember { mutableStateOf("") }
    var campoEditTopicos by remember { mutableStateOf("") }


    //  TIMER PARA FAZER OS ALERTS (FEEDBACKS) SUMIREM SOZINHOS
    LaunchedEffect(feedbackMensagem) {
        if (feedbackMensagem.isNotBlank()) {
            delay(3000) // Espera 3 segundos (3000 milissegundos)
            feedbackMensagem = "" // Limpa a mensagem e esconde o card
        }
    }





    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // fundo cinza
    ) {
//tela1
        if (telaAtual == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "A fazer:",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4D3E),
                        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                    )

                    if (listaDeTarefasSalvas.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Nenhum bloco de notas criado.",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6C757D)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toque no botão + abaixo para criar sua primeira tarefa!",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6C757D),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        listaDeTarefasSalvas.forEach { tarefa ->
                            val estaSelecionada = tarefasSelecionadasParaExcluir.contains(tarefa.id)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        if (modoEdicao) {
                                            if (estaSelecionada) {
                                                tarefasSelecionadasParaExcluir.remove(tarefa.id)
                                            } else {
                                                tarefasSelecionadasParaExcluir.add(tarefa.id)
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (modoEdicao && estaSelecionada) {
                                        Color(0xFFE2E8F0)
                                    } else if (tarefa.ehUrgente) {
                                        Color(0xFFFFEBEE)
                                    } else {
                                        Color.White
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (modoEdicao) {
                                                Text(
                                                    text = "X ",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC62828),
                                                    modifier = Modifier.clickable {
                                                        if (estaSelecionada) {
                                                            tarefasSelecionadasParaExcluir.remove(tarefa.id)
                                                        } else {
                                                            tarefasSelecionadasParaExcluir.add(tarefa.id)
                                                        }
                                                    }
                                                )
                                            }
                                            Text(
                                                text = tarefa.titulo,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (modoEdicao && estaSelecionada) Color(0xFF6C757D) else Color(0xFF1B4D3E)
                                            )
                                        }

                                        if (tarefa.ehUrgente) {
                                            Text(
                                                text = "URGENTE",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC62828)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${tarefa.topicos.size} tópicos listados.",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6C757D)
                                    )
                                }
                            }
                        }
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!modoEdicao) {
                        Button(
                            onClick = {
                                if (listaDeTarefasSalvas.isNotEmpty()) {
                                    modoEdicao = true
                                    feedbackMensagem = "Selecione as tarefas clicando no X."
                                } else {
                                    feedbackMensagem = "Não há tarefas para editar."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Text(text = "Editar", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                feedbackMensagem = ""
                                telaAtual = 2
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Text(text = "+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                tarefasSelecionadasParaExcluir.clear()
                                modoEdicao = false
                                feedbackMensagem = "Edição cancelada."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C757D))
                        ) {
                            Text(text = "Cancelar", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                if (tarefasSelecionadasParaExcluir.isNotEmpty()) {
                                    listaDeTarefasSalvas.removeAll { tarefa ->
                                        tarefasSelecionadasParaExcluir.contains(tarefa.id)
                                    }
                                    feedbackMensagem = "Tarefas selecionadas foram apagadas."
                                } else {
                                    feedbackMensagem = ""
                                }

                                tarefasSelecionadasParaExcluir.clear()
                                modoEdicao = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                        ) {
                            Text(text = "Pronto", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }



//tela2
        if (telaAtual == 2) {
            AlertDialog(
                onDismissRequest = { telaAtual = 1 },
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Nova Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4D3E)
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Preencha os campos obrigatórios abaixo.",
                            fontSize = 14.sp,
                            color = Color(0xFF6C757D),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = campoTitulo,
                            onValueChange = { campoTitulo = it },
                            label = { Text("Título da Tarefa") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1B4D3E))
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        OutlinedTextField(
                            value = campoTopicos,
                            onValueChange = { campoTopicos = it },
                            label = { Text("Itens (Separe por quebra de linha)") },
                            placeholder = { Text("Ex:\nItem 1\nItem 2") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1B4D3E))
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = marcarComoUrgente,
                                onCheckedChange = { marcarComoUrgente = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1B4D3E))
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Marcar como Urgente", fontSize = 15.sp, color = Color(0xFF6C757D))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val tituloLimpo = campoTitulo.trim()
                            val itensLimpos = campoTopicos.trim()

                            // Validação estrita exigida: Ambos são obrigatórios
                            if (tituloLimpo.isBlank() || itensLimpos.isBlank()) {
                                feedbackMensagem = "Erro: Título e itens são obrigatórios!"
                            } else {
                                // Cria o objeto dinâmico com ID único
                                val novaTarefa = Tarefa(
                                    id = contadorTarefas,
                                    titulo = tituloLimpo,
                                    topicos = itensLimpos.split("\n").filter { it.isNotBlank() },
                                    ehUrgente = marcarComoUrgente
                                )

                                listaDeTarefasSalvas.add(novaTarefa)
                                feedbackMensagem = "Sucesso: '$tituloLimpo' criado!"
                                contadorTarefas++

                                // Limpa o formulário de UX
                                campoTitulo = ""
                                campoTopicos = ""
                                marcarComoUrgente = false
                                telaAtual = 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4D3E))
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            telaAtual = 1
                            feedbackMensagem = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C757D))
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (feedbackMensagem.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 80.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0))
            ) {
                Text(
                    text = feedbackMensagem,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class Tarefa(
    val id: Int,
    val titulo: String,
    val topicos: List<String>,
    val ehUrgente: Boolean
)
