package cloud.stackit.sdk.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import cloud.stackit.sdk.core.exception.ApiException;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyFlowInterceptorTest {

	@Mock private KeyFlowAuthenticator authenticator;
	//    private KeyFlowInterceptor interceptor;
	private MockWebServer mockWebServer;
	private OkHttpClient client;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		client =
				new OkHttpClient.Builder()
						.addInterceptor(new KeyFlowInterceptor(authenticator))
						.build();
	}

	@AfterEach
	void teardown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void intercept_addsAuthHeader()
			throws IOException, InvalidKeySpecException, ApiException, InterruptedException {
		final String accessToken = "my-access-token";
		when(authenticator.getAccessToken()).thenReturn(accessToken);

		mockWebServer.enqueue(new MockResponse().setResponseCode(200));

		// Make request
		Request request = new Request.Builder().url(mockWebServer.url("/test")).build();
		client.newCall(request).execute();

		RecordedRequest recordedRequest = mockWebServer.takeRequest();

		String expectedAuthHeader = "Bearer " + accessToken;
		assertEquals(expectedAuthHeader, recordedRequest.getHeader("Authorization"));
	}
}
