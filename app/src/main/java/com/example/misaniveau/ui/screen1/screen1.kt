package com.example.misaniveau.ui.screen1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misaniveau.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen1(
    title: String,
    goToHome:() ->Unit,
    goToScan:() ->Unit,
    modifier: Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val first_text = stringResource(R.string.screen1_text)
    val snackbar_text = stringResource(R.string.screen1_snackbar_text)
    val modified_text = stringResource(R.string.screen1_modified_text)
    var showDialog by remember { mutableStateOf(false) }
    /* Texte affiché au centre*/
    var displayedText by remember {
        mutableStateOf(first_text)
    }
    var isModified by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.screen1_TopAppBar_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B263B),
                    titleContentColor = Color.White,
                ),
                navigationIcon = {
                    IconButton(onClick = { goToHome() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.ArrowBack)
                        )
                    }
                })

        }
    ) { paddingValues ->
        if(showDialog){
            AlertDialog(
                onDismissRequest = { /* Action */ },
                title = { Text(text = stringResource(R.string.screen1_dialog_title), color = Color.White) },
                text = { Text(text = stringResource(R.string.screen1_dialog_text), color = Color.White) },
                    containerColor = Color(0xFF1B263B),
                confirmButton = {
                    Button(
                        onClick = {
                            if(isModified){
                                displayedText = first_text
                                isModified = false
                                showDialog = false
                            } else {
                                displayedText = modified_text
                                isModified = true
                                showDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCCEAFF),
                            contentColor = Color(0xFF1B263B)
                        )
                    ) {
                        Text(text = stringResource(R.string.screen1_ConfirmButton))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = snackbar_text,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCCEAFF),
                            contentColor = Color(0xFF1B263B)
                        )
                    ) {
                        Text(text = stringResource(R.string.screen1_DismissButton))
                    }
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            /*Garder de l'espace en haut*/
            Image(
                painter = painterResource(R.drawable.messi_por_favor),
                contentDescription = stringResource(R.string.screen1_image_description),
                modifier = Modifier.size(350.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            // text affiché en cliquant sur le bouton 2
            Text(
                text = displayedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //bouton1 pour retourner au scan
                Button(onClick = {
                    goToScan()
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.screen1_button_1))
                }
                Spacer(modifier = Modifier.width(30.dp))
                // bouton2 change le...euh je veux dire la surprise
                Button(onClick = {
                    showDialog = true
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43A047),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.screen1_button_2))
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}