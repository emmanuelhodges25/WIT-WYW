package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.AdminInvitation
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import com.example.data.model.Ambassador
import com.example.data.model.AmbassadorPayout
import com.example.data.model.AmbassadorProfile
import com.example.data.model.AmbassadorReferral
import com.example.data.model.AuditLog
import com.example.data.model.BulkOrder
import com.example.data.model.BusinessTask
import com.example.data.model.ContactMessage
import com.example.data.model.CustomizationRequest
import com.example.data.model.InventoryMovement
import com.example.data.model.JournalEntry
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.SavedAddress
import com.example.data.model.SupplierPurchaseOrder
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class WriteYourWaveRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before performing this operation.")
    }

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun isCurrentUserAdmin(): Boolean {
        val email = auth.currentUser?.email ?: return false
        return email.equals("emmanuelhodges17@gmail.com", ignoreCase = true)
    }

    // --- PRODUCTS (Journals Only - $5.00 USD) ---
    fun observeProducts(): Flow<List<Product>> {
        return db.collection("products")
            .whereEqualTo("isPublished", true)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Product::class.java)
                    .filter { it.category.contains("Journal", ignoreCase = true) || it.id.startsWith("prod_journal") }
                    .map { it.copy(price = 5.0) }
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getProductById(productId: String): Product? = withContext(Dispatchers.IO) {
        val doc = db.collection("products").document(productId).get().await()
        doc.toObject(Product::class.java)
    }

    suspend fun saveProduct(product: Product): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (product.id.isNotBlank()) {
                db.collection("products").document(product.id)
            } else {
                db.collection("products").document()
            }
            val payload = hashMapOf<String, Any>(
                "title" to product.title,
                "slug" to product.slug,
                "category" to product.category,
                "description" to product.description,
                "price" to product.price,
                "currency" to product.currency,
                "sku" to product.sku,
                "isPublished" to product.isPublished,
                "inStock" to product.inStock,
                "stockQuantity" to product.stockQuantity,
                "features" to product.features,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (product.coverImageUrl != null) {
                payload["coverImageUrl"] = product.coverImageUrl
            }
            if (product.backCoverImageUrl != null) {
                payload["backCoverImageUrl"] = product.backCoverImageUrl
            }
            if (product.createdAt == null) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }

            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to save product", e)
            Result.failure(e)
        }
    }

    // --- ORDERS ---
    fun observeUserOrders(userId: String): Flow<List<Order>> {
        return db.collection("orders")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Order::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAllOrders(): Flow<List<Order>> {
        return db.collection("orders")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Order::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getOrderByReference(reference: String): Order? = withContext(Dispatchers.IO) {
        val snap = db.collection("orders")
            .whereEqualTo("orderReference", reference.trim().uppercase())
            .get()
            .await()
        snap.documents.firstOrNull()?.toObject(Order::class.java)
    }

    suspend fun createOrder(
        productId: String,
        productTitle: String,
        unitPrice: Double,
        quantity: Long,
        customerName: String,
        customerEmail: String,
        customerPhone: String?,
        deliveryAddress: String,
        customizationDetails: String?
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val orderRef = "WYW-2026-" + String.format("%06d", Random.nextInt(100000, 999999))
            val totalPrice = unitPrice * quantity

            val docRef = db.collection("orders").document()
            val payload = hashMapOf<String, Any>(
                "orderReference" to orderRef,
                "userId" to uid,
                "customerName" to customerName,
                "customerEmail" to customerEmail,
                "deliveryAddress" to deliveryAddress,
                "productId" to productId,
                "productTitle" to productTitle,
                "quantity" to quantity,
                "unitPrice" to unitPrice,
                "totalPrice" to totalPrice,
                "currency" to "USD",
                "status" to "PENDING",
                "paymentStatus" to "UNPAID",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!customerPhone.isNullOrBlank()) payload["customerPhone"] = customerPhone
            if (!customizationDetails.isNullOrBlank()) payload["customizationDetails"] = customizationDetails

            docRef.set(payload).await()
            val created = docRef.get().await().toObject(Order::class.java)
                ?: Order(
                    id = docRef.id,
                    orderReference = orderRef,
                    userId = uid,
                    customerName = customerName,
                    customerEmail = customerEmail,
                    deliveryAddress = deliveryAddress,
                    productId = productId,
                    productTitle = productTitle,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalPrice = totalPrice
                )
            Result.success(created)
        } catch (e: Exception) {
            Log.e("Repository", "Order creation error", e)
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        newPaymentStatus: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = hashMapOf<String, Any>(
                "status" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (newPaymentStatus != null) {
                updates["paymentStatus"] = newPaymentStatus
            }
            db.collection("orders").document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Order status update error", e)
            Result.failure(e)
        }
    }

    // --- CUSTOMIZATION REQUESTS ---
    fun observeUserCustomizations(userId: String): Flow<List<CustomizationRequest>> {
        return db.collection("customization_requests")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { it.toObjects(CustomizationRequest::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAllCustomizations(): Flow<List<CustomizationRequest>> {
        return db.collection("customization_requests")
            .snapshots()
            .map { it.toObjects(CustomizationRequest::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun submitCustomization(
        productId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String?,
        nameOnCover: String,
        preferredColors: String?,
        motivationalQuote: String?,
        customInstructions: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("customization_requests").document()
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "customerName" to customerName,
                "customerEmail" to customerEmail,
                "productId" to productId,
                "nameOnCover" to nameOnCover,
                "status" to "SUBMITTED",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!customerPhone.isNullOrBlank()) payload["customerPhone"] = customerPhone
            if (!preferredColors.isNullOrBlank()) payload["preferredColors"] = preferredColors
            if (!motivationalQuote.isNullOrBlank()) payload["motivationalQuote"] = motivationalQuote
            if (!customInstructions.isNullOrBlank()) payload["customInstructions"] = customInstructions

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Customization submission error", e)
            Result.failure(e)
        }
    }

    // --- BULK ORDERS ---
    fun observeUserBulkOrders(userId: String): Flow<List<BulkOrder>> {
        return db.collection("bulk_orders")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { it.toObjects(BulkOrder::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAllBulkOrders(): Flow<List<BulkOrder>> {
        return db.collection("bulk_orders")
            .snapshots()
            .map { it.toObjects(BulkOrder::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun submitBulkOrder(
        organizationName: String,
        contactPerson: String,
        email: String,
        phone: String,
        estimatedQuantity: Long,
        customizationRequirements: String?,
        deadlineDate: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("bulk_orders").document()
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "organizationName" to organizationName,
                "contactPerson" to contactPerson,
                "email" to email,
                "phone" to phone,
                "estimatedQuantity" to estimatedQuantity,
                "status" to "SUBMITTED",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!customizationRequirements.isNullOrBlank()) payload["customizationRequirements"] = customizationRequirements
            if (!deadlineDate.isNullOrBlank()) payload["deadlineDate"] = deadlineDate

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Bulk order submit error", e)
            Result.failure(e)
        }
    }

    // --- CONTACT MESSAGES ---
    fun observeUserContactMessages(userId: String): Flow<List<ContactMessage>> {
        return db.collection("contact_messages")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { it.toObjects(ContactMessage::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAllContactMessages(): Flow<List<ContactMessage>> {
        return db.collection("contact_messages")
            .snapshots()
            .map { it.toObjects(ContactMessage::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun submitContactMessage(
        name: String,
        email: String,
        subject: String,
        category: String,
        message: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: "guest_${System.currentTimeMillis()}"
            val docRef = db.collection("contact_messages").document()
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "name" to name,
                "email" to email,
                "subject" to subject,
                "category" to category,
                "message" to message,
                "status" to "NEW",
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Contact submit error", e)
            Result.failure(e)
        }
    }

    // --- INVENTORY MOVEMENTS ---
    fun observeMovements(): Flow<List<InventoryMovement>> {
        return db.collection("inventory_movements")
            .snapshots()
            .map { it.toObjects(InventoryMovement::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun recordInventoryMovement(
        productId: String,
        change: Long,
        movementType: String,
        reason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val docRef = db.collection("inventory_movements").document()
            val payload = hashMapOf<String, Any>(
                "productId" to productId,
                "quantityChange" to change,
                "movementType" to movementType,
                "reason" to reason,
                "performedBy" to uid,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()

            // Update product stockQuantity
            val productRef = db.collection("products").document(productId)
            db.runTransaction { tx ->
                val prodSnap = tx.get(productRef)
                val currentStock = prodSnap.getLong("stockQuantity") ?: 0L
                val newStock = (currentStock + change).coerceAtLeast(0L)
                tx.update(productRef, mapOf(
                    "stockQuantity" to newStock,
                    "inStock" to (newStock > 0),
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Inventory movement error", e)
            Result.failure(e)
        }
    }

    // --- AMBASSADORS ---
    fun observeAmbassadors(): Flow<List<Ambassador>> {
        return db.collection("ambassadors")
            .whereEqualTo("isPublished", true)
            .snapshots()
            .map { it.toObjects(Ambassador::class.java) }
            .flowOn(Dispatchers.IO)
    }

    // --- SEED INITIAL DATA (EMPTY STATE AVOIDANCE) ---
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("products").limit(1).get().await()
            val seedProducts = listOf(
                Product(
                    id = "prod_journal_hazel_quiqui",
                    title = "Write Your Wave Journal (Hazel Quiqui Edition)",
                    slug = "write-your-wave-journal-hazel-quiqui",
                    category = "Journals",
                    description = "Official Student Ambassador Edition featuring Hazel Quiqui. “Your Story Matters. Pray Truth and Wait. Big Dreams. Real Plans.” Features twin-loop gold wire binding, 5 Life Pillars roadmap, and daily reflection pages.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-00",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_hazel_quiqui,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 120L,
                    features = listOf("Official Hazel Quiqui Signature Cover", "5 Life Pillars: Study, Career, Freedom, Care, Success", "120 GSM Acid-Free Bleed-Proof Paper", "Unique Serial Verification Number", "Available at Starz & WIT Campus Hubs")
                ),
                Product(
                    id = "prod_journal_zinnah_kamara",
                    title = "Write Your Wave Journal (Zinnah M. Kamara Edition)",
                    slug = "write-your-wave-journal-zinnah-kamara",
                    category = "Journals",
                    description = "Creative & Leadership Ambassador Edition featuring Zinnah M. Kamara. “Better Days Ahead. Pray Truth and Wait.” High-end editorial design with dedicated career and financial planning sections.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-05",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_zinnah_kamara,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 110L,
                    features = listOf("Zinnah M. Kamara Leadership Edition", "Better Days Ahead Daily Vision Spreads", "120 GSM Acid-Free Heavyweight Paper", "Gold Crown Detailing & Twin-Loop Binding", "Manufactured at WIT Sinkor Production Hub")
                ),
                Product(
                    id = "prod_journal_purple_scholar",
                    title = "Write Your Wave Journal (Deep Purple Scholar Edition)",
                    slug = "write-your-wave-journal-purple-scholar",
                    category = "Journals",
                    description = "Scholars & Creators Deep Purple Edition. “Big Dreams. Real Plans. Plan, Study, Grow, Succeed.” Inspired by deep academic focus and mindful daily growth.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-06",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_purple_scholar,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 115L,
                    features = listOf("Deep Purple Scholar Aesthetic", "Plan, Study, Grow & Succeed Trackers", "120 GSM Bleed-Proof Paper for Highlighters & Inks", "Unique Serial Verification Number", "Campus Hub Pickup at Starz & WIT")
                ),
                Product(
                    id = "prod_journal_abiola_blue",
                    title = "Write Your Wave Journal (Signature Ocean Blue)",
                    slug = "write-your-wave-journal-signature-blue",
                    category = "Journals",
                    description = "Signature spiral wire-bound edition featuring student creator Abiola. Structured guided daily prompts for Study, Career, Financial Freedom, Self Care, and Success with gold foil crown lettering.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-01",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_abiola_blue,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 100L,
                    features = listOf("Spiral Double-Wire Bound Spine", "5 Life Pillars Alignment System", "120 GSM Acid-Free Bleed-Proof Paper", "Unique Serial Verification Number", "Manufactured at WIT Sinkor Production Hub")
                ),
                Product(
                    id = "prod_journal_purple_gold",
                    title = "Write Your Wave Journal (Royal Purple & Gold)",
                    slug = "write-your-wave-journal-purple-gold",
                    category = "Journals",
                    description = "A Journey to the Life I Want. Inspiring gold crown calligraphy with daily morning intentionality sections and evening gratitude reflections.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-02",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_purple_gold,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 100L,
                    features = listOf("Royal Purple & Gold Foil Stamped", "Study & Career Roadmap Prompts", "120 GSM Acid-Free Archival Paper", "Unique Serial Verification Number", "Manufactured at WIT Sinkor Production Hub")
                ),
                Product(
                    id = "prod_journal_noir_gold",
                    title = "Write Your Wave Journal (Noir Gold Class of 2026)",
                    slug = "write-your-wave-journal-noir-gold",
                    category = "Journals",
                    description = "Focus. Learn. Level Up. Big Dreams Require Discipline. Premium noir black and gold foil edition designed for academic sprinting and long-term milestones.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-03",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_noir_gold,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 100L,
                    features = listOf("Luxury Black Noir & Metallic Gold", "Plan, Study, Grow & Succeed Trackers", "120 GSM Bleed-Proof Heavyweight Paper", "Unique Serial Verification Number", "Manufactured at WIT Sinkor Production Hub")
                ),
                Product(
                    id = "prod_journal_radiant_pink",
                    title = "Write Your Wave Journal (Radiant Pink & Purple)",
                    slug = "write-your-wave-journal-radiant-pink",
                    category = "Journals",
                    description = "Dream. Plan. Achieve. Repeat. Vibrant brush-radiance cover with dedicated personalized name slot and creative vision board spreads.",
                    price = 5.0,
                    currency = "USD",
                    sku = "WYW-JOUR-04",
                    coverImageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_radiant_pink,
                    isPublished = true,
                    inStock = true,
                    stockQuantity = 100L,
                    features = listOf("Personalized Nameplate Slot", "Dream, Plan, Achieve Daily Spreads", "Durable Wire-O Binding", "Unique Serial Verification Number", "Manufactured at WIT Sinkor Production Hub")
                )
            )

            // Seed or update the 4 journal editions
            for (p in seedProducts) {
                val pDoc = db.collection("products").document(p.id)
                pDoc.set(hashMapOf(
                    "title" to p.title,
                    "slug" to p.slug,
                    "category" to p.category,
                    "description" to p.description,
                    "price" to p.price,
                    "currency" to p.currency,
                    "sku" to p.sku,
                    "coverImageUrl" to p.coverImageUrl,
                    "isPublished" to p.isPublished,
                    "inStock" to p.inStock,
                    "stockQuantity" to p.stockQuantity,
                    "features" to p.features,
                    "updatedAt" to FieldValue.serverTimestamp()
                ), com.google.firebase.firestore.SetOptions.merge()).await()
            }

            if (snapshot.isEmpty) {
                    for (p in seedProducts) {
                    val pDoc = db.collection("products").document(p.id)
                    pDoc.set(hashMapOf(
                        "title" to p.title,
                        "slug" to p.slug,
                        "category" to p.category,
                        "description" to p.description,
                        "price" to p.price,
                        "currency" to p.currency,
                        "sku" to p.sku,
                        "isPublished" to p.isPublished,
                        "inStock" to p.inStock,
                        "stockQuantity" to p.stockQuantity,
                        "features" to p.features,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )).await()
                }

                // Seed Ambassadors
                val seedAmbassadors = listOf(
                    Ambassador(
                        id = "amb_hazel_quiqui",
                        name = "Hazel Quiqui",
                        slug = "hazel-quiqui",
                        role = "Campus Ambassador & Student Leader (Starz University)",
                        biography = "Passionate advocate for student voice and intentional goal planning. Champions the Hazel Quiqui Signature Edition with the personal motto: 'Your Story Matters. Pray Truth and Wait.'",
                        featuredProduct = "Write Your Wave Journal (Hazel Quiqui Edition)",
                        imageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_hazel_quiqui,
                        isPublished = true
                    ),
                    Ambassador(
                        id = "amb_zinnah_kamara",
                        name = "Zinnah M. Kamara",
                        slug = "zinnah-kamara",
                        role = "Senior Ambassador & Creative Director (WIT Hub)",
                        biography = "Mentor in student entrepreneurship and academic excellence. Guides students across Monrovia to balance academic rigor and personal well-being: 'Better Days Ahead. Pray Truth and Wait.'",
                        featuredProduct = "Write Your Wave Journal (Zinnah M. Kamara Edition)",
                        imageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_zinnah_kamara,
                        isPublished = true
                    ),
                    Ambassador(
                        id = "amb_abiola",
                        name = "Abiola",
                        slug = "abiola",
                        role = "Founding Ambassador & Student Creator",
                        biography = "Pioneering student ambassador for the Write Your Wave movement. Creator of the 5 Life Pillars journal system: Study, Career, Financial Freedom, Self Care, and Success.",
                        featuredProduct = "Write Your Wave Journal (Signature Ocean Blue)",
                        imageUrl = "android.resource://com.example/" + com.example.R.drawable.journal_cover_abiola_blue,
                        isPublished = true
                    )
                )

                for (a in seedAmbassadors) {
                    val aDoc = db.collection("ambassadors").document(a.id)
                    aDoc.set(hashMapOf(
                        "name" to a.name,
                        "slug" to a.slug,
                        "role" to a.role,
                        "biography" to a.biography,
                        "featuredProduct" to a.featuredProduct,
                        "imageUrl" to a.imageUrl,
                        "isPublished" to a.isPublished,
                        "createdAt" to FieldValue.serverTimestamp()
                    ), com.google.firebase.firestore.SetOptions.merge()).await()
                }
            } else {
                // Ensure stationery products are added even if journals were already seeded
                val brassPenDoc = db.collection("products").document("prod_brass_pen").get().await()
                if (!brassPenDoc.exists()) {
                    for (p in seedProducts.drop(4)) {
                        val pDoc = db.collection("products").document(p.id)
                        pDoc.set(hashMapOf(
                            "title" to p.title,
                            "slug" to p.slug,
                            "category" to p.category,
                            "description" to p.description,
                            "price" to p.price,
                            "currency" to p.currency,
                            "sku" to p.sku,
                            "isPublished" to p.isPublished,
                            "inStock" to p.inStock,
                            "stockQuantity" to p.stockQuantity,
                            "features" to p.features,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )).await()
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("Repository", "Seeding skipped or failed (safe): ${e.message}")
        }
    }

    // --- USER JOURNAL ENTRIES ---
    fun observeUserJournalEntries(userId: String): Flow<List<JournalEntry>> {
        return db.collection("users")
            .document(userId)
            .collection("journal_entries")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(JournalEntry::class.java)
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun saveJournalEntry(entry: JournalEntry): Result<JournalEntry> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            val collectionRef = db.collection("users").document(userId).collection("journal_entries")
            val docRef = if (entry.id.isNotBlank()) {
                collectionRef.document(entry.id)
            } else {
                collectionRef.document()
            }

            val words = entry.content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
            val readMinutes = maxOf(1L, ((words + 199) / 200).toLong())

            val payload = hashMapOf<String, Any>(
                "userId" to userId,
                "title" to (entry.title.ifBlank { "Untitled Reflection" }).take(200),
                "content" to entry.content.take(50000),
                "pillar" to entry.pillar.ifBlank { "Study" }.take(100),
                "prompt" to entry.prompt.take(1000),
                "mood" to entry.mood.take(50),
                "wordCount" to words.toLong(),
                "readingTimeMinutes" to readMinutes,
                "isFavorite" to entry.isFavorite,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (entry.createdAt == null && entry.id.isBlank()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }

            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            val saved = entry.copy(
                id = docRef.id,
                userId = userId,
                title = (entry.title.ifBlank { "Untitled Reflection" }).take(200),
                wordCount = words.toLong(),
                readingTimeMinutes = readMinutes
            )
            Result.success(saved)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to save journal entry", e)
            Result.failure(e)
        }
    }

    suspend fun deleteJournalEntry(entryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            db.collection("users")
                .document(userId)
                .collection("journal_entries")
                .document(entryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to delete journal entry", e)
            Result.failure(e)
        }
    }

    suspend fun toggleFavoriteJournalEntry(entryId: String, currentFavorite: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            db.collection("users")
                .document(userId)
                .collection("journal_entries")
                .document(entryId)
                .update(
                    mapOf(
                        "isFavorite" to !currentFavorite,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- USER PROFILE & PREFERENCES ---
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        return db.collection("user_profiles")
            .document(userId)
            .snapshots()
            .map { snap ->
                if (snap.exists()) {
                    snap.toObject(UserProfile::class.java)
                } else {
                    null
                }
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            val docRef = db.collection("user_profiles").document(userId)
            val payload = hashMapOf<String, Any>(
                "displayName" to profile.displayName,
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "email" to profile.email,
                "phone" to profile.phone,
                "city" to profile.city,
                "country" to profile.country,
                "universityOrAffiliation" to profile.universityOrAffiliation,
                "bio" to profile.bio,
                "writingInterests" to profile.writingInterests,
                "emailNotifications" to profile.emailNotifications,
                "orderAlerts" to profile.orderAlerts,
                "seriesVerificationAlerts" to profile.seriesVerificationAlerts,
                "preferredLanguage" to profile.preferredLanguage,
                "preferredCurrency" to profile.preferredCurrency,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (profile.photoUrl != null) {
                payload["photoUrl"] = profile.photoUrl
            }
            if (profile.createdAt == null) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }
            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to save user profile", e)
            Result.failure(e)
        }
    }

    // --- SAVED ADDRESSES ---
    fun observeSavedAddresses(userId: String): Flow<List<SavedAddress>> {
        return db.collection("users")
            .document(userId)
            .collection("addresses")
            .snapshots()
            .map { it.toObjects(SavedAddress::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun saveSavedAddress(address: SavedAddress): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            val collection = db.collection("users").document(userId).collection("addresses")
            val docRef = if (address.id.isNotBlank()) collection.document(address.id) else collection.document()

            val payload = hashMapOf<String, Any>(
                "userId" to userId,
                "recipientName" to address.recipientName,
                "phone" to address.phone,
                "addressLine" to address.addressLine,
                "city" to address.city,
                "country" to address.country,
                "deliveryInstructions" to address.deliveryInstructions,
                "isDefault" to address.isDefault,
                "createdAt" to (address.createdAt ?: FieldValue.serverTimestamp())
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to save address", e)
            Result.failure(e)
        }
    }

    suspend fun deleteSavedAddress(addressId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            db.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to delete address", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // --- ENTERPRISE BUSINESS MANAGEMENT SYSTEM REPOSITORY EXTENSIONS ---
    // =========================================================================

    fun getCurrentAdminRole(): AdminRole {
        val email = auth.currentUser?.email ?: return AdminRole.READ_ONLY_ANALYST
        if (email.equals("emmanuelhodges17@gmail.com", ignoreCase = true)) {
            return AdminRole.SUPER_ADMIN
        }
        return AdminRole.OPERATIONS_ADMIN
    }

    // --- 1. AUDIT LOGS ---
    fun observeAuditLogs(): Flow<List<AuditLog>> {
        return db.collection("audit_logs")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { it.toObjects(AuditLog::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun recordAuditLog(
        action: String,
        targetType: String,
        targetId: String,
        details: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            val docRef = db.collection("audit_logs").document()
            val payload = hashMapOf<String, Any>(
                "actorId" to (user?.uid ?: "system"),
                "actorEmail" to (user?.email ?: "system@writeyourwave.com"),
                "actorRole" to getCurrentAdminRole().displayName,
                "action" to action,
                "targetType" to targetType,
                "targetId" to targetId,
                "details" to details,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to record audit log", e)
            Result.failure(e)
        }
    }

    // --- 2. ADMINISTRATOR TEAM INVITATIONS (FACEBOOK BUSINESS STYLE) ---
    fun observeAdminInvitations(): Flow<List<AdminInvitation>> {
        return db.collection("admin_invitations")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AdminInvitation::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun inviteAdministrator(
        email: String,
        role: AdminRole
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val senderEmail = auth.currentUser?.email ?: "admin"
            val token = "INV-${UUID.randomUUID().toString().take(8).uppercase()}"
            val docRef = db.collection("admin_invitations").document()
            val payload = hashMapOf<String, Any>(
                "email" to email.trim().lowercase(),
                "role" to role.name,
                "token" to token,
                "status" to "PENDING",
                "createdBy" to senderEmail,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            recordAuditLog("ADMIN_INVITATION_CREATED", "ADMIN_INVITATION", docRef.id, "Invited $email as ${role.displayName}")
            Result.success(token)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to create administrator invitation", e)
            Result.failure(e)
        }
    }

    // --- 3. BUSINESS TASK MANAGEMENT ---
    fun observeTasks(): Flow<List<BusinessTask>> {
        return db.collection("tasks")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(BusinessTask::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun createBusinessTask(
        title: String,
        description: String,
        category: String,
        priority: String,
        assignedToEmail: String,
        dueDate: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            val taskRef = "WYW-TSK-2026-${Random.nextInt(10000, 99999)}"
            val docRef = db.collection("tasks").document()
            val payload = hashMapOf<String, Any>(
                "taskReference" to taskRef,
                "title" to title,
                "description" to description,
                "category" to category,
                "priority" to priority,
                "status" to "To Do",
                "assignedToEmail" to assignedToEmail,
                "createdByEmail" to (user?.email ?: "operations@writeyourwave.com"),
                "dueDate" to dueDate,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            recordAuditLog("TASK_CREATED", "BUSINESS_TASK", docRef.id, "Created task $taskRef: $title")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to create task", e)
            Result.failure(e)
        }
    }

    suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = hashMapOf<String, Any>(
                "status" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (newStatus == "Completed") {
                updates["completedAt"] = FieldValue.serverTimestamp()
            }
            db.collection("tasks").document(taskId).update(updates).await()
            recordAuditLog("TASK_STATUS_UPDATED", "BUSINESS_TASK", taskId, "Status changed to $newStatus")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to update task", e)
            Result.failure(e)
        }
    }

    // --- 4. AMBASSADOR PLATFORM & COMMISSIONS ---
    fun observeAmbassadorProfiles(): Flow<List<AmbassadorProfile>> {
        return db.collection("ambassador_profiles")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AmbassadorProfile::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAmbassadorProfile(userId: String): Flow<AmbassadorProfile?> {
        return db.collection("ambassador_profiles").document(userId)
            .snapshots()
            .map { it.toObject(AmbassadorProfile::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun submitAmbassadorApplication(
        fullName: String,
        email: String,
        phone: String,
        campus: String,
        socialChannel: String,
        audienceDesc: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = requireUserId()
            val ref = "WYW-AMB-2026-${Random.nextInt(1000, 9999)}"
            val referralCode = (fullName.take(4) + "26").uppercase().replace(" ", "")
            val docRef = db.collection("ambassador_profiles").document(userId)

            val payload = hashMapOf<String, Any>(
                "ambassadorReference" to ref,
                "fullName" to fullName,
                "email" to email,
                "phone" to phone,
                "campusOrAffiliation" to campus,
                "socialChannel" to socialChannel,
                "audienceDesc" to audienceDesc,
                "referralCode" to referralCode,
                "commissionRate" to 0.10, // 10% standard rate
                "status" to "PENDING",
                "totalClicks" to 0L,
                "totalReferrals" to 0L,
                "totalSalesAmount" to 0.0,
                "pendingCommissions" to 0.0,
                "approvedCommissions" to 0.0,
                "paidCommissions" to 0.0,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            recordAuditLog("AMBASSADOR_APPLICATION", "AMBASSADOR_PROFILE", userId, "Submitted application for $fullName")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to submit ambassador application", e)
            Result.failure(e)
        }
    }

    suspend fun updateAmbassadorStatus(
        ambassadorId: String,
        newStatus: String,
        commissionRate: Double? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = hashMapOf<String, Any>(
                "status" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (commissionRate != null) {
                updates["commissionRate"] = commissionRate
            }
            db.collection("ambassador_profiles").document(ambassadorId).update(updates).await()
            recordAuditLog("AMBASSADOR_STATUS_CHANGE", "AMBASSADOR_PROFILE", ambassadorId, "Status updated to $newStatus")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to update ambassador status", e)
            Result.failure(e)
        }
    }

    // --- 5. AMBASSADOR REFERRALS & PAYOUTS ---
    fun observeAmbassadorReferrals(ambassadorId: String): Flow<List<AmbassadorReferral>> {
        return db.collection("ambassador_referrals")
            .whereEqualTo("ambassadorId", ambassadorId)
            .snapshots()
            .map { it.toObjects(AmbassadorReferral::class.java) }
            .flowOn(Dispatchers.IO)
    }

    fun observeAllReferrals(): Flow<List<AmbassadorReferral>> {
        return db.collection("ambassador_referrals")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AmbassadorReferral::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun recordReferralSale(
        ambassadorId: String,
        ambassadorEmail: String,
        referralCode: String,
        orderId: String,
        orderReference: String,
        customerName: String,
        productTitle: String,
        grossAmount: Double,
        commissionRate: Double = 0.10
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val commissionAmount = grossAmount * commissionRate
            val refId = "WYW-REF-2026-${Random.nextInt(10000, 99999)}"
            val docRef = db.collection("ambassador_referrals").document()

            val payload = hashMapOf<String, Any>(
                "referralReference" to refId,
                "ambassadorId" to ambassadorId,
                "ambassadorEmail" to ambassadorEmail,
                "referralCode" to referralCode,
                "orderId" to orderId,
                "orderReference" to orderReference,
                "customerName" to customerName,
                "productTitle" to productTitle,
                "grossAmount" to grossAmount,
                "commissionAmount" to commissionAmount,
                "status" to "PENDING",
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()

            // Atomically increment ambassador sales in ambassador profile
            db.collection("ambassador_profiles").document(ambassadorId).update(
                "totalReferrals", FieldValue.increment(1),
                "totalSalesAmount", FieldValue.increment(grossAmount),
                "pendingCommissions", FieldValue.increment(commissionAmount),
                "updatedAt", FieldValue.serverTimestamp()
            ).await()

            recordAuditLog("REFERRAL_RECORDED", "AMBASSADOR_REFERRAL", docRef.id, "Attributed $orderReference to $referralCode (Commission: $$commissionAmount)")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to record referral", e)
            Result.failure(e)
        }
    }

    fun observeAmbassadorPayouts(ambassadorId: String): Flow<List<AmbassadorPayout>> {
        return db.collection("ambassador_payouts")
            .whereEqualTo("ambassadorId", ambassadorId)
            .snapshots()
            .map { it.toObjects(AmbassadorPayout::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun recordAmbassadorPayout(
        ambassadorId: String,
        ambassadorEmail: String,
        amount: Double,
        method: String,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userEmail = auth.currentUser?.email ?: "finance@writeyourwave.com"
            val payoutRef = "WYW-PAY-2026-${Random.nextInt(10000, 99999)}"
            val docRef = db.collection("ambassador_payouts").document()

            val payload = hashMapOf<String, Any>(
                "payoutReference" to payoutRef,
                "ambassadorId" to ambassadorId,
                "ambassadorEmail" to ambassadorEmail,
                "amount" to amount,
                "payoutMethod" to method,
                "status" to "COMPLETED",
                "processedBy" to userEmail,
                "notes" to notes,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()

            // Update ambassador profile balances
            db.collection("ambassador_profiles").document(ambassadorId).update(
                "paidCommissions", FieldValue.increment(amount),
                "pendingCommissions", FieldValue.increment(-amount),
                "updatedAt", FieldValue.serverTimestamp()
            ).await()

            recordAuditLog("PAYOUT_EXECUTED", "AMBASSADOR_PAYOUT", docRef.id, "Paid $$amount to $ambassadorEmail via $method")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to record payout", e)
            Result.failure(e)
        }
    }

    // --- 6. PURCHASE ORDERS & INVENTORY COST VALUE ---
    fun observePurchaseOrders(): Flow<List<SupplierPurchaseOrder>> {
        return db.collection("purchase_orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(SupplierPurchaseOrder::class.java) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun createPurchaseOrder(
        supplierName: String,
        sku: String,
        quantity: Long,
        unitCost: Double,
        notes: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val poRef = "WYW-PO-2026-${Random.nextInt(10000, 99999)}"
            val docRef = db.collection("purchase_orders").document()
            val totalCost = quantity * unitCost

            val payload = hashMapOf<String, Any>(
                "poReference" to poRef,
                "supplierName" to supplierName,
                "sku" to sku,
                "quantityOrdered" to quantity,
                "unitCost" to unitCost,
                "totalCost" to totalCost,
                "status" to "RECEIVED",
                "notes" to notes,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            recordAuditLog("PO_CREATED", "PURCHASE_ORDER", docRef.id, "Created purchase order $poRef: $quantity units at $$unitCost")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to create purchase order", e)
            Result.failure(e)
        }
    }
}

