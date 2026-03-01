package cloud.stackit.sdk.customhttpclient.examples;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.iaas.api.IaasApi;
import cloud.stackit.sdk.iaas.model.*;
import java.io.IOException;
import java.util.UUID;
import okhttp3.OkHttpClient;

/*
 * This example shows how to pass an existing OkHttpClient object to the STACKIT SDKs ApiClient.
 *
 * The example shows how to set the authorization header in the OkHttpClient object (required!).
 *
 * NOTE: Passing the http client is optional, see our other examples
 * where no OkHttpClient object is passed.
 * In this case the STACKIT SDK ApiClients will just create their own OkHttpClient objects.
 * Nevertheless, for production usage try to use one single OkHttpClient object
 * for everything to take advantage of the shared connection pool and to prevent resource leaks.
 *
 * */
final class CustomHttpClientExample {

	private CustomHttpClientExample() {}

	@SuppressWarnings({"PMD.SystemPrintln", "PMD.AvoidThrowingRawExceptionTypes"})
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		CoreConfiguration configuration = new CoreConfiguration();

		OkHttpClient httpClient = new OkHttpClient();
		KeyFlowAuthenticator authenticator = new KeyFlowAuthenticator(httpClient, configuration);
		httpClient = httpClient.newBuilder().authenticator(authenticator).build();

		// Pass the custom http client
		IaasApi iaasApi = new IaasApi(httpClient, configuration);

		// the id of your STACKIT project, read from env var for this example
		String projectIdString = System.getenv("STACKIT_PROJECT_ID");
		if (projectIdString == null || projectIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}
		UUID projectId = UUID.fromString(projectIdString);

		// specify which region should be queried
		String region = "eu01";

		try {
			/* list all servers */
			ServerListResponse servers = iaasApi.listServers(projectId, region, false, null);
			System.out.println("\nAvailable servers: ");
			for (Server server : servers.getItems()) {
				System.out.println("* " + server.getId() + " | " + server.getName());
			}
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}
}
