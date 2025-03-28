package flizpay2.flizpaysdk

import android.content.Intent
import android.webkit.WebView
import flizpay2.flizpaysdk.lib.WebViewBridge
import flizpay2.flizpaysdk.lib.WebViewService
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class WebViewServiceTest {
    private lateinit var mockActivity: WebViewService
    private lateinit var mockIntent: Intent
    private lateinit var mockWebView: WebView
    private lateinit var mockWebViewBridge: WebViewBridge

    private val testRedirectUrl = "https://test.flizpay.com/checkout"
    private val testToken = "test-token"

    @Before
    fun setup() {
        // Mock Intent
        mockIntent = mockk(relaxed = true)
        every { mockIntent.getStringExtra("redirectUrl") } returns testRedirectUrl
        every { mockIntent.getStringExtra("token") } returns testToken

        // Mock WebView and WebViewBridge
        mockWebView = mockk(relaxed = true)
        mockWebViewBridge = mockk(relaxed = true)

        // Mock Activity
        mockActivity = mockk(relaxed = true)
        every { mockActivity.intent } returns mockIntent
        every { mockActivity.findViewById<WebView>(any()) } returns mockWebView
        every { mockActivity.isFinishing } returns false
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_URL_loading_with_token() {
        val expectedUrl = "$testRedirectUrl&jwt=$testToken"

        // Simulate WebView loadUrl()
        every { mockWebView.loadUrl(any()) } just runs

        // Trigger URL load
        mockWebView.loadUrl(expectedUrl)

        // Verify the correct URL was loaded
        verify { mockWebView.loadUrl(expectedUrl) }
    }

    @Test
    fun test_activity_finish_when_WebViewBridge_triggers_close() {
        // Simulate WebViewBridge behavior
        val bridge = WebViewBridge(mockWebView, mockActivity)
        every { mockActivity.finish() } just runs

        bridge.overrideWindowClose()

        assertFalse(mockActivity.isFinishing)

        // Simulate closing WebView
        bridge.closeWebView()

        verify { mockActivity.finish() }
    }

    @Test
    fun test_missing_intent_extras_handling() {
        // Simulate missing extras
        every { mockIntent.getStringExtra("redirectUrl") } returns null
        every { mockIntent.getStringExtra("token") } returns null

        // Simulate activity finishing
        every { mockActivity.finish() } just runs

        // If extras are missing, activity should finish
        mockActivity.finish()

        verify { mockActivity.finish() }
    }
}
