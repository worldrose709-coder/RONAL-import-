package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.WhatsAppService
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderReceiptEntity
import com.example.data.local.UserPreferencesState
import com.example.ui.components.Glass3DCard
import com.example.ui.components.PremiumShimmerButton
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CheckoutScreen(
    prefs: UserPreferencesState,
    cartItems: List<CartItemEntity>,
    orderReceipts: List<OrderReceiptEntity>,
    selectedBookingDate: String,
    isRecaptchaVerified: Boolean,
    recaptchaToken: String,
    onUpdateCartQty: (CartItemEntity, Int) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onSaveWhatsAppConfig: (String, Boolean, String, String) -> Unit,
    onVerifyRecaptcha: (Boolean) -> Unit,
    onConfirmOrder: (String, String, (String, String) -> Boolean) -> Unit,
    onBrowseCatalog: () -> Unit,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var clientName by remember(prefs.memberName) { mutableStateOf(prefs.memberName) }
    var whatsappPhone by remember(prefs.whatsappNumber) { mutableStateOf(prefs.whatsappNumber) }
    var showApiSettings by remember { mutableStateOf(false) }
    var autoApiEnabled by remember(prefs.whatsappAutoApiEnabled) { mutableStateOf(prefs.whatsappAutoApiEnabled) }
    var phoneNumberId by remember(prefs.whatsappPhoneNumberId) { mutableStateOf(prefs.whatsappPhoneNumberId) }
    var accessToken by remember(prefs.whatsappAccessToken) { mutableStateOf(prefs.whatsappAccessToken) }

    // Interactive reCAPTCHA puzzle slider state
    var showChallenge by remember { mutableStateOf(false) }
    var targetCode by remember { mutableStateOf(72) }
    var sliderVal by remember { mutableFloatStateOf(15f) }

    val totalAmount = remember(cartItems) {
        cartItems.sumOf { it.unitPriceLkr * it.quantity }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bag Header & Cart Items
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Luxury Bag", tint = extra.accent)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "MOBILE EXPRESS CHECKOUT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extra.accent
                                )
                                Text(
                                    text = "Your Ronal Luxury Bag (${cartItems.sumOf { it.quantity }})",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = InkObsidian
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = extra.glow.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "UK · $selectedBookingDate",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkObsidian,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (cartItems.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your luxury bag is currently empty.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(onClick = onBrowseCatalog, shape = RoundedCornerShape(14.dp)) {
                                Text("Browse Cars, Villas, Tea & Perfume")
                            }
                        }
                    } else {
                        cartItems.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF9FAFB),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.productName} · ${item.varietyLabel}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = InkObsidian
                                        )
                                        Text(
                                            text = "${WhatsAppService.formatPriceLkr(item.unitPriceLkr)} each",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extra.accent
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { onUpdateCartQty(item, -1) }) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                        }
                                        Text(
                                            text = "${item.quantity}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = InkObsidian
                                        )
                                        IconButton(onClick = { onUpdateCartQty(item, 1) }) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase")
                                        }
                                        IconButton(onClick = { onRemoveFromCart(item.productId) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFF9CA3AF))
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL BILL",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = WhatsAppService.formatPriceLkr(totalAmount),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkObsidian
                            )
                        }
                    }
                }
            }
        }

        // WhatsApp Receipt & API Connection Card (Default 0719362947)
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WHATSAPP BILL AUTOMATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = WhatsAppGreen
                            )
                            Text(
                                text = "Connected Line: ${whatsappPhone.ifBlank { WhatsAppService.DEFAULT_WHATSAPP_LOCAL }}",
                                style = MaterialTheme.typography.titleLarge,
                                color = InkObsidian
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = WhatsAppEmerald.copy(alpha = 0.16f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = WhatsAppGreen)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Name on Bill") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = whatsappPhone,
                        onValueChange = { whatsappPhone = it },
                        label = { Text("WhatsApp Number (Default: 0719362947)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("checkout_whatsapp_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showApiSettings = !showApiSettings }) {
                            Text(if (showApiSettings) "Hide WhatsApp Cloud API" else "Configure WhatsApp Cloud API")
                        }
                        TextButton(
                            onClick = {
                                onSaveWhatsAppConfig(whatsappPhone, autoApiEnabled, phoneNumberId, accessToken)
                            }
                        ) {
                            Text("Save Number")
                        }
                    }

                    AnimatedVisibility(visible = showApiSettings) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto-Dispatch via Meta Cloud API",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkObsidian
                                )
                                Switch(
                                    checked = autoApiEnabled,
                                    onCheckedChange = { autoApiEnabled = it }
                                )
                            }
                            OutlinedTextField(
                                value = phoneNumberId,
                                onValueChange = { phoneNumberId = it },
                                label = { Text("WhatsApp Phone Number ID (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = accessToken,
                                onValueChange = { accessToken = it },
                                label = { Text("Meta Graph Bearer Token (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onSaveWhatsAppConfig(whatsappPhone, autoApiEnabled, phoneNumberId, accessToken)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save WhatsApp API Connection")
                            }
                        }
                    }
                }
            }
        }

        // Interactive reCAPTCHA "I'm not a robot" Security Card
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isRecaptchaVerified) Icons.Default.VerifiedUser else Icons.Default.Security,
                                contentDescription = "Bot Protection",
                                tint = if (isRecaptchaVerified) WhatsAppGreen else extra.accent
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "reCAPTCHA BOT PROTECTION",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isRecaptchaVerified) WhatsAppGreen else extra.accent
                            )
                        }
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isRecaptchaVerified) Color(0xFFECFDF5) else Color(0xFFF9FAFB),
                        border = BorderStroke(
                            1.2.dp,
                            if (isRecaptchaVerified) WhatsAppEmerald else Color(0xFFD1D5DB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isRecaptchaVerified,
                                    onCheckedChange = { checked ->
                                        if (checked && !isRecaptchaVerified) {
                                            targetCode = (35..85).random()
                                            sliderVal = 15f
                                            showChallenge = true
                                        } else if (!checked) {
                                            onVerifyRecaptcha(false)
                                            showChallenge = false
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = WhatsAppGreen),
                                    modifier = Modifier.testTag("recaptcha_checkbox")
                                )
                                Text(
                                    text = if (isRecaptchaVerified) "Verified Human ($recaptchaToken)" else "I'm not a robot",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = InkObsidian
                                )
                            }
                            if (isRecaptchaVerified) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Passed",
                                    tint = WhatsAppGreen
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = showChallenge && !isRecaptchaVerified) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Human Security Check: Slide to align the gold dial near $targetCode (Current: ${sliderVal.roundToInt()})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkObsidian
                            )
                            Slider(
                                value = sliderVal,
                                onValueChange = { sliderVal = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = extra.accent,
                                    activeTrackColor = extra.accent
                                ),
                                modifier = Modifier.testTag("recaptcha_slider")
                            )
                            Button(
                                onClick = {
                                    if (abs(sliderVal.roundToInt() - targetCode) <= 6) {
                                        onVerifyRecaptcha(true)
                                        showChallenge = false
                                    } else {
                                        onStatusMessage("Align within ±6 of $targetCode to verify")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = InkObsidian),
                                modifier = Modifier.fillMaxWidth().testTag("recaptcha_verify_button")
                            ) {
                                Text("Verify I'm Not a Robot")
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    PremiumShimmerButton(
                        text = "Confirm & Send Bill to WhatsApp",
                        enabled = cartItems.isNotEmpty() && isRecaptchaVerified,
                        onClick = {
                            onConfirmOrder(clientName, whatsappPhone) { phone, msg ->
                                WhatsAppService.openWhatsAppWithBill(context, phone, msg)
                            }
                        },
                        testTag = "confirm_whatsapp_order_button",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
        }

        // Automated Bill & Receipt History
        if (orderReceipts.isNotEmpty()) {
            item {
                Text(
                    text = "AUTOMATED BILL & RECEIPT ARCHIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = extra.accent
                )
            }
            items(orderReceipts, key = { it.id }) { receipt ->
                Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${receipt.orderCode} · ${WhatsAppService.formatPriceLkr(receipt.totalLkr)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkObsidian
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = WhatsAppEmerald.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "WhatsApp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WhatsAppGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = receipt.whatsappStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    WhatsAppService.openWhatsAppWithBill(
                                        context,
                                        receipt.whatsappNumber,
                                        receipt.summaryText
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Send Again")
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val res = WhatsAppService.saveDocumentToDownloads(
                                            context,
                                            "${receipt.orderCode}_Bill.txt",
                                            receipt.summaryText
                                        )
                                        onStatusMessage(res.getOrElse { it.localizedMessage ?: "Save error" })
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Download")
                            }
                        }
                    }
                }
            }
        }
    }
}
