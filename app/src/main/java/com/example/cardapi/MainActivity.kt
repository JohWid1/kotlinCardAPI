package com.example.cardapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardapi.ui.theme.CardAPITheme
import io.ktor.client.HttpClient
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.call.*
import io.ktor.client.plugins.gson.GsonSerializer
import io.ktor.client.plugins.json.JsonPlugin
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController




class MainActivity : ComponentActivity() {
    private val viewModel: CardViewModel = CardViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardAPITheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    Navigation(navController, viewModel)
                }
            }
        }
    }
}


suspend fun fetchRandomCard(): Card? {
    val client = HttpClient {
        install(JsonPlugin) {
            serializer = GsonSerializer()
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }
    return withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("https://api.scryfall.com/cards/random")
            val card = response.body<Card>()
            Log.d("CardAPI", "Fetched card: $card")
            card
        } catch (e: Exception) {
            Log.e("CardAPI", "Error fetching card", e)
            null
        } finally {
            client.close()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDisplayScreen(viewModel: CardViewModel, navController: NavController) {

    val card by remember { derivedStateOf { viewModel.card } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Random MTG Card") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)

        ) {if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // Show loading spinner
        } else {
            card?.let {
                Image(
                    painter = rememberImagePainter(it.image_uris.normal),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit
                )
                Text(text = it.name, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it.type_line, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it.oracle_text, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

            Button(
                onClick = {
                    viewModel.fetchRandomCard { fetchRandomCard() }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
            ) {
                Text(stringResource(R.string.fetch_card))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("info") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Go to info")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Info") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(innerPadding)
        ){
            Text("This app fetches random cards from an API.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() }, // Navigate back
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController, viewModel: CardViewModel) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { CardDisplayScreen(viewModel, navController) }
        composable("info") { InfoScreen(navController) }
    }
}

