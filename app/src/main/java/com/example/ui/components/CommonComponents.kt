package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyAmberContainer
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyGreenContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue

@Composable
fun VerifiedBadge(
    licenseNumber: String,
    modifier: Modifier = Modifier,
    label: String = "Verified"
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = VerifiedBadgeBg,
        contentColor = VerifiedBlue
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Professional Badge",
                modifier = Modifier.size(14.dp),
                tint = VerifiedBlue
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label • $licenseNumber",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = VerifiedBlue
            )
        }
    }
}

@Composable
fun FacilityVerifiedBadge(
    modifier: Modifier = Modifier,
    label: String = "Certified Facility"
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = EmergencyGreenContainer,
        contentColor = EmergencyGreen
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Certified Facility",
                modifier = Modifier.size(13.dp),
                tint = EmergencyGreen
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = EmergencyGreen
            )
        }
    }
}

@Composable
fun EmergencyStatusPill(
    status: String,
    erWaitMinutes: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "CRITICAL" -> Triple(EmergencyRedContainer, EmergencyRed, "Critical Volume • ER Wait ~${erWaitMinutes}m")
        "BUSY" -> Triple(EmergencyAmberContainer, EmergencyAmber, "High Volume • ER Wait ~${erWaitMinutes}m")
        else -> Triple(EmergencyGreenContainer, EmergencyGreen, "Normal Capacity • ER Wait ~${erWaitMinutes}m")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(textColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = textColor
            )
        }
    }
}
