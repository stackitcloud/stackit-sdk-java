package cloud.stackit.sdk.resourcemanager;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;
import cloud.stackit.sdk.resourcemanager.model.ListOrganizationsResponse;

import javax.security.auth.login.CredentialNotFoundException;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

public class main {
    public static void main(String[] args) throws IOException, InvalidKeySpecException, ApiException, cloud.stackit.sdk.core.exception.ApiException {
        String SERVICE_ACCOUNT_KEY_PATH = "/path/to/your/sa/key.json";
        String SERIVCE_ACCOUNT_MAIL = "name-1234@sa.stackit.cloud";

        CoreConfiguration config = new CoreConfiguration
                .Builder()
                .serviceAccountKeyPath(SERVICE_ACCOUNT_KEY_PATH)
                .build();
        DefaultApi api = new DefaultApi(config);

        ListOrganizationsResponse response = api.listOrganizations(null, SERIVCE_ACCOUNT_MAIL, null, null, null);

        System.out.println(response);
    }
}
