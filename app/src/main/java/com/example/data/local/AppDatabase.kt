package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val varietyLabel: String,
    val categoryName: String,
    val unitPriceLkr: Long,
    val quantity: Int,
    val addedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_products")
data class SavedProductEntity(
    @PrimaryKey val productId: String,
    val isFavorite: Boolean,
    val isBookmarked: Boolean
)

@Entity(tableName = "personal_notes")
data class PersonalNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val tag: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_receipts")
data class OrderReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderCode: String,
    val customerName: String,
    val whatsappNumber: String,
    val bookingDate: String,
    val totalLkr: Long,
    val summaryText: String,
    val whatsappStatus: String,
    val recaptchaToken: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val category: String,
    val isRead: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_bookings")
data class CalendarBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateIso: String,
    val title: String,
    val ukLocation: String
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAtMillis DESC")
    fun observeCart(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :id LIMIT 1")
    suspend fun getById(id: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface SavedProductDao {
    @Query("SELECT * FROM saved_products")
    fun observeAll(): Flow<List<SavedProductEntity>>

    @Query("SELECT * FROM saved_products WHERE productId = :id LIMIT 1")
    suspend fun getById(id: String): SavedProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedProductEntity)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM personal_notes ORDER BY createdAtMillis DESC")
    fun observeNotes(): Flow<List<PersonalNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: PersonalNoteEntity)

    @Query("DELETE FROM personal_notes WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_receipts ORDER BY createdAtMillis DESC")
    fun observeOrders(): Flow<List<OrderReceiptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderReceiptEntity)

    @Query("UPDATE order_receipts SET whatsappStatus = :status WHERE orderCode = :orderCode")
    suspend fun updateWhatsAppStatus(orderCode: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY createdAtMillis DESC")
    fun observeNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: AppNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllRead()
}

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_bookings ORDER BY dateIso ASC")
    fun observeBookings(): Flow<List<CalendarBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: CalendarBookingEntity)

    @Query("DELETE FROM calendar_bookings WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Database(
    entities = [
        CartItemEntity::class,
        SavedProductEntity::class,
        PersonalNoteEntity::class,
        OrderReceiptEntity::class,
        AppNotificationEntity::class,
        CalendarBookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RonalDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun savedProductDao(): SavedProductDao
    abstract fun noteDao(): NoteDao
    abstract fun orderDao(): OrderDao
    abstract fun notificationDao(): NotificationDao
    abstract fun calendarDao(): CalendarDao

    companion object {
        @Volatile
        private var INSTANCE: RonalDatabase? = null

        fun getInstance(context: Context): RonalDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RonalDatabase::class.java,
                    "ronal_imports_uk.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
