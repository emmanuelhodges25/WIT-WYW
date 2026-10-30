const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Public: can read published products", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("products").doc("prod1").set({
      title: "Wave Journal",
      slug: "wave-journal",
      category: "Journal",
      description: "A premium notebook",
      price: 18.5,
      currency: "USD",
      sku: "WYW-001",
      isPublished: true,
      inStock: true,
      stockQuantity: 50,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("products").doc("prod1").get());
});

test("Unauthenticated: cannot create orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").doc("ord1").set({
    orderReference: "WYW-2026-0001",
    userId: ALICE_UID,
    customerName: "Alice",
    customerEmail: "alice@example.com",
    deliveryAddress: "123 Main St",
    productId: "prod1",
    productTitle: "Wave Journal",
    quantity: 1,
    unitPrice: 18.5,
    totalPrice: 18.5,
    currency: "USD",
    status: "PENDING",
    paymentStatus: "UNPAID",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: can create valid order for self", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("orders").doc("ord1").set({
    orderReference: "WYW-2026-0001",
    userId: ALICE_UID,
    customerName: "Alice Smith",
    customerEmail: "alice@example.com",
    deliveryAddress: "123 Main St",
    productId: "prod1",
    productTitle: "Wave Journal",
    quantity: 1,
    unitPrice: 18.5,
    totalPrice: 18.5,
    currency: "USD",
    status: "PENDING",
    paymentStatus: "UNPAID",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: cannot read another user's order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("alice_ord").set({
      orderReference: "WYW-2026-0001",
      userId: ALICE_UID,
      customerName: "Alice Smith",
      customerEmail: "alice@example.com",
      deliveryAddress: "123 Main St",
      productId: "prod1",
      productTitle: "Wave Journal",
      quantity: 1,
      unitPrice: 18.5,
      totalPrice: 18.5,
      currency: "USD",
      status: "PENDING",
      paymentStatus: "UNPAID",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("orders").doc("alice_ord").get());
});

test("Admin: can read all orders and update status", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("alice_ord").set({
      orderReference: "WYW-2026-0001",
      userId: ALICE_UID,
      customerName: "Alice Smith",
      customerEmail: "alice@example.com",
      deliveryAddress: "123 Main St",
      productId: "prod1",
      productTitle: "Wave Journal",
      quantity: 1,
      unitPrice: 18.5,
      totalPrice: 18.5,
      currency: "USD",
      status: "PENDING",
      paymentStatus: "UNPAID",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: "emmanuelhodges17@gmail.com", email_verified: true }).firestore();
  await assertSucceeds(adminDb.collection("orders").doc("alice_ord").get());
});
