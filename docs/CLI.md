## CLI Reference
The CLI logs command execution inside of its runtime container into `~/.codenvy/cli/cli.logs`.  

The CLI is configured to hide most error conditions from the output screen. If you believe that Codenvy or the CLI is starting with errors, the `cli.logs` file will have all of the traces and error output from your executions.

### `codenvy init`
Initializes an empty directory with a Codenvy configuration and instance folder where user data and runtime configuration will be stored. If you only provide a `<path>:/codenvy` volume mount, then Codenvy creates a `instance`, `config`, and `backup` subfolder of `<path>`. If you provide three volume mounts of `<path-1>:/codenvy/config`, `<path-2>:/codenvy/instance`, `<path-3>:/codenvy/backup` then these specific folders will be used instead of the subfolders approach. The `codenvy.env` file is placed into the `/codenvy/config` folder, which is the file you use to configure how Codenvy is configured and run. Other files in this folder are used by Codenvy's configuration system to structure the runtime microservices. 

These variables can be set in your local environment shell before running and they will be respected during initialization and inserted as defaults into `/codenvy/config/codenvy.ver`:

| Variable | Description |
|----------|-------------|
| `CODENVY_HOST` | The IP address or DNS name of the Codenvy service. We use `codenvy/che-ip` to attempt discovery if not set. |
| `CODENVY_DEVELOPMENT_MODE` | If `on`, then will mount `CODENVY_DEVELOPMENT_REPO`, overriding the files in Codenvy config and containers. |
| `CODENVY_DEVELOPMENT_REPO` | The location of the `http://github.com/codenvy/codenvy` local clone. |

Codenvy depends upon Docker images. We use Docker images in three ways:
1. As cross-platform utilites within the CLI. For example, in scenarios where we need to perform a `curl` operation, we use a small Docker image to perform this function. We do this as a precaution as many operating systems (like Windows) do not have curl installed.
2. To look up the master version and upgrade manifest, which is stored as a singleton Docker image called `codenvy/version`. 
3. To perform initialization and configuration of Codenvy such as with `codenvy/init`. This image contains templates that are delivered as a payload and installed onto your computer. These payload images can have different files based upon the image's version.
4. To run Codenvy and its dependent services, which include Codenvy, HAproxy, nginx, Postgres, socat, and Docker Swarm.

You can control the nature of how Codenvy downloads these images with command line options. All image downloads are performed with `docker pull`. 

| Mode>>>> | Description |
|------|-------------|
| `--no-force` | Default behavior. Will download an image if not found locally. A local check of the image will see if an image of a matching name is in your local registry and then skip the pull if it is found. This mode does not check DockerHub for a newer version of the same image. |
| `--pull` | Will always perform a `docker pull` when an image is requested. If there is a newer version of the same tagged image at DockerHub, it will pull it, or use the one in local cache. This keeps your images up to date, but execution is slower. |
| `--force` | Performs a forced removal of the local image using `docker rmi` and then pulls it again (anew) from DockerHub. You can use this as a way to clean your local cache and ensure that all images are new. |
| `--offline` | Loads Docker images from `offline/*.tar` folder during a pre-boot mode of the CLI. Used if you are performing an installation or start while disconnected from the Internet. |

### `codenvy config`
Generates a Codenvy instance configuration using the templates and environment variables stored in `/codenvy/config` and places the configuration in `/codenvy/instance`. Uses puppet to generate the configuration files for Codenvy, haproxy, swarm, socat, nginx, and postgres which are mounted when Codenvy services are started. This command is executed on every `start` or `restart`.

If you are using a `codenvy/cli:<version>` image and it does not match the version that is in `/codenvy/instance/codenvy.ver`, then the configuration will abort to prevent you from running a configuration for a different version than what is currently installed.

This command respects `--no-force`, `--pull`, `--force`, and `--offline`.

### `codenvy start`
Starts Codenvy and its services using `docker-compose`. If the system cannot find a valid `/codenvy/config` and `/codenvy/instance` it will perform a `codenvy init`. Every `start` and `restart` will run a `codenvy config` to generate a new configuration set using the latest configuration. The starting sequence will perform pre-flight testing to see if any ports required by Codenvy are currently used by other services and post-flight checks to verify access to key APIs.  

### `codenvy stop`
Stops all of the Codenvy service containers and removes them.

### `codenvy restart`
Performs a `codenvy stop` followed by a `codenvy start`, respecting `--pull`, `--force`, and `--offline`.

### `codenvy destroy`
Deletes `/codenvy/config` and `/codenvy/instance`, including destroying all user workspaces, projects, data, and user database. If you provide `--force` then the confirmation warning will be skipped.

### `codenvy offline`
Saves all of the Docker images that Codenvy requires into `/codenvy/backup/*.tar` files. Each image is saved as its own file. If the `backup` folder is available on a machine that is disconnected from the Internet and you start Codenvy with `--offline`, the CLI pre-boot sequence will load all of the Docker images in the `/codenvy/backup/` folder.

### `codenvy rmi`
Deletes the Docker images from the local registry that Codenvy has downloaded for this version.

### `codenvy download`
Used to download Docker images that will be stored in your Docker images repository. This command downloads images that are used by the CLI as utilities, for Codenvy to do initialization and configuration, and for the runtime images that Codenvy needs when it starts.  This command respects `--offline`, `--pull`, `--force`, and `--no-force` (default).  This command is invoked by `codenvy init`, `codenvy config`, and `codenvy start`.

This command is invoked by `codenvy init` before initialization to download the images for the version specified by `codenvy/cli:<version>`.

### `codenvy version`
Provides information on the current version, the available versions that are hosted in Codenvy's repositories, and if you have a `/codenvy/instance`, then also the available upgrade paths. `codenvy upgrade` enforces upgrade sequences and will prevent you from upgrading one version to another version where data migrations cannot be guaranteed.

### `codenvy upgrade`
Manages the sequence of upgrading Codenvy from one version to another. Run `codenvy version` to get a list of available versions that you can upgrade to.

Do *not* upgrade by wiping your Codenvy images and using a new version. There is a possibility that you will corrupt your system. We have multiple checks that will stop you from starting Codenvy if the `codenvy/cli:<version>` differs from the one that is in `/codenvy/instance/codenvy.ver`.  In some releases, we change the underlying database schema model, and we need to run internal migration scripts that transforms the old data model into the new format. The `codenvy upgrade` function ensures that you are upgrading to a supported version where a clean data migration for your existing database can be completed.

### `codenvy info`
Displays system state and debugging information. `--network` runs a test to take your `CODENVY_HOST` value to test for networking connectivity simulating browser > Codenvy and Codenvy > workspace connectivity.

### `codenvy backup`
Tars both your `/codenvy/config` and `/codenvy/instance` into files and places them into `/codenvy/backup`. These files are restoration-ready.

### `codenvy restore`
Restores `/codenvy/config` and `/codenvy/instance` to their previous state. You do not need to worry about having the right Docker images. The normal start / stop / restart cycle ensures that the proper Docker images are available or downloaded, if not found.

This command will destroy your existing `/codenvy/config` and `/codenvy/instance` folders, so use with caution, or set these values to different folders when performing a restore.

### `codenvy add-node`
Adds a new physical node into the Codenvy cluster. That node must have Docker pre-configured similar to how you have Docker configured on the master node, including any configurations that you add for proxies or an alternative key-value store like Consul. Codenvy generates an automated script that can be run on each new node which prepares the node by installing some dependencies, adding the Codenvy SSH key, and registering itself within the Codenvy cluster.

### `codenvy remove-node`
Takes a single parameter, `ip`, which is the external IP address of the remote physical node to be removed from the Codenvy cluster. This utility does not remove any software from the remote node, but it does ensure that workspace runtimes are not executing on that node. 
