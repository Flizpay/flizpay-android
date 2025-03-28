package flizpay2.flizpaysdk

import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import flizpay2.flizpaysdk.lib.WebViewBridge
import io.mockk.*
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class WebViewBridgeTest {
    private lateinit var mockWebView: WebView
    private lateinit var webViewBridge: WebViewBridge
    private lateinit var mockContext: AppCompatActivity
    private val closeFlag = AtomicBoolean(false)

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockWebView = mockk(relaxed = true)
        webViewBridge = WebViewBridge(mockWebView, mockContext)

        every { mockContext.startActivity(any()) } just runs
        every { mockContext.finish() } answers { closeFlag.set(true) }
    }

    @Test
    fun test_JavaScript_interface_is_added_on_initialization() {
        verify {
            mockWebView.addJavascriptInterface(
                webViewBridge,
                "AndroidBridge"
            )
        }
    }

    @Test
    fun test_closeWebView_triggers_onClose_callback() {
        // Initial state should be false
        assert(!closeFlag.get())

        // Call closeWebView
        webViewBridge.closeWebView()

        // Verify callback was triggered
        assert(closeFlag.get())
    }

    @Test
    fun test_JavascriptInterface_annotation_is_present() {
        val method = WebViewBridge::class.java.getMethod("closeWebView")
        val annotation = method.getAnnotation(android.webkit.JavascriptInterface::class.java)
        assert(annotation != null)
    }
}