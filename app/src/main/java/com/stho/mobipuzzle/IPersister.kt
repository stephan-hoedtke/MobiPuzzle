package com.stho.mobipuzzle

interface IPersister {
    fun load(): Repository
    fun save(repository: Repository)
}
