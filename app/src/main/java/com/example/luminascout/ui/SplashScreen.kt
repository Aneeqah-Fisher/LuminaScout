package com.example.luminascout.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminascout.R

@Composable
fun SplashScreen() {
    val navy = Color(0xFF0B1020)
    Box(
        modifier = Modifier.fillMaxSize().background(navy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.luminascout_splash_logo),
                contentDescription = "LuminaScout logo",
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "LuminaScout",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "S C O U T  •  P L A N  •  S H O O T",
                color = Color(0xFF9CA3B5),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
