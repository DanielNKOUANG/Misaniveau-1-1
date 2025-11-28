package com.example.misaniveau.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.misaniveau.R

@Composable
fun ElementList(
    title: String = "Device",
    content: String = "Mon contenu",
    image: Int? = R.drawable.download,
    onClick: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp)){
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)){
            if(image != null){
                Image(modifier = Modifier.height(56.dp).width(56.dp),
                    painter = painterResource(image),
                    contentDescription = "Icône du BLE"
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(modifier= Modifier.fillMaxWidth())
            {
                Text(text=title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(text= content,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light, fontSize=10.sp)
                )
            }
        }
    }

}