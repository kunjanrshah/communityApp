package com.krs.community.graphql

import com.apollographql.apollo3.ApolloClient

object GraphQLClientProvider {
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("http://localhost:3000/graphql")
            .build()
    }
}
