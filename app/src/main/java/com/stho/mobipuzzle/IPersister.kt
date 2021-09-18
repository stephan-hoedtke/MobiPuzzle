package com.stho.mobipuzzle

interface IPersister {
    fun loadRepository(): Repository
    fun save(repository: Repository)
}
