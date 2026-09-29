package com.example.myapplication.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

@Composable
fun LoginScreen(onNavigateToCharacters: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.rick_morty_logo),
            contentDescription = "Logo de Rick y Morty",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )

        Button(
            onClick = onNavigateToCharacters,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Empezar")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Mauricio Corado - 25218",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}