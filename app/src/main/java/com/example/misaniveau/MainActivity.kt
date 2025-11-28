package com.example.misaniveau

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.misaniveau.ui.theme.MisaniveauTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.misaniveau.ui.components.Home
import com.example.misaniveau.ui.screen1.Screen1
import com.example.misaniveau.ui.screen2.Scan



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisaniveauTheme {
                    val navController = rememberNavController()
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = stringResource(R.string.home_route)
                    ) {

                        // Une page avec un paramètre (ici un nom)
                        composable("Home"){ Home(stringResource(R.string.home_route),
                            //l'action permet d'entrer dans les differentes pages
                            goToScreen1 = { navController.navigate("screen1")},
                            goToScan = { navController.navigate("scan")},
                            Modifier
                        )
                        }
                        composable("screen1"){ Screen1(stringResource(R.string.screen1_route),
                            goToHome = { navController.navigate("Home")},
                            goToScan = { navController.navigate("scan")},
                            Modifier
                        )
                        }
                        composable("scan"){ Scan(stringResource(R.string.scan_route),
                            goToHome = { navController.navigate("Home")}
                        )
                        }
                    }
            }
        }
    }
}
