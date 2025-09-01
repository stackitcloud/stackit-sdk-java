# Contribute to the STACKIT Java SDK

Your contribution is welcome! Thank you for your interest in contributing to the STACKIT Java SDK. 
We greatly value your feedback, feature requests, additions to the code, bug reports or documentation extensions.

## Table of contents

- [Developer Guide](#developer-guide)
  - [Repository structure](#repository-structure)
  - [Implementing a module waiter](#implementing-a-module-waiter)
    - [Waiter structure](#waiter-structure)
    - [Notes](#notes)
- [Code Contributions](#code-contributions)
- [Bug Reports](#bug-reports)

## Developer Guide

Building the STACKIT Java SDK requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

In case you want to open the project in your preferred IDE, run `./gradlew idea` or `./gradlew eclipse` beforehand.

#### Useful Make commands

These commands can be executed from the project root:

- `make fmt` or `./gradlew spotlessApply`: apply code format.
- `make test` or `./gradlew test`: run unit tests. 

#### Installation

To install the API client library to your local Maven repository, simply execute:

```bash
./gradlew publishToMavenLocal
```

### Repository structure

The STACKIT Java SDK service submodules are located under `services`. 
The files located in `services/[service]` are automatically generated from the [REST API specs](https://github.com/stackitcloud/stackit-api-specifications), whereas the ones located in subfolders (like `wait`) are manually maintained. Therefore, changes to files located in `services/[service]` will not be accepted. Instead, consider proposing changes to the generation process in the [Generator repository](https://github.com/stackitcloud/stackit-sdk-generator).

Inside the `core` submodule you can find several classes that are used by all service modules. Examples of usage of the SDK are located in the `examples` directory.

### Implementing a service waiter

Waiters are routines that wait for the completion of asynchronous operations. They are located in a package named `wait` inside each service project.

Let's suppose you want to implement the waiters for the `Create`, `Update` and `Delete` operations of a resource `bar` of service `foo`:

1. Start by creating a new Java package `cloud.stackit.sdk.<service>.wait` inside `services/foo/` project, if it doesn't exist yet
2. Create a file `FooWait.java` inside your new Java package `cloud.stackit.sdk.resourcemanager.wait`, if it doesn't exist yet. The class should be named `FooWait`.
3. Refer to the [Waiter structure](./CONTRIBUTION.md/#waiter-structure) section for details on the structure of the file and the methods
4. Add unit tests to the wait functions

#### Waiter structure

You can find a typical waiter structure here: [Example](./services/resourcemanager/src/main/java/cloud/stackit/sdk/resourcemanager/wait/ResourcemanagerWait.java)

#### Notes

- The success condition may vary from service to service. In the example above we wait for the field `Status` to match a successful or failed message, but other services may have different fields and/or values to represent the state of the create, update or delete operations.
- The `id` and the `state` might not be present on the root level of the API response, this also varies from service to service. You must always match the resource `id` and the resource `state` to what is expected.
- The timeout values included above are just for reference, each resource takes different amounts of time to finish the create, update or delete operations. You should account for some buffer, e.g. 15 minutes, on top of normal execution times.
- For some resources, after a successful delete operation the resource can't be found anymore, so a call to the `Get` method would result in an error. In those cases, the waiter can be implemented by calling the `List` method and check that the resource is not present.
- The main objective of the waiter functions is to make sure that the operation was successful, which means any other special cases such as intermediate error states should also be handled.

## Code Contributions

To make your contribution, follow these steps:

1. Check open or recently closed [Pull Requests](https://github.com/stackitcloud/stackit-sdk-java/pulls) and [Issues](https://github.com/stackitcloud/stackit-sdk-java/issues) to make sure the contribution you are making has not been already tackled by someone else.
2. Fork the repo.
3. Make your changes in a branch that is up-to-date with the original repo's `main` branch.
4. Commit your changes including a descriptive message.
5. Create a pull request with your changes.
6. The pull request will be reviewed by the repo maintainers. If you need to make further changes, make additional commits to keep commit history. When the PR is merged, commits will be squashed.

## Bug Reports

If you would like to report a bug, please open a [GitHub issue](https://github.com/stackitcloud/stackit-sdk-java/issues/new).

To ensure we can provide the best support to your issue, follow these guidelines:

1. Go through the existing issues to check if your issue has already been reported.
2. Make sure you are using the latest version of the SDK modules, we will not provide bug fixes for older versions. Also, latest versions may have the fix for your bug.
3. Please provide as much information as you can about your environment, e.g. your version of Java, your version of the SDK modules, which operating system you are using and the corresponding version.
4. Include in your issue the steps to reproduce it, along with code snippets and/or information about your specific use case. This will make the support process much easier and efficient.

