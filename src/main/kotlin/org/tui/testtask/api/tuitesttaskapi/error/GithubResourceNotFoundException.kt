package org.tui.testtask.api.tuitesttaskapi.error

class GithubResourceNotFoundException(override val message:String) : Exception(message) {
}