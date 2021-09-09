package com.stho.mobipuzzle

import android.app.Application

object Dependencies {

    fun getRepository(application: Application): Repository {
        val persister: IPersister = Persister(application.applicationContext)
        return Repository.getInstance(persister)
    }

}