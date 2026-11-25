package com.example.core

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.data.local.CartItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object WhatsAppService {
    private const val TAG = "WhatsAppService"
    const val DEFAULT_WHATSAPP_LOCAL = "0719362947"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    fun formatInternationalPhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.startsWith("0") && digits.length == 10 -> "94" + digits.substring(1)
            digits.startsWith("94") -> digits
            digits.startsWith("44") -> digits
            digits.isNotBlank() -> digits
            else -> "94719362947"
        }
    }

    fun formatPriceLkr(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.UK)
        return "Rs. ${formatter.format(amount)}"
    }

    fun buildBillMessage(
        orderCode: String,
        customerName: String,
        whatsappNumber: String,
        bookingDate: String,
        items: List<CartItemEntity>,
        totalAmount: Long,
        recaptchaToken: String,
        vipTier: String
    ): String {
        val dateText = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK).format(Date())
        val lines = StringBuilder()
        lines.append("*RONAL IMPORTS · TRJ UK CONCIERGE*\n")
        lines.append("Official Automated Bill & Receipt\n")
        lines.append("────────────────────────\n")
        lines.append("Bill ID: $orderCode\n")
        lines.append("Date (UK): $dateText\n")
        lines.append("Client: $customerName ($vipTier)\n")
        lines.append("Location: Mayfair, London, UK\n")
        lines.append("Schedule Date: $bookingDate\n")
        lines.append("WhatsApp Line: $whatsappNumber\n")
        lines.append("────────────────────────\n")
        items.forEach { item ->
            val subTotal = item.unitPriceLkr * item.quantity
            lines.append("• ${item.productName} (${item.varietyLabel}) x${item.quantity} = ${formatPriceLkr(subTotal)}\n")
        }
        lines.append("────────────────────────\n")
        lines.append("*TOTAL DUE: ${formatPriceLkr(totalAmount)}*\n")
        lines.append("Security: Verified ($recaptchaToken)\n")
        lines.append("Thank you for choosing Ronal Imports UK.")
        return lines.toString()
    }

    fun openWhatsAppWithBill(context: Context, rawPhone: String, message: String): Boolean {
        return try {
            val intlPhone = formatInternationalPhone(rawPhone)
            val encoded = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://wa.me/$intlPhone?text=$encoded")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Unable to launch WhatsApp intent", e)
            false
        }
    }

    suspend fun sendViaWhatsAppCloudApi(
        phoneNumberId: String,
        accessToken: String,
        recipientRaw: String,
        messageText: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (phoneNumberId.isBlank() || accessToken.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Cloud API credentials not configured"))
        }
        try {
            val recipient = formatInternationalPhone(recipientRaw)
            val payload = JSONObject().apply {
                put("messaging_product", "whatsapp")
                put("to", recipient)
                put("type", "text")
                put("text", JSONObject().apply { put("body", messageText) })
            }
            val request = Request.Builder()
                .url("https://graph.facebook.com/v20.0/${phoneNumberId.trim()}/messages")
                .addHeader("Authorization", "Bearer ${accessToken.trim()}")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    Result.success("WhatsApp API delivered to +$recipient")
                } else {
                    Result.failure(IllegalStateException("API ${response.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp Cloud API call failed", e)
            Result.failure(e)
        }
    }

    fun shareTextDocument(context: Context, title: String, content: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(
            Intent.createChooser(shareIntent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    suspend fun saveDocumentToDownloads(
        context: Context,
        fileName: String,
        content: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                    put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/RonalImports")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext Result.failure(IllegalStateException("Failed to create download entry"))
                resolver.openOutputStream(uri)?.use { stream ->
                    stream.write(content.toByteArray(Charsets.UTF_8))
                }
                Result.success("Saved to Downloads/RonalImports/$fileName")
            } else {
                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RonalImports")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                file.writeText(content, Charsets.UTF_8)
                Result.success("Saved to ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving download", e)
            Result.failure(e)
        }
    }
}
