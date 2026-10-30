package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.JournalEntry
import com.example.data.model.Order
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class WriteYourWaveRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun createOrder_authenticatedUser_success() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = WriteYourWaveRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createOrder(
        productId = "prod_univ_wave",
        productTitle = "University Student Wave Notebook",
        unitPrice = 16.0,
        quantity = 2L,
        customerName = "Alice Smith",
        customerEmail = ALICE_EMAIL,
        customerPhone = "1234567890",
        deliveryAddress = "Campus Hall Dorm 4B",
        customizationDetails = "Name: Alice - Wave Maker"
      )
    }

    assertTrue("Order creation should succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
    val order = result.getOrThrow()
    assertNotNull(order.id)
    assertTrue(order.orderReference.startsWith("WYW-2026-"))
  }

  @Test
  fun observeUserOrders_authenticatedOwner_returnsMatchingOrders() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = WriteYourWaveRepository(firestore)

    val createdOrder = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createOrder(
        productId = "prod_student_crest",
        productTitle = "Future Leader Student Notebook",
        unitPrice = 12.0,
        quantity = 1L,
        customerName = "Alice Smith",
        customerEmail = ALICE_EMAIL,
        customerPhone = null,
        deliveryAddress = "Campus Hall Dorm 4B",
        customizationDetails = null
      ).getOrThrow()
    }

    val emittedOrders = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeUserOrders(uid).first { list -> list.any { it.id == createdOrder.id } }
    }
    assertTrue(emittedOrders.any { it.id == createdOrder.id })
  }

  @Test
  fun getOrder_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = WriteYourWaveRepository(firestore)

    val order = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.createOrder(
        productId = "prod_daily_wave",
        productTitle = "Write Your Wave Daily Reflection Journal",
        unitPrice = 22.0,
        quantity = 1L,
        customerName = "Alice Smith",
        customerEmail = ALICE_EMAIL,
        customerPhone = null,
        deliveryAddress = "Campus Hall Dorm 4B",
        customizationDetails = null
      ).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    try {
      withTimeout<DocumentSnapshot>(DEFAULT_TIMEOUT_MS) {
        firestore.collection("orders").document(order.id).get().await()
      }
      fail("Bob should not be able to read Alice's order directly")
    } catch (e: Exception) {
      val fse = e.unwrapFirestoreException()
      assertNotNull("Expected FirebaseFirestoreException but got $e", fse)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fse?.code)
    }
  }

  @Test
  fun createOrder_unauthenticatedUser_fails() = runBlocking {
    auth.signOut()
    val repository = WriteYourWaveRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createOrder(
        productId = "prod_premium_gold",
        productTitle = "Ocean Wave Signature Premium Journal",
        unitPrice = 34.0,
        quantity = 1L,
        customerName = "Anonymous",
        customerEmail = "anon@test.com",
        customerPhone = null,
        deliveryAddress = "Nowhere",
        customizationDetails = null
      )
    }
    assertTrue("Unauthenticated order creation must fail", result.isFailure)
  }

  @Test
  fun saveJournalEntry_authenticatedUser_successAndCanObserve() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = WriteYourWaveRepository(firestore)

    val entry = JournalEntry(
      title = "First Milestone Reflection",
      content = "Completed module 3 of economics with clear clarity and focus.",
      pillar = "Study",
      prompt = "What did you learn today?",
      mood = "Focused",
      isFavorite = true
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveJournalEntry(entry)
    }

    assertTrue("Journal entry save should succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
    val saved = result.getOrThrow()
    assertTrue(saved.id.isNotBlank())
    assertEquals(uid, saved.userId)

    val entries = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeUserJournalEntries(uid).first { list -> list.any { it.id == saved.id } }
    }
    assertTrue("Observed entries must contain the saved entry", entries.any { it.id == saved.id })
  }

  @Test
  fun getJournalEntry_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = WriteYourWaveRepository(firestore)

    val entry = JournalEntry(
      title = "Alice Private Reflections",
      content = "Deep private thoughts.",
      pillar = "Self Care",
      prompt = "What brought you peace?",
      mood = "Calm"
    )

    val saved = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveJournalEntry(entry).getOrThrow()
    }

    // Switch to Bob
    signInTestUser(BOB_EMAIL)
    try {
      withTimeout<DocumentSnapshot>(DEFAULT_TIMEOUT_MS) {
        firestore.collection("users")
          .document(aliceUid)
          .collection("journal_entries")
          .document(saved.id)
          .get()
          .await()
      }
      fail("Bob should not be able to read Alice's private journal entry")
    } catch (e: Exception) {
      val fse = e.unwrapFirestoreException()
      assertNotNull("Expected FirebaseFirestoreException but got $e", fse)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fse?.code)
    }
  }

  @Test
  fun saveJournalEntry_unauthenticatedUser_fails() = runBlocking {
    auth.signOut()
    val repository = WriteYourWaveRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveJournalEntry(
        JournalEntry(
          title = "Ghost",
          content = "Unauthenticated content",
          pillar = "Study"
        )
      )
    }
    assertTrue("Unauthenticated journal save must fail", result.isFailure)
  }

  private companion object {
    const val ALICE_EMAIL = "alice_test@example.com"
    const val BOB_EMAIL = "bob_test@example.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
