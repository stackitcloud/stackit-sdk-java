package cloud.stackit.sdk.iaas.api;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import java.io.IOException;

public class IaasApi extends DefaultApi {
	public IaasApi() throws IOException {
		super();
	}

	public IaasApi(CoreConfiguration configuration) throws IOException {
		super(configuration);
	}
}
