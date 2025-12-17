<div align="center">
<br>
<img src=".github/images/stackit-logo.svg" alt="STACKIT logo" width="50%"/>
<br>
<br>
</div>

# STACKIT SDK for Java (BETA)

[![GitHub License](https://img.shields.io/github/license/stackitcloud/stackit-sdk-java)](https://www.apache.org/licenses/LICENSE-2.0)
[![CI/CD](https://github.com/stackitcloud/stackit-sdk-java/actions/workflows/ci.yaml/badge.svg?branch=main)](https://github.com/stackitcloud/stackit-sdk-java/actions/workflows/ci.yaml)

This repository contains the STACKIT SDKs for Java.

## Getting started

Requires Java 8 or higher.

The release artifacts of the STACKIT Java SDK are available on [Maven Central](https://central.sonatype.com/namespace/cloud.stackit.sdk).
See below how to use them in your Java project.

### Maven

Add the dependencies for the services you want to interact with to your project's POM, e.g. `iaas` and `resourcemanager` (replace `<SDK_VERSION>` with the latest version of each SDK submdoule):

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>iaas</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>resourcemanager</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle

Add the dependencies to your project's build file (replace `<SDK_VERSION>` with the latest version of each SDK submdoule):

```groovy
  repositories {
    mavenCentral()
  }

  dependencies {
     // add the dependencies of the services you want to interact with here,
     // e.g. "iaas" and "resourcemanager"
     implementation "cloud.stackit.sdk:iaas:<SDK_VERSION>"
     implementation "cloud.stackit.sdk:resourcemanger:<SDK_VERSION>"
  }
```

## Examples

Examples on services, configuration and authentication possibilities can be found in the [examples folder](/examples).

> [!WARNING]
> For production usage, especially when working with multiple STACKIT SDK modules, consider passing your own `OkHttpClient` 
> object (as recommended in the [OkHttpClient lib docs](https://square.github.io/okhttp/3.x/okhttp/index.html?okhttp3/OkHttpClient.html)).
> See our [custom HTTP client example](/examples/custom-http-client/src/main/java/cloud/stackit/sdk/customhttpclient/examples/CustomHttpClientExample.java) for reference.

## Authorization

To authenticate to the SDK, you will need a [service account](https://docs.stackit.cloud/platform/access-and-identity/service-accounts/). Create it in the STACKIT Portal and assign it the necessary permissions, e.g. `project.owner`.

The Java SDK supports only Key flow for authentication.

When setting up authentication, the SDK will search for credentials in several locations, following a specific order:

1. Explicit configuration, e.g. by using the option `new CoreConfiguration().serviceAccountKeyPath("path/to/sa_key.json")`
2. Environment variable, e.g. by setting `STACKIT_SERVICE_ACCOUNT_KEY_PATH`
3. Credentials file

   The SDK will check the credentials file located in the path defined by the `STACKIT_CREDENTIALS_PATH` env var, if specified,
   or in `$HOME/.stackit/credentials.json` as a fallback.
   The credentials file should be a json and each credential should be set using the name of the respective environment variable, as stated below in each flow. Example:

   ```json
   {
     "STACKIT_SERVICE_ACCOUNT_KEY_PATH": "path/to/sa_key.json",
     "STACKIT_PRIVATE_KEY_PATH": "(OPTIONAL) when the private key isn't included in the Service Account key"
   }
   ```

### Example

The following instructions assume that you have created a service account and assigned it the necessary permissions, e.g. `project.owner`.

To use the key flow, you need to have a service account key, which must have an RSA key-pair attached to it.

When creating the service account key, a new pair can be created automatically, which will be included in the service account key.
This will make it much easier to configure the key flow authentication in the SDK, by just providing the service account key.

> **Optionally**, you can provide your own private key when creating the service account key, which will then require you to also provide it explicitly to the SDK, additionally to the service account key.
> Check the STACKIT Docs for an [example of how to create your own key-pair](https://docs.stackit.cloud/platform/access-and-identity/service-accounts/how-tos/manage-service-account-keys/).

To configure the key flow, follow this steps:

1. Create a service account key:
   - Use the STACKIT Portal: go to the `Service Accounts` tab, choose a `Service Account` and go to `Service Account Keys` to create a key. For more details, see [Create a service account key](https://docs.stackit.cloud/platform/access-and-identity/service-accounts/how-tos/manage-service-account-keys/).
2. Save the content of the service account key by copying it and saving it in a JSON file. The expected format of the service account key is **JSON** with the following structure:

   ```json
   {
     "id": "uuid",
     "publicKey": "public key",
     "createdAt": "2023-08-24T14:15:22Z",
     "validUntil": "2023-08-24T14:15:22Z",
     "keyType": "USER_MANAGED",
     "keyOrigin": "USER_PROVIDED",
     "keyAlgorithm": "RSA_2048",
     "active": true,
     "credentials": {
       "kid": "string",
       "iss": "my-sa@sa.stackit.cloud",
       "sub": "uuid",
       "aud": "string",
       "privateKey": "(OPTIONAL) private key when generated by the SA service"
     }
   }
   ```

3. Configure the service account key for authentication in the SDK by following one of the alternatives below:

   - using the configuration options:

     ```java
     CoreConfiguration config =
                new CoreConfiguration()
                        ...
                        .serviceAccountKeyPath("/path/to/service_account_key.json");

     ResourceManagerApi api = new ResourceManagerApi(config);
     ```

   - setting the environment variable: `STACKIT_SERVICE_ACCOUNT_KEY_PATH`
   - setting `STACKIT_SERVICE_ACCOUNT_KEY_PATH` in the credentials file (see above)

> **Optionally, only if you have provided your own RSA key-pair when creating the service account key**, you also need to configure your private key (takes precedence over the one included in the service account key, if present). **The private key must be PEM encoded** and can be provided using one of the options below:
>
> - using the configuration options:
>   ```java
>     CoreConfiguration config =
>                new CoreConfiguration()
>                        ...
>                        .privateKeyPath("/path/to/private_key.pem");
>   ```
> - setting the environment variable: `STACKIT_PRIVATE_KEY_PATH`
> - setting `STACKIT_PRIVATE_KEY_PATH` in the credentials file (see above)

> **Alternatively, if you can't store the credentials in a file, e.g. when using it in a pipeline**, you can store the credentials in environment variables:
>
> - setting the environment variable `STACKIT_SERVICE_ACCOUNT_KEY` with the content of the service account key
> - (OPTIONAL) setting the environment variable `STACKIT_PRIVATE_KEY` with the content of the private key

4. The SDK will search for the keys and, if valid, will use them to get access and refresh tokens which will be used to authenticate all the requests.

Check the [authentication example](examples/authentication/src/main/java/cloud/stackit/sdk/authentication/examples/AuthenticationExample.java) for more details.

## Using custom endpoints

The example below shows how to use the STACKIT Java SDK in custom STACKIT enviroments.

```java
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.ListOrganizationsResponse;

import java.io.IOException;

class CustomEndpointExample {
    public static void main(String[] args) {
        CoreConfiguration config =
                new CoreConfiguration()
                        .serviceAccountKey("/path/to/sa_key.json")
                        .customEndpoint("https://resource-manager.api.stackit.cloud")
                        .tokenCustomUrl("https://service-account.api.stackit.cloud/token");

        try {
            ResourceManagerApi resourceManagerApi = new ResourceManagerApi(config);

            /* list all organizations */
            ListOrganizationsResponse response =
                    resourceManagerApi.listOrganizations(
                            null,
                            "service-account-mail@sa.stackit.cloud",
                            null,
                            null,
                            null
                    );

            System.out.println(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

## Reporting issues

If you encounter any issues or have suggestions for improvements, please open an issue in the repository or create a ticket in the [STACKIT Help Center](https://support.stackit.cloud/).

## Contribute

Your contribution is welcome! For more details on how to contribute, refer to our [Contribution Guide](./CONTRIBUTION.md).

## Release creation

See the [release documentation](./RELEASE.md) for further information.

## License

Apache 2.0
