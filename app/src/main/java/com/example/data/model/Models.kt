package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val title: String = "",
    val slug: String = "",
    val category: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val sku: String = "",
    val coverImageUrl: String? = null,
    val backCoverImageUrl: String? = null,
    val isPublished: Boolean = true,
    val inStock: Boolean = true,
    val stockQuantity: Long = 0L,
    val features: List<String> = emptyList(),
    val imageUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun resolvedFrontCoverRes(): Int = when {
        id == "prod_journal_hazel_quiqui" || slug.contains("hazel", ignoreCase = true) || title.contains("Hazel", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_hazel_quiqui
        id == "prod_journal_zinnah_kamara" || slug.contains("zinnah", ignoreCase = true) || title.contains("Zinnah", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_zinnah_kamara
        id == "prod_journal_purple_scholar" || slug.contains("scholar", ignoreCase = true) || title.contains("Scholar", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_purple_scholar
        id == "prod_journal_purple_gold" || slug.contains("purple", ignoreCase = true) || title.contains("Purple", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_purple_gold
        id == "prod_journal_noir_gold" || slug.contains("noir", ignoreCase = true) || title.contains("Noir", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_noir_gold
        id == "prod_journal_radiant_pink" || slug.contains("pink", ignoreCase = true) || title.contains("Radiant", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_radiant_pink
        id == "prod_journal_abiola_blue" || slug.contains("blue", ignoreCase = true) || title.contains("Ocean", ignoreCase = true) || title.contains("Abiola", ignoreCase = true) ->
            com.example.R.drawable.journal_cover_abiola_blue
        else -> com.example.R.drawable.journal_cover_hazel_quiqui
    }
    fun resolvedBackCoverRes(): Int = com.example.R.drawable.journal_back_cover

    fun resolvedImageUrl(): String {
        if (!imageUrl.isNullOrBlank()) return imageUrl
        if (!coverImageUrl.isNullOrBlank()) return coverImageUrl
        // Return local android resource URI so Coil loads the user's authentic cover directly
        return "android.resource://com.example/" + resolvedFrontCoverRes()
    }
}

@IgnoreExtraProperties
data class CartItem(
    val product: Product = Product(),
    val quantity: Int = 1
) {
    val totalPrice: Double get() = product.price * quantity
}

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val orderReference: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String? = null,
    val deliveryAddress: String = "",
    val productId: String = "",
    val productTitle: String = "",
    val quantity: Long = 1L,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val currency: String = "USD",
    val customizationDetails: String? = null,
    val status: String = "PENDING", // PENDING, CONFIRMED, PROCESSING, READY, OUT_FOR_DELIVERY, COMPLETED, CANCELLED
    val paymentStatus: String = "UNPAID", // UNPAID, PENDING, PAID, REFUNDED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class InventoryMovement(
    @DocumentId val id: String = "",
    val productId: String = "",
    val quantityChange: Long = 0L,
    val movementType: String = "INITIAL", // INITIAL, RECEIVED, ADJUSTMENT, RESERVED, FULFILLED, RETURNED, DAMAGED
    val reason: String = "",
    val performedBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomizationRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String? = null,
    val productId: String = "",
    val nameOnCover: String = "",
    val preferredColors: String? = null,
    val motivationalQuote: String? = null,
    val customInstructions: String? = null,
    val status: String = "SUBMITTED", // SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, COMPLETED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BulkOrder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val organizationName: String = "",
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val estimatedQuantity: Long = 50L,
    val customizationRequirements: String? = null,
    val deadlineDate: String? = null,
    val status: String = "SUBMITTED", // SUBMITTED, QUOTATION_PREPARED, APPROVED, REJECTED, CONVERTED
    val quotationAmount: Double? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ContactMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val category: String = "General inquiry",
    val message: String = "",
    val status: String = "NEW", // NEW, IN_PROGRESS, RESOLVED
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Ambassador(
    @DocumentId val id: String = "",
    val name: String = "",
    val slug: String = "",
    val role: String = "",
    val biography: String = "",
    val imageUrl: String? = null,
    val featuredProduct: String? = null,
    val isPublished: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class JournalInteriorPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val prompt: String,
    val templateCategory: String
)

@IgnoreExtraProperties
data class JournalEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val pillar: String = "Study", // Study, Career, Financial Freedom, Self Care, Success
    val prompt: String = "",
    val mood: String = "Focused", // Focused, Inspired, Grateful, Calm, Ambitious, Reflective
    val wordCount: Long = 0L,
    val readingTimeMinutes: Long = 1L,
    val isFavorite: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "", // userId
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val city: String = "Monrovia",
    val country: String = "Liberia",
    val universityOrAffiliation: String = "Starz University", // default campus partner
    val bio: String = "Living with purpose, writing my wave.",
    val writingInterests: List<String> = listOf("Academic Planning", "Career Goals", "Daily Reflections"),
    val emailNotifications: Boolean = true,
    val orderAlerts: Boolean = true,
    val seriesVerificationAlerts: Boolean = true,
    val preferredLanguage: String = "English",
    val preferredCurrency: String = "USD",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedAddress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val recipientName: String = "",
    val phone: String = "",
    val addressLine: String = "", // e.g. "Starz University Campus (Main & New Campus), Airfield, Sinkor"
    val city: String = "Sinkor, Monrovia",
    val country: String = "Liberia",
    val deliveryInstructions: String = "",
    val isDefault: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

// --- ENTERPRISE BUSINESS MANAGEMENT SYSTEM ENTITIES ---

enum class AdminRole(val displayName: String, val description: String) {
    SUPER_ADMIN("Super Admin", "Full system authority, administrator management, and financial governance"),
    OPERATIONS_ADMIN("Operations Admin", "Inventory movements, product catalog, and fulfillment workflows"),
    SALES_MANAGER("Sales Manager", "Order lifecycle, customer transactions, and sales performance"),
    FINANCE_MANAGER("Finance Manager", "Revenue, COGS, gross margins, and commission liabilities"),
    AMBASSADOR_MANAGER("Ambassador Manager", "Ambassador applications, referral tracking, and commission approval"),
    READ_ONLY_ANALYST("Read-Only Analyst", "Audit inspection and business intelligence reports")
}

@IgnoreExtraProperties
data class AdminUser(
    @DocumentId val id: String = "", // Firebase Auth UID
    val email: String = "",
    val displayName: String = "",
    val role: String = AdminRole.OPERATIONS_ADMIN.name,
    val permissions: List<String> = emptyList(),
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED, REVOKED
    val invitedBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdminInvitation(
    @DocumentId val id: String = "",
    val email: String = "",
    val role: String = AdminRole.OPERATIONS_ADMIN.name,
    val token: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, EXPIRED, REVOKED
    val createdBy: String = "",
    val expiresAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AuditLog(
    @DocumentId val id: String = "",
    val actorId: String = "",
    val actorEmail: String = "",
    val actorRole: String = "",
    val action: String = "", // e.g., "INVENTORY_ADJUSTMENT", "ROLE_ASSIGNMENT", "ORDER_STATUS_UPDATE"
    val targetType: String = "", // e.g., "PRODUCT", "ORDER", "ADMIN_USER"
    val targetId: String = "",
    val details: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GitHubSyncLog(
    val id: String = "",
    val timestamp: String = "",
    val action: String = "REPOSITORY_VERIFICATION",
    val repository: String = "write-your-wave",
    val branch: String = "main",
    val status: String = "SUCCESS",
    val summary: String = ""
)

@IgnoreExtraProperties
data class BusinessTask(
    @DocumentId val id: String = "",
    val taskReference: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "Operations", // Operations, Inventory, Ambassadors, Finance, Fulfillment
    val priority: String = "Normal", // Low, Normal, High, Urgent
    val status: String = "To Do", // Backlog, To Do, In Progress, Under Review, Completed, Cancelled
    val assignedToEmail: String = "",
    val createdByEmail: String = "",
    val dueDate: String = "",
    val completedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AmbassadorProfile(
    @DocumentId val id: String = "", // userId
    val ambassadorReference: String = "", // WYW-AMB-2026-XXXXX
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val campusOrAffiliation: String = "Starz University",
    val socialChannel: String = "",
    val audienceDesc: String = "",
    val referralCode: String = "", // e.g., "ABIOLA26"
    val commissionRate: Double = 0.10, // 10% on $5.00 journals = $0.50/unit
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, SUSPENDED
    val totalClicks: Long = 0L,
    val totalReferrals: Long = 0L,
    val totalSalesAmount: Double = 0.0,
    val pendingCommissions: Double = 0.0,
    val approvedCommissions: Double = 0.0,
    val paidCommissions: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AmbassadorReferral(
    @DocumentId val id: String = "",
    val referralReference: String = "", // WYW-REF-2026-XXXXX
    val ambassadorId: String = "",
    val ambassadorEmail: String = "",
    val referralCode: String = "",
    val orderId: String = "",
    val orderReference: String = "",
    val customerName: String = "",
    val productTitle: String = "",
    val grossAmount: Double = 0.0,
    val commissionAmount: Double = 0.0,
    val status: String = "PENDING", // PENDING, APPROVED, PAID, REVERSED
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AmbassadorPayout(
    @DocumentId val id: String = "",
    val payoutReference: String = "", // WYW-PAY-2026-XXXXX
    val ambassadorId: String = "",
    val ambassadorEmail: String = "",
    val amount: Double = 0.0,
    val payoutMethod: String = "Mobile Money", // Mobile Money, Campus Desk Pickup, Bank Wire
    val status: String = "COMPLETED", // PENDING, COMPLETED, FAILED
    val processedBy: String = "",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SupplierPurchaseOrder(
    @DocumentId val id: String = "",
    val poReference: String = "", // WYW-PO-2026-XXXXX
    val supplierName: String = "Waterloop Production Center (WIT)",
    val sku: String = "WYW-JRN-2026-01",
    val quantityOrdered: Long = 100L,
    val unitCost: Double = 2.50, // $2.50 manufacturing cost for $5.00 journal (50% gross margin)
    val totalCost: Double = 250.00,
    val status: String = "RECEIVED", // DRAFT, SUBMITTED, PARTIAL, RECEIVED, CANCELLED
    val expectedDeliveryDate: String = "2026-11-15",
    val notes: String = "Batch production for Starz University campus launch",
    @ServerTimestamp val createdAt: Timestamp? = null
)


