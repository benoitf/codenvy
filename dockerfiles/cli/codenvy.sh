#!/bin/bash
# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Tyler Jewell - Initial Implementation
#

init_host_ip() {
  GLOBAL_HOST_IP=${GLOBAL_HOST_IP:=$(docker_run --net host codenvy/che-ip:nightly)}
}

init_logging() {
  BLUE='\033[1;34m'
  GREEN='\033[0;32m'
  RED='\033[0;31m'
  YELLOW='\033[38;5;220m'
  NC='\033[0m'

  # Which che CLI version to run?
  DEFAULT_CHE_CLI_VERSION="latest"
  CHE_CLI_VERSION=${CHE_CLI_VERSION:-${DEFAULT_CHE_CLI_VERSION}}

  DEFAULT_CHE_PRODUCT_NAME="CODENVY"
  CHE_PRODUCT_NAME=${CHE_PRODUCT_NAME:-${DEFAULT_CHE_PRODUCT_NAME}}

  # Name used in CLI statements
  DEFAULT_CHE_MINI_PRODUCT_NAME="codenvy"
  CHE_MINI_PRODUCT_NAME=${CHE_MINI_PRODUCT_NAME:-${DEFAULT_CHE_MINI_PRODUCT_NAME}}

  # Turns on stack trace
  DEFAULT_CHE_CLI_DEBUG="false"
  CHE_CLI_DEBUG=${CHE_CLI_DEBUG:-${DEFAULT_CHE_CLI_DEBUG}}

  # Activates console output
  DEFAULT_CHE_CLI_INFO="true"
  CHE_CLI_INFO=${CHE_CLI_INFO:-${DEFAULT_CHE_CLI_INFO}}

  # Activates console warnings
  DEFAULT_CHE_CLI_WARN="true"
  CHE_CLI_WARN=${CHE_CLI_WARN:-${DEFAULT_CHE_CLI_WARN}}

  # Activates console output
  DEFAULT_CHE_CLI_LOG="true"
  CHE_CLI_LOG=${CHE_CLI_LOG:-${DEFAULT_CHE_CLI_LOG}}

  # Initialize CLI folder
  CLI_DIR=~/."${CHE_MINI_PRODUCT_NAME}"/cli
  test -d "${CLI_DIR}" || mkdir -p "${CLI_DIR}"

  # Initialize logging into a log file
  DEFAULT_CHE_CLI_LOGS_FOLDER="${CLI_DIR}"
  CHE_CLI_LOGS_FOLDER="${CHE_CLI_LOGS_FOLDER:-${DEFAULT_CHE_CLI_LOGS_FOLDER}}"

  # Ensure logs folder exists
  LOGS="${CHE_CLI_LOGS_FOLDER}/cli.log"
  mkdir -p "${CHE_CLI_LOGS_FOLDER}"
  # Log date of CLI execution
  log "$(date)"

  USAGE="
Usage: docker run -it --rm 
                  -v /var/run/docker.sock:/var/run/docker.sock
                  -v <host-path-for-codenvy-data>:/codenvy
                  ${CHE_MINI_PRODUCT_NAME}/$CHE_MINI_PRODUCT_NAME:<version> [COMMAND]

    help                                 This message
    version                              Installed version and upgrade paths
    init [--pull|--force|--offline]      Initializes a directory with a ${CHE_MINI_PRODUCT_NAME} configuration
    start [--pull|--force|--offline]     Starts ${CHE_MINI_PRODUCT_NAME} services
    stop                                 Stops ${CHE_MINI_PRODUCT_NAME} services
    restart [--pull|--force]             Restart ${CHE_MINI_PRODUCT_NAME} services
    destroy [--quiet]                    Stops services, and deletes ${CHE_MINI_PRODUCT_NAME} instance data
    rmi [--quiet]                        Removes the Docker images for CODENVY_VERSION, forcing a repull
    config                               Generates a ${CHE_MINI_PRODUCT_NAME} config from vars; run on any start / restart
    add-node                             Adds a physical node to serve workspaces intto the ${CHE_MINI_PRODUCT_NAME} cluster
    remove-node <ip>                     Removes the physical node from the ${CHE_MINI_PRODUCT_NAME} cluster
    upgrade                              Upgrades Codenvy from one version to another with migrations and backups
    download [--pull|--force|--offline]  Pulls Docker images for the current Codenvy version
    backup [--quiet|--skip-data] Backups ${CHE_MINI_PRODUCT_NAME} configuration and data to CODENVY_BACKUP_FOLDER
    restore [--quiet]                    Restores ${CHE_MINI_PRODUCT_NAME} configuration and data from CODENVY_BACKUP_FOLDER
    offline                              Saves ${CHE_MINI_PRODUCT_NAME} Docker images into TAR files for offline install
    info [ --all                         Run all debugging tests
           --debug                       Displays system information
           --network ]                   Test connectivity between ${CHE_MINI_PRODUCT_NAME} sub-systems

Variables:
    CODENVY_CONFIG                        Where the Codenvy config, CLI and variables are located
    CODENVY_INSTANCE                      Where ${CHE_MINI_PRODUCT_NAME} data, database, logs, are saved
    CODENVY_DEVELOPMENT_MODE              If 'on', then mounts host source folders into Docker images
    CODENVY_DEVELOPMENT_REPO              Location of host git repository that contains source code to be mounted
    CODENVY_BACKUP_FOLDER                 Location where backups files of installation are stored. Default = pwd
"
}

# Sends arguments as a text to CLI log file
# Usage:
#   log <argument> [other arguments]
log() {
  if is_log; then
    echo "$@" >> "${LOGS}"
  fi 
}

usage () {
  debug $FUNCNAME
  printf "%s" "${USAGE}"
  return 1;
}

warning() {
  if is_warning; then
    printf  "${YELLOW}WARN:${NC} %s\n" "${1}"
  fi
  log $(printf "WARN: %s\n" "${1}")
}

info() {
  if [ -z ${2+x} ]; then
    PRINT_COMMAND=""
    PRINT_STATEMENT=$1
  else
    PRINT_COMMAND="($CHE_MINI_PRODUCT_NAME $1):"
    PRINT_STATEMENT=$2
  fi
  if is_info; then
    printf "${GREEN}INFO:${NC} %s %s\n" \
              "${PRINT_COMMAND}" \
              "${PRINT_STATEMENT}"
  fi
  log $(printf "INFO: %s %s\n" \
        "${PRINT_COMMAND}" \
        "${PRINT_STATEMENT}")
}

debug() {
  if is_debug; then
    printf  "\n${BLUE}DEBUG:${NC} %s" "${1}"
  fi
  log $(printf "\nDEBUG: %s" "${1}")
}

error() {
  printf  "${RED}ERROR:${NC} %s\n" "${1}"
  log $(printf  "ERROR: %s\n" "${1}")
}

# Prints message without changes
# Usage: has the same syntax as printf command
text() {
  printf "$@"
  log $(printf "$@")
}

## TODO use that for all native calls to improve logging for support purposes
# Executes command with 'eval' command.
# Also logs what is being executed and stdout/stderr
# Usage:
#   cli_eval <command to execute>
# Examples:
#   cli_eval "$(which curl) http://localhost:80/api/"
cli_eval() {
  log "$@"
  tmpfile=$(mktemp)
  if eval "$@" &>"${tmpfile}"; then
    # Execution succeeded
    cat "${tmpfile}" >> "${LOGS}"
    cat "${tmpfile}"
    rm "${tmpfile}"
  else
    # Execution failed
    cat "${tmpfile}" >> "${LOGS}"
    cat "${tmpfile}"
    rm "${tmpfile}"
    fail
  fi
}

# Executes command with 'eval' command and suppress stdout/stderr.
# Also logs what is being executed and stdout+stderr
# Usage:
#   cli_silent_eval <command to execute>
# Examples:
#   cli_silent_eval "$(which curl) http://localhost:80/api/"
cli_silent_eval() {
  log "$@"
  eval "$@" >> "${LOGS}" 2>&1
}

is_log() {
  if [ "${CHE_CLI_LOG}" = "true" ]; then
    return 0
  else
    return 1
  fi
}

is_warning() {
  if [ "${CHE_CLI_WARN}" = "true" ]; then
    return 0
  else
    return 1
  fi
}

is_info() {
  if [ "${CHE_CLI_INFO}" = "true" ]; then
    return 0
  else
    return 1
  fi
}

is_debug() {
  if [ "${CHE_CLI_DEBUG}" = "true" ]; then
    return 0
  else
    return 1
  fi
}

has_docker() {
  hash docker 2>/dev/null && return 0 || return 1
}

has_compose() {
  hash docker-compose 2>/dev/null && return 0 || return 1
}

has_curl() {
  hash curl 2>/dev/null && return 0 || return 1
}

check_docker() {
  if ! has_docker; then
    error "Docker not found. Get it at https://docs.docker.com/engine/installation/."
    return 1;
  fi

  # If DOCKER_HOST is not set, then it should bind mounted
  if [ -z "${DOCKER_HOST+x}" ]; then
      if ! docker ps >> "${LOGS}" 2>&1; then
        info "Welcome to Codenvy!"
        info ""
        info "We did not detect a valid DOCKER_HOST." 
        info ""
        info "Rerun the CLI:"
        info "  docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock <local-path>:/codenvy codenvy/cli $@"    
        return 2;
      fi
  fi

  DATA_MOUNT=$(get_container_bind_folder)
  if [ "${DATA_MOUNT}" = "not set" ]; then
    info "Welcome to Codenvy!"
    info ""
    info "We did not detect a host mounted data directory." 
    info ""
    info "Rerun the CLI:"
    info "  docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock <local-path>:/codenvy codenvy/cli $@"    
    return 2;
  fi
}

get_container_bind_folder() {
  THIS_CONTAINER_ID=$(get_this_container_id)
  FOLDER=$(get_container_host_bind_folder ":/codenvy" $THIS_CONTAINER_ID)
  echo "${FOLDER:=not set}"
}

get_this_container_id() {
  hostname
}

get_container_host_bind_folder() {
  BINDS=$(docker inspect --format="{{.HostConfig.Binds}}" "${2}" | cut -d '[' -f 2 | cut -d ']' -f 1)
  IFS=$' '
  for SINGLE_BIND in $BINDS; do
    case $SINGLE_BIND in
      *$1)
        echo "${SINGLE_BIND}" | cut -f1 -d":"
      ;;
      *)
      ;;
    esac
  done
}

grab_offline_images(){
  # If you are using codenvy in offline mode, images must be loaded here
  # This is the point where we know that docker is working, but before we run any utilities
  # that require docker.
  if [ ! -z ${2+x} ]; then
    if [ "${2}" == "--offline" ]; then
      info "init" "Importing ${CHE_MINI_PRODUCT_NAME} Docker images from tars..."

      if [ ! -d offline ]; then
        info "init" "You requested offline loading of images, but could not find 'offline/'"
        return 2;
      fi

      IFS=$'\n'
      for file in "offline"/*.tar 
      do
        if ! $(docker load < "offline"/"${file##*/}" > /dev/null); then
          error "Failed to restore ${CHE_MINI_PRODUCT_NAME} Docker images"
          return 2;
        fi
        info "init" "Loading ${file##*/}..."
      done
    fi
  fi
}

grab_initial_images() {
  # Prep script by getting default image
  if [ "$(docker images -q alpine 2> /dev/null)" = "" ]; then
    info "cli" "Pulling image alpine:latest"
    log "docker pull alpine >> \"${LOGS}\" 2>&1"
    TEST=""
    docker pull alpine >> "${LOGS}" 2>&1 || TEST=$?
    if [ "$TEST" = "1" ]; then
      error "Image alpine unavailable. Not on dockerhub or built locally."
      return 1;
    fi
  fi

  if [ "$(docker images -q appropriate/curl 2> /dev/null)" = "" ]; then
    info "cli" "Pulling image curl:latest"
    log "docker pull appropriate/curl >> \"${LOGS}\" 2>&1"
    TEST=""
    docker pull appropriate/curl >> "${LOGS}" 2>&1 || TEST=$?
    if [ "$TEST" = "1" ]; then
      error "Image appropriate/curl unavailable. Not on dockerhub or built locally."
      return 1;
    fi
  fi

  if [ "$(docker images -q codenvy/che-ip:nightly 2> /dev/null)" = "" ]; then
    info "cli" "Pulling image eclipse/che-ip:nightly"
    log "docker pull codenvy/che-ip:nightly >> \"${LOGS}\" 2>&1"
    TEST=""
    docker pull codenvy/che-ip:nightly >> "${LOGS}" 2>&1 || TEST=$?
    if [ "$TEST" = "1" ]; then
      error "Image codenvy/che-ip:nightly unavailable. Not on dockerhub or built locally."
      return 1;
    fi
  fi

  if [ "$(docker images -q docker/compose:1.8.1 2> /dev/null)" = "" ]; then
    info "cli" "Pulling image docker/compose:1.8.1"
    log "docker pull docker/version >> \"${LOGS}\" 2>&1"
    TEST=""
    docker pull docker/compose:1.8.1 >> "${LOGS}" 2>&1 || TEST=$? 
    if [ "$TEST" = "1" ]; then
      error "Image docker/compose not found on dockerhub or locally."
      return 1;
    fi
  fi
}

check_volume_mount() {
  echo 'test' > /codenvy/test
  
  if [[ ! -f /codenvy/test ]]; then
    error "Docker installed, but unable to volume mount files from your host."
    error "Have you enabled Docker to allow mounting host directories?"
    return 1;
  fi

  rm -rf /codenvy/test 
}

docker_run() {
  debug $FUNCNAME
  # Setup options for connecting to docker host
  if [ -z "${DOCKER_HOST+x}" ]; then
      DOCKER_HOST="/var/run/docker.sock"
  fi

  if [ -S "$DOCKER_HOST" ]; then
    docker run --rm -v $DOCKER_HOST:$DOCKER_HOST \
                    -v $HOME:$HOME \
                    -w "$(pwd)" "$@"
  else
    docker run --rm -e DOCKER_HOST -e DOCKER_TLS_VERIFY -e DOCKER_CERT_PATH \
                    -v $HOME:$HOME \
                    -w "$(pwd)" "$@"
  fi
}

docker_compose() {
  debug $FUNCNAME

  if has_compose; then
    docker-compose "$@"
  else
    docker_run -v "${CODENVY_HOST_INSTANCE}":"${CODENVY_CONTAINER_INSTANCE}" \
                  docker/compose:1.8.1 "$@"
  fi
}

curl() {
  if ! has_curl; then
    log "docker run --rm --net=host appropriate/curl \"$@\""
    docker run --rm --net=host appropriate/curl "$@"
  else
    log "$(which curl) \"$@\""
    $(which curl) "$@"
  fi
}

get_script_source_dir() {
  SOURCE="${BASH_SOURCE[0]}"
  while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P '$( dirname \"$SOURCE\" )' && pwd )"
    SOURCE="$(readlink '$SOURCE')"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
  done
  echo "$( cd -P "$( dirname "$SOURCE" )" && pwd )"
}

init() {
  init_logging

  if [[ $# == 0 ]]; then
    usage;
  fi

  check_docker "$@"
  grab_offline_images
  grab_initial_images
  check_volume_mount

  source /cli/cli.sh
}

# See: https://sipb.mit.edu/doc/safe-shell/
set -e
set -u

# Initialize the self-updating CLI - this is a common code between Che & Codenvy.
init "$@"

# Begin product-specific CLI calls
info "cli" "Loading cli..."
cli_init "$@"
cli_parse "$@"
cli_cli "$@"
