package flizpay2.flizpaysdk

import flizpay2.flizpaysdk.lib.TransactionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class TransactionServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var transactionService: TransactionService
    private val testToken = "test-token"
    private val testAmount = "100.00"
    @Volatile
    var capturedResult: Result<String>? = null

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val mockJsonObject = mockk<JSONObject>(relaxed = true)
        mockkConstructor(JSONObject::class)
        every { JSONObject().put(any<String>(), any<Any>()) } returns mockJsonObject

        // Override API_URL constant for testing
        Constants.API_URL = mockWebServer.url("/").toString().removeSuffix("/")
        transactionService = TransactionService()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test successful transaction fetch`() {
        val latch = CountDownLatch(1)
        val rawResponse = """
                {
                    "data": {
                        "redirectUrl": "https://test.flizpay.com/checkout"
                    }
                }
            """.trimIndent()

        every { JSONObject(rawResponse).get("data") } returns ""
        every { JSONObject("").get("redirectUrl") } returns "https://test.flizpay.com/checkout"

        // Prepare mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(rawResponse)
        mockWebServer.enqueue(mockResponse)

        // Execute request
        transactionService.fetchTransactionInfo(testToken, testAmount) { result ->
            capturedResult = result
            latch.countDown()
        }

        // Wait for async operation
        assertTrue(latch.await(6, TimeUnit.SECONDS))

        // Verify response
        assertTrue(capturedResult?.isSuccess == true)
        assertEquals(
            "https://test.flizpay.com/checkout",
            capturedResult?.getOrNull()
        )

        // Verify request
        val recordedRequest = mockWebServer.takeRequest()

        assertEquals("POST", recordedRequest.method)
        assertEquals("/transactions", recordedRequest.path)
        assertEquals("Bearer $testToken", recordedRequest.getHeader("Authentication"))
        assertEquals("application/json; charset=utf-8", recordedRequest.getHeader("Content-Type"))
    }

    @Test
    fun `test failed transaction fetch - server error`() {
        val latch = CountDownLatch(1)
        var capturedResult: Result<String>? = null

        // Prepare error response
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error")
        mockWebServer.enqueue(mockResponse)

        // Execute request
        transactionService.fetchTransactionInfo(testToken, testAmount) { result ->
            capturedResult = result
            latch.countDown()
        }

        // Wait for async operation
        assertTrue(latch.await(5, TimeUnit.SECONDS))

        // Verify response
        assertTrue(capturedResult?.isFailure == true)
        assertTrue(capturedResult?.exceptionOrNull()?.message?.contains("Unexpected response") == true)
    }

    @Test
    fun `test failed transaction fetch - malformed JSON`() {
        val latch = CountDownLatch(1)
        var capturedResult: Result<String>? = null

        // Prepare malformed response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("invalid json")
        mockWebServer.enqueue(mockResponse)

        // Execute request
        transactionService.fetchTransactionInfo(testToken, testAmount) { result ->
            capturedResult = result
            latch.countDown()
        }

        // Wait for async operation
        assertTrue(latch.await(5, TimeUnit.SECONDS))

        // Verify response
        assertTrue(capturedResult?.isFailure == true)
        assertNotNull(capturedResult?.exceptionOrNull())
    }
}
