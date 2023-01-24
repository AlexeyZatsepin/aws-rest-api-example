package org.tui.testtask.api.tuitesttaskapi.mapping

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import org.tui.testtask.api.tuitesttaskapi.client.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.client.dto.Commit
import org.tui.testtask.api.tuitesttaskapi.client.dto.Owner
import org.tui.testtask.api.tuitesttaskapi.client.dto.RepositoryResponse

class RepositoryMapperTest {
    private val mapper = Mappers.getMapper(RepositoryMapper::class.java)

    @Test
    fun `should map github repositories response to entity`() {
        val branchesResponse = BranchesResponse(
            name="main",
            commit = Commit(
                sha = "12345"
            )
        )
        val repositoryResponse = RepositoryResponse(
            name = "test-repo",
            owner = Owner(
                login = "username"
            ),
            fork = false
        )
        val result = mapper.map(repositoryResponse, listOf(branchesResponse))

        assertEquals("test-repo", result.name)
        assertEquals("username", result.owner)
        assertNotNull(result.branches)
        result.branches!!.first().let {
            assertEquals("main", it.name)
            assertEquals("12345", it.sha)
        }
    }

    @Test
    fun `should map github branches response to entity`() {
        val response = BranchesResponse(
            name="main",
            commit = Commit(
                sha = "12345"
            )
        )
        val result = mapper.map(response)
        assertEquals("main", result.name)
        assertEquals("12345", result.sha)
    }
}