package cloud.stackit.sdk.authentication.examples;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.ListOrganizationsResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

class AuthenticationExample {
	public static void main(String[] args) throws IOException {
		///////////////////////////////////////////////////////
		// Option 1: setting the paths to service account key (and private key) as configuration
		///////////////////////////////////////////////////////
		String SERVICE_ACCOUNT_KEY_PATH = "/path/to/sa_key.json";
		String PRIVATE_KEY_PATH = "/path/to/private_key.pem";
		String SERVICE_ACCOUNT_MAIL = "name-1234@sa.stackit.cloud";

		try {
			ResourceManagerApi api =
					new ResourceManagerApi(
							new CoreConfiguration()
									.serviceAccountKeyPath(SERVICE_ACCOUNT_KEY_PATH)
									// Optional: if private key not included in service account key
									.privateKeyPath(PRIVATE_KEY_PATH));

			/* list all organizations */
			ListOrganizationsResponse response =
					api.listOrganizations(null, SERVICE_ACCOUNT_MAIL, null, null, null);

			System.out.println(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		///////////////////////////////////////////////////////
		// Option 2: setting the service account key (and private key) as configuration
		///////////////////////////////////////////////////////

		// read key content from a file, in production you can also read it e.g. from STACKIT
		// secrets manager, so it's only kept in-memory
		String serviceAccountKeyPath = // replace it with the path to your service account key
				"examples/authentication/src/main/java/cloud/stackit/sdk/authentication/examples/dummy_credentials/dummy-service-account-key.json";
		File serviceAccountKeyFile = new File(serviceAccountKeyPath);
		StringBuilder serviceAccountKeyContent = new StringBuilder();
		try (Scanner myReader = new Scanner(serviceAccountKeyFile)) {
			while (myReader.hasNextLine()) {
				serviceAccountKeyContent.append(myReader.nextLine());
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		String privateKeyPath = // replace it with the path to your private key
				"examples/authentication/src/main/java/cloud/stackit/sdk/authentication/examples/dummy_credentials/dummy-private-key.pem";
		File privateKeyFile = new File(privateKeyPath);
		StringBuilder privateKeyContent = new StringBuilder();
		try (Scanner myReader = new Scanner(privateKeyFile)) {
			while (myReader.hasNextLine()) {
				privateKeyContent.append(myReader.nextLine());
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		String SERVICE_ACCOUNT_KEY = serviceAccountKeyContent.toString();
		String PRIVATE_KEY = privateKeyContent.toString();

		try {
			ResourceManagerApi api =
					new ResourceManagerApi(
							new CoreConfiguration()
									.serviceAccountKey(SERVICE_ACCOUNT_KEY)
									// Optional: if private key not included in service account key
									.privateKey(PRIVATE_KEY));

			/* list all organizations */
			ListOrganizationsResponse response =
					api.listOrganizations(null, SERVICE_ACCOUNT_MAIL, null, null, null);

			System.out.println(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		///////////////////////////////////////////////////////
		// Option 3: setting the service account key (and private key) as environment variable
		///////////////////////////////////////////////////////
		// Set the service account key via environment variable:
		// - STACKIT_SERVICE_ACCOUNT_KEY_PATH=/path/to/sa_key.json
		// - STACKIT_SERVICE_ACCOUNT_KEY="<content of service account key>"
		//
		// If the private key is not included in the service account key, set also:
		// - STACKIT_PRIVATE_KEY_PATH=/path/to/private_key.pem
		// - STACKIT_PRIVATE_KEY="<content of private key>"
		//
		// If no environment variable is set, fallback to credentials file in
		// "$HOME/.stackit/credentials.json".
		// Can be overridden with the environment variable `STACKIT_CREDENTIALS_PATH`
		// The credentials file must be a json:
		//   {
		//     "STACKIT_SERVICE_ACCOUNT_KEY_PATH": "path/to/sa_key.json",
		//     "STACKIT_PRIVATE_KEY_PATH": "(OPTIONAL) when the private key isn't included in the
		// Service Account key",
		//     // Alternative:
		//     "STACKIT_SERVICE_ACCOUNT_KEY": "<content of private key>",
		//     "STACKIT_PRIVATE_KEY": "(OPTIONAL) when the private key isn't included in the Service
		// Account key",
		//   }
		try {
			ResourceManagerApi api = new ResourceManagerApi();

			/* list all organizations */
			ListOrganizationsResponse response =
					api.listOrganizations(null, SERVICE_ACCOUNT_MAIL, null, null, null);

			System.out.println(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
