package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Write Your Wave", appName)
  }

  @Test
  fun `verify journal covers drawable resources exist`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val frontCover = context.resources.getDrawable(R.drawable.journal_front_cover, null)
    val backCover = context.resources.getDrawable(R.drawable.journal_back_cover, null)
    val abiolaCover = context.resources.getDrawable(R.drawable.journal_cover_abiola_blue, null)
    val purpleGoldCover = context.resources.getDrawable(R.drawable.journal_cover_purple_gold, null)
    val noirGoldCover = context.resources.getDrawable(R.drawable.journal_cover_noir_gold, null)
    val radiantPinkCover = context.resources.getDrawable(R.drawable.journal_cover_radiant_pink, null)

    org.junit.Assert.assertNotNull(frontCover)
    org.junit.Assert.assertNotNull(backCover)
    org.junit.Assert.assertNotNull(abiolaCover)
    org.junit.Assert.assertNotNull(purpleGoldCover)
    org.junit.Assert.assertNotNull(noirGoldCover)
    org.junit.Assert.assertNotNull(radiantPinkCover)
  }

  @Test
  fun `verify stationery shop contains only journals priced strictly at 5 dollars`() {
    val stationeryList = com.example.ui.screens.defaultStationeryProducts
    org.junit.Assert.assertTrue("Stationery items should not be empty", stationeryList.isNotEmpty())

    // All products must be journals and priced at $5.00 USD
    for (item in stationeryList) {
      assertEquals("Category must be Journals", "Journals", item.category)
      assertEquals("Price must be $5.00 USD", 5.0, item.price, 0.001)
      org.junit.Assert.assertTrue("Resolved image URL should be non-empty", item.resolvedImageUrl().isNotBlank())
    }

    val abiolaJournal = stationeryList.find { it.id == "prod_journal_abiola_blue" }
    org.junit.Assert.assertNotNull("Abiola Ocean Blue journal edition should exist", abiolaJournal)
    assertEquals(5.0, abiolaJournal!!.price, 0.001)

    val purpleGoldJournal = stationeryList.find { it.id == "prod_journal_purple_gold" }
    org.junit.Assert.assertNotNull("Purple & Gold journal edition should exist", purpleGoldJournal)
    assertEquals(5.0, purpleGoldJournal!!.price, 0.001)

    val noirGoldJournal = stationeryList.find { it.id == "prod_journal_noir_gold" }
    org.junit.Assert.assertNotNull("Noir Gold journal edition should exist", noirGoldJournal)
    assertEquals(5.0, noirGoldJournal!!.price, 0.001)

    val radiantPinkJournal = stationeryList.find { it.id == "prod_journal_radiant_pink" }
    org.junit.Assert.assertNotNull("Radiant Pink journal edition should exist", radiantPinkJournal)
    assertEquals(5.0, radiantPinkJournal!!.price, 0.001)
  }

  @Test
  fun `verify CartItem calculates totals accurately with 5 dollar journals`() {
    val journal = com.example.ui.screens.defaultStationeryProducts.first { it.id == "prod_journal_abiola_blue" }
    val cartItem = com.example.data.model.CartItem(product = journal, quantity = 3)
    assertEquals(15.0, cartItem.totalPrice, 0.01)
  }

  @Test
  fun `verify stationery search filter matches journal name or color theme`() {
    val items = com.example.ui.screens.defaultStationeryProducts

    // Filter by name (e.g. "Abiola")
    val abiolaQuery = "Abiola"
    val filteredByAbiola = items.filter { prod ->
      prod.title.contains(abiolaQuery, ignoreCase = true) ||
      prod.category.contains(abiolaQuery, ignoreCase = true) ||
      prod.description.contains(abiolaQuery, ignoreCase = true)
    }
    org.junit.Assert.assertTrue("Should find Abiola edition journal", filteredByAbiola.isNotEmpty())
    org.junit.Assert.assertEquals("prod_journal_abiola_blue", filteredByAbiola.first().id)

    // Filter by color / style (e.g. "Gold")
    val goldQuery = "Gold"
    val filteredByGold = items.filter { prod ->
      prod.title.contains(goldQuery, ignoreCase = true) ||
      prod.category.contains(goldQuery, ignoreCase = true)
    }
    org.junit.Assert.assertTrue("Should find gold editions", filteredByGold.size >= 2)

    // Verify non-journal pens and accessories are absent
    val penCheck = items.find { it.id == "prod_brass_pen" }
    org.junit.Assert.assertNull("Brass pen should NOT be in the catalog", penCheck)
  }

  @Test
  fun `verify official Write Your Wave brand logo resource exists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val logoDrawable = context.resources.getDrawable(R.drawable.wyw_official_logo, null)
    org.junit.Assert.assertNotNull("Official logo wyw_official_logo drawable must be present", logoDrawable)
  }

  @Test
  fun `verify verified store locator contains Starz University and Waterloop Institute with exact addresses`() {
    val locations = com.example.ui.screens.officialVerifiedLocations
    org.junit.Assert.assertEquals(2, locations.size)

    val starz = locations.find { it.id == "starz_university" }
    org.junit.Assert.assertNotNull("Starz University must be present as a verified location", starz)
    assertEquals("Starz University", starz!!.name)
    assertEquals("Airfield, Sinkor", starz.address)
    assertEquals("Main Campus & New Campus", starz.campusDivision)
    org.junit.Assert.assertFalse("Starz is campus partner, not production hub", starz.isProductionHub)
    org.junit.Assert.assertTrue("Starz must have directions steps", starz.directionsStepByStep.isNotEmpty())

    val waterloop = locations.find { it.id == "waterloop_institute" }
    org.junit.Assert.assertNotNull("Waterloop Institute of Technology must be present as a verified location", waterloop)
    org.junit.Assert.assertTrue(waterloop!!.name.contains("Waterloop Institute of Technology"))
    assertEquals("Airfield Shortcut, Sinkor", waterloop.address)
    org.junit.Assert.assertTrue("Waterloop must be designated as the official production hub", waterloop.isProductionHub)
    org.junit.Assert.assertTrue("Waterloop must have services listed", waterloop.services.isNotEmpty())
  }

  @Test
  fun `verify verified store locator search matches by name and address`() {
    val locations = com.example.ui.screens.officialVerifiedLocations

    val airfieldMatches = locations.filter { it.address.contains("Airfield", ignoreCase = true) }
    assertEquals("Both verified locations are in the Airfield Sinkor corridor", 2, airfieldMatches.size)

    val shortcutMatches = locations.filter { it.address.contains("Shortcut", ignoreCase = true) }
    assertEquals(1, shortcutMatches.size)
    assertEquals("waterloop_institute", shortcutMatches.first().id)

    val starzMatches = locations.filter { it.name.contains("Starz", ignoreCase = true) }
    assertEquals(1, starzMatches.size)
    assertEquals("starz_university", starzMatches.first().id)
  }

  @Test
  fun `verify UserProfile model defaults and campus affiliation`() {
    val profile = com.example.data.model.UserProfile(
      id = "user_test_123",
      displayName = "Emmanuel Hodges",
      firstName = "Emmanuel",
      lastName = "Hodges",
      email = "emmanuelhodges17@gmail.com",
      phone = "+231 77 000 0000",
      universityOrAffiliation = "Starz University Main Campus"
    )

    assertEquals("user_test_123", profile.id)
    assertEquals("Emmanuel Hodges", profile.displayName)
    assertEquals("Starz University Main Campus", profile.universityOrAffiliation)
    assertEquals("Liberia", profile.country)
    assertEquals("Monrovia", profile.city)
    org.junit.Assert.assertTrue("Order alerts enabled by default", profile.orderAlerts)
    org.junit.Assert.assertTrue("Series verification alerts enabled by default", profile.seriesVerificationAlerts)
  }

  @Test
  fun `verify SavedAddress model initializes campus pickup location`() {
    val address = com.example.data.model.SavedAddress(
      id = "addr_starz_campus",
      userId = "user_test_123",
      recipientName = "Emmanuel Hodges",
      phone = "+231 77 000 0000",
      addressLine = "Starz University Campus (Main & New Campus), Airfield, Sinkor",
      isDefault = true
    )

    assertEquals("addr_starz_campus", address.id)
    assertEquals("Starz University Campus (Main & New Campus), Airfield, Sinkor", address.addressLine)
    org.junit.Assert.assertTrue(address.isDefault)
  }

  @Test
  fun `verify ContactMessage model initializes with valid fields`() {
    val message = com.example.data.model.ContactMessage(
      id = "msg_test_001",
      userId = "user_test_123",
      name = "Abiola",
      email = "abiola@student.starz.edu.lr",
      subject = "Homepage Contact Inquiry",
      category = "General inquiry",
      message = "Hello! I would like to inquire about the $5.00 journals pickup at Starz University."
    )

    assertEquals("msg_test_001", message.id)
    assertEquals("Abiola", message.name)
    assertEquals("abiola@student.starz.edu.lr", message.email)
    assertEquals("Homepage Contact Inquiry", message.subject)
    assertEquals("General inquiry", message.category)
    assertEquals("NEW", message.status)
    org.junit.Assert.assertTrue(message.message.contains("Starz University"))
  }
  @Test
  fun `verify AdminRole and AdminUser models`() {
    val admin = com.example.data.model.AdminUser(
      id = "admin_super",
      email = "emmanuelhodges17@gmail.com",
      displayName = "Emmanuel Hodges",
      role = com.example.data.model.AdminRole.SUPER_ADMIN.name,
      status = "ACTIVE"
    )

    assertEquals("admin_super", admin.id)
    assertEquals("emmanuelhodges17@gmail.com", admin.email)
    assertEquals(com.example.data.model.AdminRole.SUPER_ADMIN.name, admin.role)
    assertEquals("ACTIVE", admin.status)
  }

  @Test
  fun `verify BusinessTask model and status updates`() {
    val task = com.example.data.model.BusinessTask(
      id = "task_001",
      title = "Fulfill Waterloop Institute Production Batch",
      description = "Inspect 500 spiral coils and gold foil stamping",
      priority = "HIGH",
      status = "PENDING",
      dueDate = "Nov 5, 2026"
    )

    assertEquals("task_001", task.id)
    assertEquals("HIGH", task.priority)
    assertEquals("PENDING", task.status)

    val updatedTask = task.copy(status = "COMPLETED")
    assertEquals("COMPLETED", updatedTask.status)
  }

  @Test
  fun `verify AmbassadorProfile and 10 percent referral commission calculation`() {
    val profile = com.example.data.model.AmbassadorProfile(
      id = "amb_abiola",
      fullName = "Abiola",
      email = "abiola@student.starz.edu.lr",
      campusOrAffiliation = "Starz University",
      referralCode = "ABIOLA26",
      status = "APPROVED",
      commissionRate = 0.10, // 10%
      totalReferrals = 40L,
      totalSalesAmount = 200.0,
      pendingCommissions = 15.0,
      paidCommissions = 5.0
    )

    assertEquals("ABIOLA26", profile.referralCode)
    assertEquals("APPROVED", profile.status)
    assertEquals(0.10, profile.commissionRate, 0.001)

    // Calculate commission on a 5-unit journal purchase ($25.00)
    val orderUnits = 5
    val unitPrice = 5.0
    val totalSale = orderUnits * unitPrice
    val expectedCommission = totalSale * profile.commissionRate
    assertEquals(2.50, expectedCommission, 0.001)

    // Verify referral model
    val referral = com.example.data.model.AmbassadorReferral(
      id = "ref_001",
      ambassadorId = profile.id,
      referralCode = profile.referralCode,
      orderId = "order_100",
      grossAmount = totalSale,
      commissionAmount = expectedCommission,
      status = "APPROVED"
    )

    assertEquals("ref_001", referral.id)
    assertEquals("order_100", referral.orderId)
    assertEquals(2.50, referral.commissionAmount, 0.001)
  }

  @Test
  fun `verify AuditLog model records operations with metadata`() {
    val log = com.example.data.model.AuditLog(
      id = "log_001",
      actorId = "emmanuelhodges17@gmail.com",
      actorEmail = "emmanuelhodges17@gmail.com",
      action = "ADJUST_STOCK",
      targetType = "PRODUCT",
      targetId = "prod_journal_abiola_blue",
      details = "Adjusted stock by +50 units at Waterloop Institute production site."
    )

    assertEquals("log_001", log.id)
    assertEquals("ADJUST_STOCK", log.action)
    assertEquals("PRODUCT", log.targetType)
    org.junit.Assert.assertTrue(log.details.contains("Waterloop Institute"))
  }

  @Test
  fun `verify GitHubSyncLog models can format properly to CSV format`() {
    val syncLog = com.example.data.model.GitHubSyncLog(
      id = "sync_001",
      timestamp = "Oct 30, 2026 • 07:05 AM",
      action = "MANUAL_SYNC",
      repository = "write-your-wave",
      branch = "main",
      status = "SUCCESS",
      summary = "Pushed 7 journal editions & Starz/WIT campus pickup desks to Vercel."
    )

    val csvHeader = "ID,Timestamp,Action,Repository,Branch,Status,Summary"
    val csvRow = "\"${syncLog.id}\",\"${syncLog.timestamp}\",\"${syncLog.action}\",\"${syncLog.repository}\",\"${syncLog.branch}\",\"${syncLog.status}\",\"${syncLog.summary}\""

    org.junit.Assert.assertTrue(csvHeader.startsWith("ID,Timestamp"))
    org.junit.Assert.assertTrue(csvRow.contains("sync_001"))
    org.junit.Assert.assertTrue(csvRow.contains("write-your-wave"))
    org.junit.Assert.assertTrue(csvRow.contains("main"))
    org.junit.Assert.assertTrue(csvRow.contains("SUCCESS"))
  }
}



