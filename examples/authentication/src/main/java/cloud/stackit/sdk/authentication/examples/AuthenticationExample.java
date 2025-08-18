package cloud.stackit.sdk.authentication.examples;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;
import cloud.stackit.sdk.resourcemanager.model.ListOrganizationsResponse;

class AuthenticationExample {
	public static void main(String[] args) {
		String SERVICE_ACCOUNT_KEY_PATH = "/path/to/your/sa/key.json";
		String SERVICE_ACCOUNT_MAIL = "name-1234@sa.stackit.cloud";

		CoreConfiguration config =
				new CoreConfiguration().serviceAccountKeyPath(SERVICE_ACCOUNT_KEY_PATH);

		try {
			DefaultApi api = new DefaultApi(config);

			/* list all organizations */
			ListOrganizationsResponse response =
					api.listOrganizations(null, SERVICE_ACCOUNT_MAIL, null, null, null);

			System.out.println(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
