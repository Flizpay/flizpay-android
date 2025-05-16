package flizpay2.flizpaysdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import flizpay2.flizpaysdk.lib.TransactionService
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class FlizpaySDKTest {
    private lateinit var mockContext: AppCompatActivity
    private lateinit var mockTransactionService: TransactionService
    private lateinit var mockIntent: Intent
    private val testToken = "test-token"
    private val testAmount = "100.00"
    private val testRedirectUrl = "https://test.url"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockContext = mockk(relaxed = true)
        mockIntent = mockk(relaxed = true)
        mockTransactionService = mockk(relaxed = true)

        // Mock TransactionService
        mockkConstructor(TransactionService::class)
        every {
            anyConstructed<TransactionService>().fetchTransactionInfo(
                any(),
                any(),
                any(),
                captureLambda()
            )
        } answers {
            lambda<(Result<String>) -> Unit>().captured.invoke(Result.success(testRedirectUrl))
        }

        // Mock Intent
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(any(), any<String>()) } returns mockIntent
        every { anyConstructed<Intent>().addFlags(any()) } returns mockIntent
        every { mockIntent.toString() } returns "Mock Intent"
    }

    @Test
    fun test_successful_payment_initiation() {
        // Execute
        FlizpaySDK.initiatePayment(mockContext, testToken, testAmount)

        // Verify
        verify { mockContext.startActivity(any()) }
    }

    @Test
    fun test_payment_initiation_failure() {
        val errorMessage = "Test error"
        var capturedError: Throwable? = null

        // Override default success mock with failure
        every {
            anyConstructed<TransactionService>().fetchTransactionInfo(
                any(),
                any(),
                any(),
                captureLambda()
            )
        } answers {
            lambda<(Result<String>) -> Unit>().captured.invoke(
                Result.failure(Exception(errorMessage))
            )
        }

        // Execute
        FlizpaySDK.initiatePayment(mockContext, testToken, testAmount) { error ->
            capturedError = error
        }

        // Verify
        assertTrue(capturedError?.message == errorMessage)
        verify(exactly = 0) { mockContext.startActivity(any()) }
    }

    @Test
    fun test_activity_context_adds_new_task_flag() {
        val intentSlot = slot<Intent>()

        // Mock startActivity for Application
        every { mockContext.startActivity(capture(intentSlot)) } just Runs

        // Execute
        FlizpaySDK.initiatePayment(mockContext, testToken, testAmount)

        val capturedIntent = intentSlot.captured

        // Verify FLAG_ACTIVITY_NEW_TASK is set
        assertTrue(capturedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }
}
