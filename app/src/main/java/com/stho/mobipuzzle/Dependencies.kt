package com.stho.mobipuzzle

import android.app.Application


fun Application.getRepository(): Repository {
    val persister: IPersister = Persister(this.applicationContext)
    return Repository.getInstance(persister)
}

fun Application.save() {
    val persister: IPersister = Persister(this.applicationContext)
    val repository = Repository.getInstance(persister)
    persister.save(repository)
}

